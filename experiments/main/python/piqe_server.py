from concurrent import futures

import grpc
import numpy as np

from pypiqe import piqe

from piqe_service_pb2 import ComputePiqeResponse
from piqe_service_pb2_grpc import PIQEService, add_PIQEServiceServicer_to_server


class PIQENvmlService(PIQEService):
    def ComputePiqe(self, request, context):
        image = np.empty(shape=(request.height, request.width))
        for j in range(request.height):
            for i in range(request.width):
                image[i, j] = request.image_row[j].pixel[i]
        response = ComputePiqeResponse()
        response.score = piqe(image)[0]
        print(f'image with dim {request.width}x{request.height}={response.score}')
        return response


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    add_PIQEServiceServicer_to_server(PIQENvmlService(), server)
    server.add_insecure_port('localhost:8980')
    print('starting piqe server at localhost:8980')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    serve()
