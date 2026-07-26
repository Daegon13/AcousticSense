"""Deterministic synthetic direct paths, echoes, and Gaussian noise."""

from collections.abc import Sequence
import numpy as np

from .models import Echo
from .ranging import delay_for_distance


def simulate_received_signal(
    transmitted: np.ndarray,
    sample_rate_hz: int,
    echoes: Sequence[Echo],
    duration_s: float,
    speed_of_sound_mps: float = 343.0,
    noise_std: float = 0.0,
    direct_path_amplitude: float | None = None,
    seed: int | None = 0,
) -> np.ndarray:
    if transmitted.ndim != 1 or transmitted.size == 0:
        raise ValueError("transmitted must be a non-empty one-dimensional signal")
    if duration_s <= 0 or sample_rate_hz <= 0 or speed_of_sound_mps <= 0:
        raise ValueError("duration, sample rate, and speed of sound must be positive")
    if noise_std < 0:
        raise ValueError("noise_std cannot be negative")
    if direct_path_amplitude is not None and not 0 <= direct_path_amplitude <= 1:
        raise ValueError("direct_path_amplitude must be in [0, 1]")
    size = round(duration_s * sample_rate_hz)
    if size < transmitted.size:
        raise ValueError("duration_s is too short for the transmitted signal")
    received = np.random.default_rng(seed).normal(0.0, noise_std, size)

    def add_at(delay: int, amplitude: float) -> None:
        end = min(size, delay + transmitted.size)
        if delay < size:
            received[delay:end] += amplitude * transmitted[: end - delay]

    if direct_path_amplitude is not None:
        add_at(0, direct_path_amplitude)
    for echo in echoes:
        if echo.distance_m < 0 or not 0 <= echo.amplitude <= 1:
            raise ValueError("echo distance must be non-negative and amplitude in [0, 1]")
        _, delay = delay_for_distance(echo.distance_m, sample_rate_hz, speed_of_sound_mps)
        add_at(delay, echo.amplitude)
    return received
