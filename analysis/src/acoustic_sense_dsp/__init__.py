"""Synthetic acoustic ranging primitives; no hardware access is performed."""

from .chirp import generate_chirp
from .correlation import detect_echo
from .models import DetectionResult, Echo, RangingEstimate
from .ranging import delay_for_distance, distance_from_delay, estimate_distance
from .simulation import simulate_received_signal

__all__ = [
    "DetectionResult", "Echo", "RangingEstimate", "delay_for_distance",
    "detect_echo", "distance_from_delay", "estimate_distance",
    "generate_chirp", "simulate_received_signal",
]
