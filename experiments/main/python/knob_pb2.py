# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# NO CHECKED-IN PROTOBUF GENCODE
# source: knob.proto
# Protobuf Python Version: 5.29.0
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import runtime_version as _runtime_version
from google.protobuf import symbol_database as _symbol_database
from google.protobuf.internal import builder as _builder
_runtime_version.ValidateProtobufRuntimeVersion(
    _runtime_version.Domain.PUBLIC,
    5,
    29,
    0,
    '',
    'knob.proto'
)
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\nknob.proto\x12\nflora.knob\"_\n\tRangeKnob\x12\x12\n\x05start\x18\x01 \x01(\x03H\x00\x88\x01\x01\x12\x10\n\x03\x65nd\x18\x02 \x01(\x03H\x01\x88\x01\x01\x12\x11\n\x04step\x18\x03 \x01(\x03H\x02\x88\x01\x01\x42\x08\n\x06_startB\x06\n\x04_endB\x07\n\x05_stepB\x0e\n\nflora.knobP\x01\x62\x06proto3')

_globals = globals()
_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, _globals)
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'knob_pb2', _globals)
if not _descriptor._USE_C_DESCRIPTORS:
  _globals['DESCRIPTOR']._loaded_options = None
  _globals['DESCRIPTOR']._serialized_options = b'\n\nflora.knobP\001'
  _globals['_RANGEKNOB']._serialized_start=26
  _globals['_RANGEKNOB']._serialized_end=121
# @@protoc_insertion_point(module_scope)
