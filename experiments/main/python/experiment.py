from pathlib import Path

import numpy as np
from PIL import Image

from blender import Blender
from collector import DataCollector
from evaluator import ImageEvaluator
from flora_client import FloraRenderingProblemClient
from sampler import DeviceSampler


class Experiment:
    def __init__(self, scene: Path, output_dir: Path):
        self.scene_file = scene
        self.scene_name = scene.stem
        self.output_dir = output_dir
        self.blender = Blender()

    def setup(self, **kwargs):
        self.validate_settings(kwargs)
        self.device = kwargs.get("device", "cpu")

        self.blender.load_scene(self.scene_file)
        self.blender.set_render_settings(device=self.device)

        port = kwargs.get("ec_port")
        self.client = FloraRenderingProblemClient(f"localhost:{port}")

        self.data_collector = DataCollector()
        
        self.output_dir.mkdir(parents=True, exist_ok=True)

        sample_devices = ["cpu"]
        if self.device == "gpu":
            sample_devices.append("gpu")
        
        self.device_sampler = DeviceSampler(sample_devices)
        self.image_evaluator = ImageEvaluator(["piqe", "brisque"])

    def validate_settings(self, settings) -> bool:
        if "device" in settings and settings["device"] not in ["cpu", "gpu"]:
            raise ValueError("Invalid device setting. Must be 'cpu' or 'gpu'.")

        if "ec_port" not in settings:
            raise ValueError("Missing 'ec_port' setting (port for the EC server).")

        return True

    def run(self, num_iterations: int | None = None):
        i = 0
        while num_iterations is None or i < num_iterations:
            config = self.client.next_configuration()
            output_file = self.blender.render(
                config, f"{self.scene_name}-{i}", self.device_sampler
            )
            img = Image.open(output_file).resize((1200, 1200)).convert("RGB")
            arr = np.array(img)
            scores = {
                **self.device_sampler.collect_values(),
                **self.image_evaluator.evaluate(arr),
            }
            self.data_collector.add_record(i, config, scores)
            self.client.evaluate(**scores)
            i += 1

    def save_results(self):
        self.data_collector.write_data(self.output_dir / "results.json")

    def cleanup(self):
        self.device_sampler.close()
        self.image_evaluator.close()

        del self.client
        del self.data_collector
