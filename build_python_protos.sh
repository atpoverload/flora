python -m grpc_tools.protoc \
    -Iexperiments/main/proto \
    --python_out=experiments/main/python \
    --pyi_out=experiments/main/python \
    --grpc_python_out=experiments/main/python \
    experiments/main/proto/knob.proto \
    experiments/main/proto/flora_rendering_problem_service.proto
