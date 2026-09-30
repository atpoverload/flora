from abc import ABC, abstractmethod

from brisque import BRISQUE
import numpy as np
from pypiqe import piqe


class Evaluator(ABC):
    @abstractmethod
    def evaluate(self, image: np.ndarray) -> dict:
        pass

    @abstractmethod
    def close(self):
        pass


class PiqeEvaluator(Evaluator):
    def evaluate(self, image: np.ndarray) -> dict:
        return {"piqe": piqe(image)[0]}

    def close(self):
        pass


class BrisqueEvaluator(Evaluator):
    def __init__(self):
        self.brisque = BRISQUE(url=False)
    
    def evaluate(self, image: np.ndarray) -> dict:
        return {"brisque": self.brisque.score(image)}

    def close(self):
        del self.brisque


class ImageEvaluator:
    def __init__(self, scores: list[str]):
        self.evaluators = [
            self._get_evaluator(score) for score in scores
        ]

    @staticmethod
    def _get_evaluator(score: str) -> Evaluator:
        match score:
            case "piqe":
                return PiqeEvaluator()
            case "brisque":
                return BrisqueEvaluator()
            case _:
                raise ValueError(f"Unknown score: {score}")

    def evaluate(self, image: np.ndarray) -> dict:
        results = {}
        for evaluator in self.evaluators:
            results.update(evaluator.evaluate(image))
        
        return results

    def close(self):
        for evaluator in self.evaluators:
            evaluator.close()
