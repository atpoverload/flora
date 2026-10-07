import logging
from pathlib import Path

import bpy
from grpc._channel import _InactiveRpcError

from blender import BlenderSceneRenderer
from flora_client import FloraRenderingProblemClient
from flora_rendering_problem_service_pb2 import RenderingConfiguration, RenderingScore
from meter import MultiMeter


class Experiment:
    def __init__(self, scene: Path, output_dir: Path):
        self.scene_file = scene
        self.scene_name = scene.stem
        self.output_dir = output_dir

        bpy.ops.wm.open_mainfile(filepath=self.scene_file.as_posix())
        self.scene_renderer = BlenderSceneRenderer(scene=bpy.context.scene)

    def setup(self, device: str, ec_port: int):
        self.device = device
        self.scene_renderer.set_render_settings(device=self.device)
        self.client = FloraRenderingProblemClient(f"localhost:{ec_port}")
        self.multi_meter = MultiMeter(self.output_dir.as_posix())

    def warmup(self):
        test_configuration = RenderingConfiguration(
            resolution_x=100,
            resolution_y=100,
            aa_samples=0,
            ao_samples=0,
            filter="GAUSSIAN",
            threads=1
        )
        self.scene_renderer.apply_configuration(test_configuration, warmup=True)
        self.multi_meter.set_image_path("/tmp/warmup.png")
        self.multi_meter.start()
        self.scene_renderer.render()
        self.multi_meter.stop()

    def run(self, num_iterations: int | None = None):
        i = 0
        while num_iterations is None or i < num_iterations:
            try:
                config = self.client.next_configuration()
            except _InactiveRpcError as e:
                logging.error("Flora client is inactive.")
                break

            output_file = self.output_dir / f"{self.scene_name}-{i}.png"
            self.scene_renderer.apply_configuration(
                config,
                output_file_path=output_file.as_posix()
            )
            self.multi_meter.set_image_path(output_file.as_posix())
            self.multi_meter.start()
            self.scene_renderer.render()
            self.multi_meter.stop()
            scores = self.multi_meter.read()
            logging.info(f"Scores {RenderingScore(**scores)}")
            self.client.evaluate(**scores)
            i += 1

    def cleanup(self):
        self.scene_renderer.cleanup()
