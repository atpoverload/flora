from google.protobuf.internal import containers as _containers
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Iterable as _Iterable, Mapping as _Mapping, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class ComputePiqeRequest(_message.Message):
    __slots__ = ("width", "height", "image_row")
    class ImageRow(_message.Message):
        __slots__ = ("pixel",)
        PIXEL_FIELD_NUMBER: _ClassVar[int]
        pixel: _containers.RepeatedScalarFieldContainer[int]
        def __init__(self, pixel: _Optional[_Iterable[int]] = ...) -> None: ...
    WIDTH_FIELD_NUMBER: _ClassVar[int]
    HEIGHT_FIELD_NUMBER: _ClassVar[int]
    IMAGE_ROW_FIELD_NUMBER: _ClassVar[int]
    width: int
    height: int
    image_row: _containers.RepeatedCompositeFieldContainer[ComputePiqeRequest.ImageRow]
    def __init__(self, width: _Optional[int] = ..., height: _Optional[int] = ..., image_row: _Optional[_Iterable[_Union[ComputePiqeRequest.ImageRow, _Mapping]]] = ...) -> None: ...

class ComputePiqeResponse(_message.Message):
    __slots__ = ("score",)
    SCORE_FIELD_NUMBER: _ClassVar[int]
    score: float
    def __init__(self, score: _Optional[float] = ...) -> None: ...
