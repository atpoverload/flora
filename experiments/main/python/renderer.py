import logging
import sys
from argparse import ArgumentParser
from pathlib import Path

from experiment import Experiment

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s",
    stream=sys.stdout,
    force=True
)


def parse_args():
    parser = ArgumentParser()
    parser.add_argument(
        "-s",
        "--scene",
        help="path to blender scene file to render",
        type=str,
        required=True,
    )
    parser.add_argument(
        "-p", "--port", help="port for the EC server", type=int, default=8980
    )
    parser.add_argument(
        "-d", "--device", help="device to render with", type=str, default=""
    )
    parser.add_argument(
        "-o",
        "--output",
        help="directory to save rendered images",
        type=str,
        default="rendering-data",
    )
    return parser.parse_args()


def main():
    args = parse_args()
    args.scene = Path(args.scene)
    args.output = Path(args.output)
    args.output.mkdir(parents=True, exist_ok=True)

    experiment = Experiment(args.scene, args.output)
    experiment.setup(device=args.device, ec_port=args.port)
    experiment.warmup()
    experiment.run()
    experiment.cleanup()


if __name__ == "__main__":
    main()
