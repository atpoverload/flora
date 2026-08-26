""" a thin client to talk to a flora server. """
import grpc

from flora_rendering_problem_service_pb2 import Empty, RenderingScore
from flora_rendering_problem_service_pb2_grpc import FloraRenderingProblemServiceStub


class FloraRenderingProbemClient:
    def __init__(self, addr):
        self.stub = FloraRenderingProblemServiceStub(
            grpc.insecure_channel(addr))

    def next_configuration(self):
        return self.stub.NextConfiguration(Empty())

    def evaluate(self, energy=0, runtime=0, piqe=0, mse=0, brisque=0):
        score = RenderingScore()
        score.energy = energy
        score.runtime = runtime
        score.piqe = piqe
        score.mse = mse
        score.brisque = brisque
        return self.stub.Evaluate(score)
