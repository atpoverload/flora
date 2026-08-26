"""a thin client to talk to a flora server."""

import json
import os
import sys
import time
from argparse import ArgumentParser

import bpy
import numpy as np
import pyRAPL
from collector import DataCollector
from flora_client import FloraRenderingProbemClient
from jcarbon.nvml.sampler import NvmlSampler
from jcarbon.report import to_dataframe
from PIL import Image
from pypiqe import piqe

ENERGY_SIGNAL = "nvmlDeviceGetTotalEnergyConsumption"


def create_scene(scene_path):
    print(f"Loading scene from: {scene_path}")
    bpy.ops.wm.open_mainfile(filepath=scene_path)

    # Get the current scene
    scene = bpy.context.scene

    # ---- Fix Color Management ----
    try:
        scene.display_settings.display_device = "sRGB"
        scene.view_settings.view_transform = "Standard"
        scene.view_settings.look = "None"
        scene.view_settings.exposure = 0.0
        print("Color management settings applied successfully.")
    except Exception as e:
        print(f"Warning: Failed to apply color management settings: {e}")

    return scene


def create_output_dir(output_dir, scene_name, scene):
    output_dir = os.path.join(output_dir, scene_name)
    os.makedirs(output_dir, exist_ok=True)
    scene.render.image_settings.file_format = "PNG"
    return output_dir


def set_device(device):
    bpy.context.scene.render.engine = "CYCLES"

    prefs = bpy.context.preferences.addons["cycles"].preferences
    match device:
        case "cpu":
            bpy.context.scene.render.threads_mode = "FIXED"
            bpy.context.scene.cycles.device = "CPU"

            # Access Cycles preferences
            if prefs:
                print("Cycles Addon Enabled:", True)
                prefs.compute_device_type = "NONE"
                print("Compute Device Type Set To:", prefs.compute_device_type)

                # Configure devices
                prefs.get_devices()
                for device in prefs.devices:
                    device.use = device.type == "CPU"
                    print(
                        f"Device: {device.name}, Type: {device.type}, Enabled: {device.use}"
                    )
            else:
                print("Warning: Cycles Addon is NOT enabled.")
        case "gpu":
            bpy.context.scene.cycles.device = "GPU"

            # Access Cycles preferences
            if prefs:
                print("Cycles Addon Enabled:", True)
                prefs.compute_device_type = "CUDA"
                print("Compute Device Type Set To:", prefs.compute_device_type)

                # Configure devices
                prefs.get_devices()
                for device in prefs.devices:
                    device.use = device.type in [
                        "CUDA",
                        "OPTIX",
                    ]  # Enable CUDA and OptiX
                    print(
                        f"Device: {device.name}, Type: {device.type}, Enabled: {device.use}"
                    )
            else:
                print("Warning: Cycles Addon is NOT enabled.")
        case _:
            bpy.context.scene.cycles.device = "GPU"

            # Access Cycles preferences
            if prefs:
                print("Cycles Addon Enabled:", True)
                prefs.compute_device_type = "CUDA"
                print("Compute Device Type Set To:", prefs.compute_device_type)

                # Configure devices
                prefs.get_devices()
                for device in prefs.devices:
                    device.use = True
                    print(
                        f"Device: {device.name}, Type: {device.type}, Enabled: {device.use}"
                    )
            else:
                print("Warning: Cycles Addon is NOT enabled.")


def parse_args():
    parser = ArgumentParser()
    parser.add_argument(
        "-s",
        "--scene",
        help="path to blender scene file to render",
        type=str,
        required=True,
    )
    parser.add_argument(
        "-p", "--port", help="port for the EC server", type=int, default=8980
    )
    parser.add_argument(
        "-d", "--device", help="device to render with", type=str, default=""
    )
    parser.add_argument(
        "-o",
        "--output",
        help="directory to save rendered images",
        type=str,
        default="rendering-data",
    )
    return parser.parse_args()


