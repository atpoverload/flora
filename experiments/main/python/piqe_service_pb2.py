# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: piqe_service.proto
# Protobuf Python Version: 4.25.1
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import symbol_database as _symbol_database
from google.protobuf.internal import builder as _builder
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\x12piqe_service.proto\x12\x1f\x66lora.experiments.sunflow.image\"\xbe\x01\n\x12\x43omputePiqeRequest\x12\x12\n\x05width\x18\x01 \x01(\x04H\x00\x88\x01\x01\x12\x13\n\x06height\x18\x02 \x01(\x04H\x01\x88\x01\x01\x12O\n\timage_row\x18\x03 \x03(\x0b\x32<.flora.experiments.sunflow.image.ComputePiqeRequest.ImageRow\x1a\x19\n\x08ImageRow\x12\r\n\x05pixel\x18\x01 \x03(\x04\x42\x08\n\x06_widthB\t\n\x07_height\"3\n\x13\x43omputePiqeResponse\x12\x12\n\x05score\x18\x01 \x01(\x02H\x00\x88\x01\x01\x42\x08\n\x06_score2\x89\x01\n\x0bPiqeService\x12z\n\x0b\x43omputePiqe\x12\x33.flora.experiments.sunflow.image.ComputePiqeRequest\x1a\x34.flora.experiments.sunflow.image.ComputePiqeResponse\"\x00\x42#\n\x1f\x66lora.experiments.sunflow.imageP\x01\x62\x06proto3')

_globals = globals()
_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, _globals)
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'piqe_service_pb2', _globals)
if _descriptor._USE_C_DESCRIPTORS == False:
  _globals['DESCRIPTOR']._options = None
  _globals['DESCRIPTOR']._serialized_options = b'\n\037flora.experiments.sunflow.imageP\001'
  _globals['_COMPUTEPIQEREQUEST']._serialized_start=56
  _globals['_COMPUTEPIQEREQUEST']._serialized_end=246
  _globals['_COMPUTEPIQEREQUEST_IMAGEROW']._serialized_start=200
  _globals['_COMPUTEPIQEREQUEST_IMAGEROW']._serialized_end=225
  _globals['_COMPUTEPIQERESPONSE']._serialized_start=248
  _globals['_COMPUTEPIQERESPONSE']._serialized_end=299
  _globals['_PIQESERVICE']._serialized_start=302
  _globals['_PIQESERVICE']._serialized_end=439
# @@protoc_insertion_point(module_scope)
