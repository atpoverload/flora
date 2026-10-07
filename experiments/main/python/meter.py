from abc import ABC
from time import time

import numpy as np
from brisque import BRISQUE
from PIL import Image
from pynvml import (
    nvmlInit, nvmlShutdown,
    nvmlDeviceGetCount, nvmlDeviceGetHandleByIndex,
    nvmlDeviceGetTotalEnergyConsumption
)
from pypiqe import piqe
from pyRAPL import Measurement, setup as rapl_setup


class Meter(ABC):
    def start(self) -> None:
        pass

    def stop(self) -> None:
        pass

    def read(self) -> float:
        raise NotImplementedError("Subclasses must implement this method.")


class TimeMeter(Meter):
    def start(self) -> None:
        self.start_time = time()

    def stop(self) -> None:
        self.end_time = time()

    def read(self) -> float:
        if self.start_time is None:
            raise ValueError("Meter has not been started.")

        if self.end_time is None:
            raise ValueError("Meter has not been stopped.")
        
        return self.end_time - self.start_time


class RaplEnergyMeter(Meter):
    def __init__(self):
        rapl_setup()
        self.measure = Measurement("measure")

    def start(self) -> None:
        self.measure.begin()

    def stop(self) -> None:
        self.measure.end()

    def read(self) -> float:
        result = self.measure.result
        energy_uj = (
            (sum(result.pkg) if result.pkg else 0) +
            (sum(result.dram) if result.dram else 0)
        )
        return energy_uj / 1e6


class NvmlEnergyMeter(Meter):
    def __init__(self):
        nvmlInit()
        self.device_count = nvmlDeviceGetCount()
        self.devices = [nvmlDeviceGetHandleByIndex(i) for i in range(self.device_count)]
        print(self.devices)
        self.energies = {}

    def start(self) -> None:
        for device_index in range(self.device_count):
            self.energies[device_index] = nvmlDeviceGetTotalEnergyConsumption(self.devices[device_index])

    def stop(self) -> None:
        for device_index in range(self.device_count):
            self.energies[device_index] = nvmlDeviceGetTotalEnergyConsumption(
                self.devices[device_index]
            ) - self.energies[device_index]

    def read(self) -> float:
        return sum(self.energies.values()) / 1e3


class EnergyMeter(Meter):
    def __init__(self):
        self.meters = [
            RaplEnergyMeter(),
            NvmlEnergyMeter()
        ]

    def start(self) -> None:
        for meter in self.meters:
            meter.start()

    def stop(self) -> None:
        for meter in self.meters:
            meter.stop()

    def read(self) -> float:
        return sum(meter.read() for meter in self.meters)


class ImageMeter(Meter):
    def __init__(self, image_path: str):
        self.image_path = image_path

    def start(self) -> None:
        pass

    def stop(self) -> None:
        img = Image.open(self.image_path).resize((1200, 1200)).convert("RGB")
        self.img = np.array(img)


class PiqeMeter(ImageMeter):
    def __init__(self, image_path: str):
        super().__init__(image_path)

    def read(self) -> float:
        return piqe(self.img)[0]


class BrisqueMeter(ImageMeter):
    def __init__(self, image_path: str):
        super().__init__(image_path)

    def read(self) -> float:
        return BRISQUE(url=False).score(self.img)


class MultiMeter:
    def __init__(self, image_path: str):
        self.image_path = image_path
        self.meters = [
            RaplEnergyMeter(),
            NvmlEnergyMeter(),
            EnergyMeter(),
            TimeMeter(),
            PiqeMeter(image_path),
            BrisqueMeter(image_path)
        ]

    def start(self) -> None:
        for meter in self.meters:
            meter.start()

    def stop(self) -> None:
        for meter in self.meters:
            meter.stop()

    def set_image_path(self, image_path: str) -> None:
        for meter in self.meters:
            if isinstance(meter, ImageMeter):
                meter.image_path = image_path

    def read(self) -> dict:
        return {
            "cpu_energy": self.meters[0].read(),
            "gpu_energy": self.meters[1].read(),
            "energy": self.meters[2].read(),
            "runtime": self.meters[3].read(),
            "piqe": self.meters[4].read(),
            "brisque": self.meters[5].read()
        }
