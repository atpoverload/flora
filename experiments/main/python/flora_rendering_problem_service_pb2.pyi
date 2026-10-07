import knob_pb2 as _knob_pb2
from google.protobuf.internal import containers as _containers
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from collections.abc import Iterable as _Iterable, Mapping as _Mapping
from typing import ClassVar as _ClassVar, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class Empty(_message.Message):
    __slots__ = ()
    def __init__(self) -> None: ...

class RenderingKnobs(_message.Message):
    __slots__ = ("resolution_x", "resolution_y", "aa_samples", "ao_samples", "filter", "threads")
    RESOLUTION_X_FIELD_NUMBER: _ClassVar[int]
    RESOLUTION_Y_FIELD_NUMBER: _ClassVar[int]
    AA_SAMPLES_FIELD_NUMBER: _ClassVar[int]
    AO_SAMPLES_FIELD_NUMBER: _ClassVar[int]
    FILTER_FIELD_NUMBER: _ClassVar[int]
    THREADS_FIELD_NUMBER: _ClassVar[int]
    resolution_x: _knob_pb2.RangeKnob
    resolution_y: _knob_pb2.RangeKnob
    aa_samples: _knob_pb2.RangeKnob
    ao_samples: _knob_pb2.RangeKnob
    filter: _containers.RepeatedScalarFieldContainer[str]
    threads: _knob_pb2.RangeKnob
    def __init__(self, resolution_x: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ..., resolution_y: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ..., aa_samples: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ..., ao_samples: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ..., filter: _Optional[_Iterable[str]] = ..., threads: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ...) -> None: ...

class RenderingConfiguration(_message.Message):
    __slots__ = ("resolution_x", "resolution_y", "aa_samples", "ao_samples", "filter", "threads")
    RESOLUTION_X_FIELD_NUMBER: _ClassVar[int]
    RESOLUTION_Y_FIELD_NUMBER: _ClassVar[int]
    AA_SAMPLES_FIELD_NUMBER: _ClassVar[int]
    AO_SAMPLES_FIELD_NUMBER: _ClassVar[int]
    FILTER_FIELD_NUMBER: _ClassVar[int]
    THREADS_FIELD_NUMBER: _ClassVar[int]
    resolution_x: int
    resolution_y: int
    aa_samples: int
    ao_samples: int
    filter: str
    threads: int
    def __init__(self, resolution_x: _Optional[int] = ..., resolution_y: _Optional[int] = ..., aa_samples: _Optional[int] = ..., ao_samples: _Optional[int] = ..., filter: _Optional[str] = ..., threads: _Optional[int] = ...) -> None: ...

class RenderingScore(_message.Message):
    __slots__ = ("cpu_energy", "gpu_energy", "energy", "runtime", "piqe", "brisque", "mse")
    CPU_ENERGY_FIELD_NUMBER: _ClassVar[int]
    GPU_ENERGY_FIELD_NUMBER: _ClassVar[int]
    ENERGY_FIELD_NUMBER: _ClassVar[int]
    RUNTIME_FIELD_NUMBER: _ClassVar[int]
    PIQE_FIELD_NUMBER: _ClassVar[int]
    BRISQUE_FIELD_NUMBER: _ClassVar[int]
    MSE_FIELD_NUMBER: _ClassVar[int]
    cpu_energy: float
    gpu_energy: float
    energy: float
    runtime: float
    piqe: float
    brisque: float
    mse: float
    def __init__(self, cpu_energy: _Optional[float] = ..., gpu_energy: _Optional[float] = ..., energy: _Optional[float] = ..., runtime: _Optional[float] = ..., piqe: _Optional[float] = ..., brisque: _Optional[float] = ..., mse: _Optional[float] = ...) -> None: ...
