""" a thin client to talk to a flora server. """
import os
import sys

from argparse import ArgumentParser

import blender as bpy
import grpc

from jcarbon.report import to_dataframe
from jcarbon.nvml.sampler import NvmlSampler, create_report

from flora_rendering_problem_service_pb2 import Empty, RenderingScore
from flora_rendering_problem_service_pb2_grpc import FloraRenderingProblemServiceStub


ENERGY_SIGNAL = 'nvmlDeviceGetTotalEnergyConsumption'


class FloraRenderingClient:
    def __init__(self, addr):
        self.stub = FloraRenderingProblemServiceStub(
            grpc.insecure_channel(addr))

    def next_configuration(self):
        return self.stub.NextConfiguration(Empty())

    def evaluate(self, energy):
        score = RenderingScore()
        score.energy = energy
        return self.stub.Evaluate(score)


def create_scene(scene_path):
    print(f"Loading scene from: {scene_path}")
    bpy.ops.wm.open_mainfile(filepath=scene_path)

    # Get the current scene
    scene = bpy.context.scene

    # ---- Fix Color Management ----
    try:
        scene.display_settings.display_device = 'sRGB'
        scene.view_settings.view_transform = 'Standard'
        scene.view_settings.look = 'None'
        scene.view_settings.exposure = 0.0
        print("Color management settings applied successfully.")
    except Exception as e:
        print(f"Warning: Failed to apply color management settings: {e}")

    scene.render.engine = 'CYCLES'
    scene.cycles.device = 'GPU'

    # Access Cycles preferences
    prefs = bpy.context.preferences.addons.get('cycles')
    if prefs:
        print('Cycles Addon Enabled:', True)
        prefs.preferences.compute_device_type = 'CUDA'
        print('Compute Device Type Set To:',
              prefs.preferences.compute_device_type)

        # Configure devices
        prefs.preferences.get_devices()
        for device in prefs.preferences.devices:
            device.use = device.type in [
                'CUDA', 'OPTIX']  # Enable CUDA and OptiX
            print(
                f'Device: {device.name}, Type: {device.type}, Enabled: {device.use}')
    else:
        print('Warning: Cycles Addon is NOT enabled.')
    return scene


def create_output_dir(scene):
    output_dir = os.path.join(os.path.dirname(scene), 'output')
    os.makedirs(output_dir, exist_ok=True)
    scene.render.image_settings.file_format = 'PNG'
    return output_dir


def parse_args():
    parser = ArgumentParser()
    parser.add_argument(
        's',
        'scene',
        help='path to blender scene file to render',
        type=str,
    )
    parser.add_argument(
        'p',
        'port',
        help='port for the EC server',
        type=int,
        default=8980,
    )
    return parser.parse_args()


def main():
    args = parse_args()

    scene_name = os.path.splitext(os.path.basename(args.scene))[0]
    scene_path = os.path.join(os.path.dirname(args.scene), scene_name)
    scene = create_scene(scene_path)
    output = create_output_dir(scene_path)

    client = FloraRenderingClient(f'localhost:{args.port}')
    i = 0
    while True:
        config = client.next_configuration()
        sampler = NvmlSampler()

        # ---- Render Settings ----
        print(f"setting rendering configuration to {config}")
        scene.render.resolution_x = config.resolution_x
        scene.render.resolution_y = config.resolution_y
        scene.render.resolution_percentage = 100

        if scene.render.engine == 'CYCLES':
            scene.cycles.samples = 128
            scene.cycles.use_adaptive_sampling = True
            scene.cycles.use_denoising = True
            scene.cycles.denoiser = 'OPENIMAGEDENOISE'
            scene.cycles.denoising_optix = True

        output_file = os.path.join(output, f'{scene_name}-{i}.png')
        scene.render.filepath = output_file

        print("Sampling GPU metrics before rendering...")
        sampler.sample()

        try:
            print("Starting render...")
            bpy.ops.render.render(write_still=True)
            print(f"Render complete! Image saved at: {output_file}")
        except Exception as e:
            print(f"Error during rendering: {e}")
            sys.exit(1)

        sampler.sample()
        report = to_dataframe(create_report(sampler.samples))
        energy = report[report.source == ENERGY_SIGNAL].sum()

        print(f"consumed {config:.4f} J")
        client.evaluate(energy)

        i += 1


if __name__ == '__main__':
    main()
