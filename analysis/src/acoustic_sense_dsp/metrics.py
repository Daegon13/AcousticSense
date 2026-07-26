"""Distance error metrics."""

from dataclasses import dataclass
import numpy as np


@dataclass(frozen=True)
class ErrorSummary:
    mean_absolute_error_m: float
    median_absolute_error_m: float
    p95_absolute_error_m: float | None


def absolute_error(actual: float, estimated: float) -> float:
    return abs(estimated - actual)


def relative_error(actual: float, estimated: float) -> float:
    if actual == 0:
        raise ValueError("relative error is undefined for zero ground truth")
    return absolute_error(actual, estimated) / abs(actual)


def summarize_errors(actual: list[float], estimated: list[float]) -> ErrorSummary:
    if len(actual) != len(estimated) or not actual:
        raise ValueError("series must be non-empty and have equal lengths")
    errors = np.abs(np.asarray(estimated) - np.asarray(actual))
    return ErrorSummary(float(np.mean(errors)), float(np.median(errors)), float(np.percentile(errors, 95)) if len(errors) >= 2 else None)
