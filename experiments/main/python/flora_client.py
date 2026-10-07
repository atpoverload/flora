"""a thin client to talk to a flora server."""

import grpc
from flora_rendering_problem_service_pb2 import Empty, RenderingScore
from flora_rendering_problem_service_pb2_grpc import FloraRenderingProblemServiceStub


class FloraRenderingProblemClient:
    def __init__(self, addr):
        self.stub = FloraRenderingProblemServiceStub(grpc.insecure_channel(addr))

    def next_configuration(self):
        return self.stub.NextConfiguration(Empty())

    def evaluate(self, cpu_energy=0, gpu_energy=0, energy=0, runtime=0, piqe=0, brisque=0):
        score = RenderingScore()
        score.cpu_energy = cpu_energy
        score.gpu_energy = gpu_energy
        score.energy = energy
        score.runtime = runtime
        score.piqe = piqe
        score.brisque = brisque
        return self.stub.Evaluate(score)
