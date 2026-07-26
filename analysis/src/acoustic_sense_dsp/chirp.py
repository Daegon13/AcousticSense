"""Configurable linear chirp generation."""

import numpy as np
from scipy.signal import chirp as scipy_chirp
from scipy.signal.windows import get_window


def generate_chirp(
    start_frequency_hz: float,
    end_frequency_hz: float,
    duration_s: float,
    sample_rate_hz: int,
    amplitude: float = 1.0,
    window: str | None = "hann",
) -> np.ndarray:
    """Return a linear chirp, optionally tapered with a SciPy window."""
    if sample_rate_hz <= 0:
        raise ValueError("sample_rate_hz must be positive")
    if duration_s <= 0:
        raise ValueError("duration_s must be positive")
    nyquist = sample_rate_hz / 2
    if start_frequency_hz <= 0 or end_frequency_hz <= 0:
        raise ValueError("frequencies must be positive")
    if start_frequency_hz >= nyquist or end_frequency_hz >= nyquist:
        raise ValueError("frequencies must be below Nyquist")
    if not 0 < amplitude <= 1:
        raise ValueError("amplitude must be in (0, 1]")
    sample_count = round(duration_s * sample_rate_hz)
    if sample_count < 2:
        raise ValueError("duration must produce at least two samples")
    time = np.arange(sample_count, dtype=float) / sample_rate_hz
    signal = amplitude * scipy_chirp(
        time, f0=start_frequency_hz, f1=end_frequency_hz, t1=duration_s, method="linear"
    )
    if window is not None:
        signal *= get_window(window, sample_count, fftbins=False)
    return signal
