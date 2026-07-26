import numpy as np
import pytest

from acoustic_sense_dsp.chirp import generate_chirp


def test_configurable_chirp_has_expected_shape_and_bound():
    signal = generate_chirp(4_000, 12_000, 0.01, 48_000, 0.7, "hann")
    assert signal.shape == (480,)
    assert np.max(np.abs(signal)) <= 0.7 + 1e-12


@pytest.mark.parametrize("kwargs", [
    {"duration_s": 0}, {"sample_rate_hz": 0}, {"start_frequency_hz": 24_000},
    {"end_frequency_hz": 24_000}, {"amplitude": 0}, {"amplitude": 1.1},
])
def test_invalid_chirp_parameters(kwargs):
    defaults = dict(start_frequency_hz=4_000, end_frequency_hz=12_000, duration_s=0.01, sample_rate_hz=48_000, amplitude=0.8)
    defaults.update(kwargs)
    with pytest.raises(ValueError):
        generate_chirp(**defaults)
