import numpy as np

from acoustic_sense_dsp import Echo, detect_echo, generate_chirp, simulate_received_signal
from acoustic_sense_dsp.ranging import delay_for_distance


def make_chirp():
    return generate_chirp(4_000, 12_000, 0.01, 48_000, 0.8)


def test_strong_direct_path_is_rejected_and_later_echo_found():
    chirp = make_chirp()
    received = simulate_received_signal(chirp, 48_000, [Echo(1.0, 0.4)], 0.06, noise_std=0.02, direct_path_amplitude=1.0, seed=4)
    result = detect_echo(received, chirp, min_delay_samples=32)
    _, expected = delay_for_distance(1.0, 48_000, 343.0)
    assert result.found
    assert abs(result.delay_samples - expected) <= 1


def test_no_echo_returns_explicit_failure():
    chirp = make_chirp()
    received = simulate_received_signal(chirp, 48_000, [], 0.06, noise_std=0.02, seed=3)
    result = detect_echo(received, chirp, min_delay_samples=32)
    assert not result.found
    assert result.delay_samples is None


def test_low_amplitude_echo_is_detected_in_low_noise():
    chirp = make_chirp()
    received = simulate_received_signal(chirp, 48_000, [Echo(0.5, 0.08)], 0.05, noise_std=0.005, seed=8)
    result = detect_echo(received, chirp, min_delay_samples=100)
    _, expected = delay_for_distance(0.5, 48_000, 343.0)
    assert result.found
    assert abs(result.delay_samples - expected) <= 1
