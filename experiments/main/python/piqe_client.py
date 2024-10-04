""" a thin client to talk to a flora server. """
import grpc

from piqe_service_pb2 import ComputePiqeRequest
from piqe_service_pb2_grpc import PiqeServiceStub


class PiqeClient:
    def __init__(self, addr):
        self.stub = PiqeServiceStub(grpc.insecure_channel(addr))

    def compute(self, image):
        request = ComputePiqeRequest()
        request.height = len(image)
        request.width = len(image[0])
        for j in range(request.height):
            row = ComputePiqeRequest.ImageRow()
            row.pixel.extend(image[j])
            request.image_row.extend([row])
        return self.stub.ComputePiqe(request).score


def main():
    client = PiqeClient('localhost:8913')
    print(client.compute([[1, 0], [0, 1]]))


if __name__ == '__main__':
    main()
