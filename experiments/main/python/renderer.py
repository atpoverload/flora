"""a thin client to talk to a flora server."""

import os
import sys
import time
from argparse import ArgumentParser

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
# Add the site-packages from Blender's own pip-installed packages
sys.path.insert(
    0, "/home/mamataliev/Documents/Projects/Research/jcarbon/service/src/main/python"
)
sys.path.insert(0, "/root/.local/lib/python3.13/site-packages")

import bpy
import numpy as np
import pyRAPL
from brisque import BRISQUE
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
    scene = bpy.context.scene
    cycles = scene.cycles
    prefs = bpy.context.preferences.addons["cycles"].preferences

    scene.render.engine = "CYCLES"
    match device:
        case "cpu":
            cycles.device = "CPU"
            scene.render.threads_mode = "FIXED"
            prefs.compute_device_type = "NONE"

            # Configure devices
            prefs.get_devices()
            for device in prefs.devices:
                device.use = device.type == "CPU"
                print(
                    f"Device: {device.name}, Type: {device.type}, Enabled: {device.use}"
                )

            # Force Blender to recognize the preference change
            bpy.context.preferences.is_dirty = True
        case "gpu":
            cycles.device = "GPU"
            prefs.compute_device_type = "CUDA"

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

            # Force Blender to recognize the preference change
            bpy.context.preferences.is_dirty = True
        case _:
            cycles.device = "GPU"
            prefs.compute_device_type = "CUDA"

            # Configure devices
            prefs.get_devices()
            for device in prefs.devices:
                device.use = True
                print(
                    f"Device: {device.name}, Type: {device.type}, Enabled: {device.use}"
                )

            # Force Blender to recognize the preference change
            bpy.context.preferences.is_dirty = True

    # Verify
    print("Cycles Device Set To:", cycles.device)
    print("Compute Device Type Set To:", prefs.compute_device_type)


def parse_args():
    parser = ArgumentParser()
    parser.add_argument("--background", action="store_true")
    parser.add_argument("--python")
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

    scene_name = os.path.splitext(os.path.basename(args.scene))[0]
    scene_path = os.path.join(os.path.dirname(args.scene), f"{scene_name}.blend")
    scene = create_scene(scene_path)
    output = create_output_dir(args.output, scene_name, scene)

    device = args.device.lower()
    set_device(device)

    client = FloraRenderingProbemClient(f"localhost:{args.port}")
    data_collector = DataCollector()

    pyRAPL.setup()
    measure = pyRAPL.Measurement("blender")

    for i in range(3000):
        config = client.next_configuration()
        cpu_energy = 0
        while True:
            sampler = NvmlSampler()

            # ---- Render Settings ----
            print(f"setting rendering configuration to {config}")
            scene.render.resolution_x = config.resolution_x
            scene.render.resolution_y = config.resolution_y
            scene.render.resolution_percentage = 100
            if device == "cpu":
                scene.render.threads = config.threads

            if scene.render.engine == "CYCLES":
                scene.cycles.samples = max((2**config.aa_samples) ** 2, 1)
                scene.cycles.use_adaptive_sampling = True

                # Ambient occlusion
                world = bpy.context.scene.world
                nodes = world.node_tree.nodes
                ao_node = next(
                    (n for n in nodes if n.type == "AMBIENT_OCCLUSION"), None
                )
                if ao_node is None:
                    ao_node = nodes.new("ShaderNodeAmbientOcclusion")
                ao_node.samples = config.ao_samples

                scene.cycles.use_denoising = True
                scene.cycles.denoiser = "OPENIMAGEDENOISE"
                # scene.cycles.denoising_optix = True
                scene.cycles.pixel_filter_type = config.filter

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

            pkg_energy = measure.result.pkg
            if pkg_energy:
                cpu_energy += sum(v for v in pkg_energy) / (1000**2)

            dram_energy = measure.result.dram
            if dram_energy:
                cpu_energy += sum(v for v in dram_energy) / (1000**2)

            if cpu_energy == 0:
                continue

            report = to_dataframe(sampler.create_report()).to_frame().reset_index()
            gpu_energy = 0
            if device == "gpu":
                gpu_energy = report[report.source == ENERGY_SIGNAL].value.sum()

            energy = cpu_energy + gpu_energy
            runtime = time.time() - start

            img = Image.open(output_file)
            img = img.resize((1200, 1200))
            img = img.convert("RGB")
            arr = np.array(img)
            piqe_score = piqe(arr)[0]

            obj = BRISQUE(url=False)
            brisque_score = obj.score(img)
            img.close()

            scores = {
                "energy": energy,
                "runtime": runtime,
                "piqe": piqe_score,
                "brisque": brisque_score,
                "mse": 0,
            }
            data_collector.add_record(i, config, scores)

            for score in scores:
                print(f"{score}:{scores[score]}")

            client.evaluate(**scores)
            break

    data_collector.write_data(
        os.path.join(output, f"results-{args.device.lower()}.json")
    )


if __name__ == "__main__":
    main()
