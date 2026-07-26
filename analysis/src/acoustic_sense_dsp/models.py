"""Data models shared by simulation, detection, and ranging."""

from dataclasses import dataclass


@dataclass(frozen=True)
class Echo:
    distance_m: float
    amplitude: float = 1.0


@dataclass(frozen=True)
class DetectionResult:
    found: bool
    delay_samples: int | None
    peak_index: int | None
    score: float
    confidence: float
    correlation: "object"


@dataclass(frozen=True)
class RangingEstimate:
    found: bool
    distance_m: float | None
    delay_seconds: float | None
    delay_samples: int | None
    peak_index: int | None
    score: float
    confidence: float
