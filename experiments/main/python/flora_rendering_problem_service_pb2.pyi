import knob_pb2 as _knob_pb2
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Mapping as _Mapping, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class Empty(_message.Message):
    __slots__ = ()
    def __init__(self) -> None: ...

class RenderingKnobs(_message.Message):
    __slots__ = ("resolutionX", "resolutionY")
    RESOLUTIONX_FIELD_NUMBER: _ClassVar[int]
    RESOLUTIONY_FIELD_NUMBER: _ClassVar[int]
    resolutionX: _knob_pb2.RangeKnob
    resolutionY: _knob_pb2.RangeKnob
    def __init__(self, resolutionX: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ..., resolutionY: _Optional[_Union[_knob_pb2.RangeKnob, _Mapping]] = ...) -> None: ...

class RenderingConfiguration(_message.Message):
    __slots__ = ("resolutionX", "resolutionY")
    RESOLUTIONX_FIELD_NUMBER: _ClassVar[int]
    RESOLUTIONY_FIELD_NUMBER: _ClassVar[int]
    resolutionX: int
    resolutionY: int
    def __init__(self, resolutionX: _Optional[int] = ..., resolutionY: _Optional[int] = ...) -> None: ...

class RenderingScore(_message.Message):
    __slots__ = ("energy",)
    ENERGY_FIELD_NUMBER: _ClassVar[int]
    energy: float
    def __init__(self, energy: _Optional[float] = ...) -> None: ...
