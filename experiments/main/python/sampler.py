from abc import ABC, abstractmethod
from threading import Event, Thread
from time import sleep, perf_counter

import numpy as np
from pynvml import (
    nvmlInit, nvmlShutdown,
    nvmlDeviceGetHandleByIndex, nvmlDeviceGetPowerUsage
)
from pyRAPL import Measurement, setup as rapl_setup


class Sampler(ABC):
    @abstractmethod
    def run(self) -> None:
        pass

    @abstractmethod
    def stop(self) -> None:
        pass

    @abstractmethod
    def collect_values(self) -> dict:
        pass

    @abstractmethod
    def close(self) -> None:
        pass


class RuntimeSampler(Sampler):
    def __init__(self):
        self._t0 = self._t1 = 0.0

    def run(self) -> None:
        self._t0 = perf_counter()

    def stop(self) -> None:
        self._t1 = perf_counter()

    def collect_values(self) -> dict:
        return {"runtime": self._t1 - self._t0}

    def close(self) -> None:
        pass


class RaplEnergySampler(Sampler):
    def __init__(self):
        rapl_setup()
        self.measure = Measurement("measure")

    def run(self) -> None:
        self.measure.begin()

    def stop(self) -> None:
        self.measure.end()

    def collect_values(self) -> dict:
        r = self.measure.result
        energy_uj = (sum(r.pkg) if r.pkg else 0) + (sum(r.dram) if r.dram else 0)
        return {"cpu_energy": energy_uj / 1e6}

    def close(self) -> None:
        pass


class NvmlEnergySampler(Sampler):
    def __init__(self, interval: float = 5e-2, device_index: int = 0):
        nvmlInit()
        self.interval = interval
        self.device = nvmlDeviceGetHandleByIndex(device_index)
        self.samples: list[tuple[float, float]] = []  # (timestamp, watts)
        self._stop_event = Event()
        self._thread: Thread | None = None

    def _poll(self) -> None:
        while not self._stop_event.is_set():
            watts = nvmlDeviceGetPowerUsage(self.device) / 1e3  # mW -> W
            self.samples.append((perf_counter(), watts))
            self._stop_event.wait(self.interval)  # interruptible sleep

    def run(self) -> None:
        self.samples = [(perf_counter(), nvmlDeviceGetPowerUsage(self.device) / 1e3)]
        self._stop_event.clear()
        self._thread = Thread(target=self._poll, daemon=True)
        self._thread.start()

    def stop(self) -> None:
        self._stop_event.set()
        if self._thread:
            self._thread.join()

        self.samples.append((perf_counter(), nvmlDeviceGetPowerUsage(self.device) / 1e3))

    def collect_values(self) -> dict:
        if len(self.samples) < 2:
            return {"gpu_energy": 0.0}
        
        t, p = np.array(self.samples).T
        joules = float(np.sum((t[1:] - t[:-1]) * (p[1:] + p[:-1]) / 2))  # trapezoid
        return {"gpu_energy": joules}

    def close(self) -> None:
        nvmlShutdown()


class DeviceSampler:
    def __init__(self, devices: list[str]):
        self.samplers = [RuntimeSampler()] + [
            self._get_sampler(device) for device in devices
        ]

    @staticmethod
    def _get_sampler(device: str) -> Sampler:
        match device:
            case "cpu":
                return RaplEnergySampler()
            case "gpu":
                return NvmlEnergySampler()
            case _:
                raise ValueError(f"Unknown device: {device}")

    def __enter__(self):
        for sampler in self.samplers:
            sampler.run()
        return self

    def __exit__(self, *exc) -> bool:
        for sampler in reversed(self.samplers):
            sampler.stop()
        return False  # don't swallow exceptions

    def collect_values(self) -> dict:
        values = {}
        for sampler in self.samplers:
            values.update(sampler.collect_values())
        
        return values

    def close(self) -> None:
        for sampler in self.samplers:
            sampler.close()
