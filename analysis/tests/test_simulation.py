import numpy as np
import pytest

from acoustic_sense_dsp import Echo, generate_chirp, simulate_received_signal
from acoustic_sense_dsp.ranging import delay_for_distance


def test_echo_is_inserted_at_round_trip_delay():
    chirp = generate_chirp(4_000, 12_000, 0.01, 48_000)
    received = simulate_received_signal(chirp, 48_000, [Echo(1.0, 0.5)], 0.05)
    _, delay = delay_for_distance(1.0, 48_000, 343.0)
    assert np.allclose(received[delay:delay + chirp.size], chirp * 0.5, atol=1e-12)


def test_noise_is_reproducible():
    chirp = np.ones(8)
    first = simulate_received_signal(chirp, 100, [], 0.2, noise_std=0.1, seed=7)
    second = simulate_received_signal(chirp, 100, [], 0.2, noise_std=0.1, seed=7)
    assert np.allclose(first, second, atol=0)


@pytest.mark.parametrize("echo", [Echo(-1), Echo(1, -0.1), Echo(1, 1.1)])
def test_invalid_echo_parameters(echo):
    with pytest.raises(ValueError):
        simulate_received_signal(np.ones(8), 100, [echo], 1.0)
