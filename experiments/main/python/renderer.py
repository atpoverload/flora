""" a thin client to talk to a flora server. """
import random

import grpc

from flora_rendering_problem_service_pb2 import Empty, RenderingScore
from flora_rendering_problem_service_pb2_grpc import FloraRenderingProblemServiceStub


class PiqeClient:
    def __init__(self, addr):
        self.stub = FloraRenderingProblemServiceStub(
            grpc.insecure_channel(addr))

    def next_configuration(self):
        return self.stub.NextConfiguration(Empty())

    def evaluate(self, energy):
        score = RenderingScore()
        score.energy = energy
        return self.stub.Evaluate(score)


def main():
    client = PiqeClient('localhost:8980')
    while True:
        print(client.next_configuration())
        print(client.evaluate(random.random()))


if __name__ == '__main__':
    main()
