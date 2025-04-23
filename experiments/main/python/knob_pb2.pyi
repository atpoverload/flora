from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Optional as _Optional

DESCRIPTOR: _descriptor.FileDescriptor

class RangeKnob(_message.Message):
    __slots__ = ("start", "end", "step")
    START_FIELD_NUMBER: _ClassVar[int]
    END_FIELD_NUMBER: _ClassVar[int]
    STEP_FIELD_NUMBER: _ClassVar[int]
    start: int
    end: int
    step: int
    def __init__(self, start: _Optional[int] = ..., end: _Optional[int] = ..., step: _Optional[int] = ...) -> None: ...