def main():
    args = parse_args()

    device = args.device.lower()
    set_device(device)

    scene_name = os.path.splitext(os.path.basename(args.scene))[0]
    scene_path = os.path.join(os.path.dirname(args.scene), f"{scene_name}.blend")
    scene = create_scene(scene_path)
    output = create_output_dir(args.output, scene_name, scene)

    client = FloraRenderingProbemClient(f"localhost:{args.port}")
    # data_collector = DataCollector()
    data_collector = []

    pyRAPL.setup()
    measure = pyRAPL.Measurement("blender")

    # for i in range(3000):
    for i in range(1, 17):
        config = {
            "resolution_x": 2000,
            "resolution_y": 2000,
            "aa_samples": 2,
            "ao_samples": 96,
            "filter": "BOX",
            "threads": i,
        }
        scene.render.threads = config["threads"]
        sampler = NvmlSampler()

        # ---- Render Settings ----
        print(f"setting rendering configuration to {config}")
        scene.render.resolution_x = config["resolution_x"]
        scene.render.resolution_y = config["resolution_y"]
        scene.render.resolution_percentage = 100

        if scene.render.engine == "CYCLES":
            scene.cycles.samples = config["aa_samples"]
            # scene.cycles.aa_samples = config.aa_samples
            scene.cycles.use_adaptive_sampling = True

            # Ambient occlusion
            world = bpy.context.scene.world
            world.use_nodes = True
            nodes = world.node_tree.nodes
            ao_node = next((n for n in nodes if n.type == "AMBIENT_OCCLUSION"), None)
            if ao_node is None:
                ao_node = nodes.new("ShaderNodeAmbientOcclusion")
            ao_node.samples = config["ao_samples"]
            # scene.cycles.ao_samples = config.ao_samples

            scene.cycles.use_denoising = True
            scene.cycles.denoiser = "OPENIMAGEDENOISE"
            # scene.cycles.denoising_optix = True
            scene.cycles.pixel_filter_type = config["filter"]
            # scene.cycles.filter_type = config.filter

        output_file = os.path.join(output, f"{scene_name}-{i}.png")
        scene.render.filepath = output_file

        print("Sampling CPU and GPU metrics before rendering...")
        start = time.time()
        sampler.sample()
        measure.begin()

        try:
            print("Starting render...")
            bpy.ops.render.render(write_still=True)
            print(f"Render complete! Image saved at: {output_file}")
        except Exception as e:
            print(f"Error during rendering: {e}")
            sys.exit(1)

        sampler.sample()

        measure.end()

        report = to_dataframe(sampler.create_report()).to_frame().reset_index()
        gpu_energy = report[report.source == ENERGY_SIGNAL].value.sum()

        cpu_energy = 0
        pkg_energy = measure.result.pkg
        if pkg_energy:
            cpu_energy += sum(v for v in pkg_energy) / (1000**2)

        dram_energy = measure.result.dram
        if dram_energy:
            cpu_energy += sum(v for v in dram_energy) / (1000**2)

        runtime = time.time() - start

        img = Image.open(output_file)
        img = img.convert("RGB")
        arr = np.array(img)
        piqe_score = piqe(arr)[0]
        img.close()
        # img = bpy.data.images['Render Result']
        # w, h = img.size[:2]
        # pixels = np.array(img.pixels[:], dtype=np.float32).reshape((h, w, 4))
        # pixels = np.flipud(pixels)
        # rgb = pixels[:, :, :3]
        # rgb = np.where(rgb <= 0.0031308, 12.92 * rgb, 1.055 * (rgb ** (1/2.2)) - 0.055)
        # rgb_uint8 = (np.clip(rgb, 0, 1) * 255).astype(np.uint8)
        # piqe_score = piqe(rgb_uint8)[0]

        scores = {
            "cpu_energy": cpu_energy,
            "gpu_energy": gpu_energy,
            "runtime": runtime,
            "piqe": piqe_score,
            "mse": 0,
        }
        # data_collector.add_record(i, config, scores)
        data_collector.append(
            {"iteration": i, "configuration": config, "results": scores}
        )

        for score in scores:
            print(f"{score}:{scores[score]}")

        # client.evaluate(**scores)

    # data_collector.write_data(
    #     os.path.join(output, f"results-{args.device.lower()}.json")
    # )
    with open(os.path.join(output, f"results-{args.device.lower()}.json"), "w") as f:
        json.dump(data_collector, f)


if __name__ == "__main__":
    main()
