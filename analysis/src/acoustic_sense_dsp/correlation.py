"""Matched-filter echo detection with explicit no-detection output."""

import numpy as np
from scipy.signal import correlate

from .models import DetectionResult


def detect_echo(
    received: np.ndarray,
    transmitted: np.ndarray,
    min_delay_samples: int = 1,
    max_delay_samples: int | None = None,
    confidence_threshold: float = 0.35,
    candidate_threshold: float = 0.2,
) -> DetectionResult:
    """Detect the earliest credible echo using normalized matched-filter scores.

    Confidence is the normalized correlation coefficient in [0, 1], not a
    calibrated probability. The candidate threshold finds local peaks; the
    confidence threshold decides whether the result is trustworthy.
    """
    if received.ndim != 1 or transmitted.ndim != 1 or transmitted.size == 0:
        raise ValueError("signals must be one-dimensional and transmitted non-empty")
    if min_delay_samples < 0 or not 0 <= candidate_threshold <= confidence_threshold <= 1:
        raise ValueError("invalid delay or thresholds")
    raw = correlate(received, transmitted, mode="valid", method="fft")
    template_energy = np.sum(transmitted ** 2)
    window_energy = np.convolve(received ** 2, np.ones(transmitted.size), mode="valid")
    normalized = np.abs(raw) / np.sqrt(np.maximum(template_energy * window_energy, np.finfo(float).eps))
    upper = normalized.size - 1 if max_delay_samples is None else min(max_delay_samples, normalized.size - 1)
    if min_delay_samples > upper:
        return DetectionResult(False, None, None, 0.0, 0.0, normalized)
    region = normalized[min_delay_samples : upper + 1]
    local = np.flatnonzero(
        (region >= candidate_threshold)
        & (region >= np.r_[region[0], region[:-1]])
        & (region >= np.r_[region[1:], region[-1]])
    )
    if local.size == 0:
        score = float(np.max(region)) if region.size else 0.0
        return DetectionResult(False, None, None, score, score, normalized)
    # Earliest candidate avoids silently replacing a near echo with a stronger later reflection.
    peak = min_delay_samples + int(local[0])
    score = float(normalized[peak])
    found = score >= confidence_threshold
    return DetectionResult(found, peak if found else None, peak if found else None, score, score, normalized)
