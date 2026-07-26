import pytest

from acoustic_sense_dsp import Echo, detect_echo, estimate_distance, generate_chirp, simulate_received_signal
from acoustic_sense_dsp.metrics import absolute_error, relative_error, summarize_errors
from acoustic_sense_dsp.ranging import delay_for_distance, distance_from_delay


@pytest.mark.parametrize("distance", [0.5, 1.0, 2.0])
def test_known_distances_are_recovered(distance):
    chirp = generate_chirp(4_000, 12_000, 0.01, 48_000, 0.8)
    received = simulate_received_signal(chirp, 48_000, [Echo(distance, 0.5)], 0.08, noise_std=0.02, seed=11)
    estimate = estimate_distance(detect_echo(received, chirp, min_delay_samples=100), 48_000, 343.0)
    assert estimate.found
    assert estimate.distance_m == pytest.approx(distance, abs=0.005)


def test_multiple_echoes_returns_earliest_candidate():
    chirp = generate_chirp(4_000, 12_000, 0.01, 48_000)
    received = simulate_received_signal(chirp, 48_000, [Echo(0.5, 0.35), Echo(2.0, 0.8)], 0.08, noise_std=0.01, seed=2)
    estimate = estimate_distance(detect_echo(received, chirp, min_delay_samples=100), 48_000, 343.0)
    assert estimate.distance_m == pytest.approx(0.5, abs=0.005)


def test_delay_formula_and_validation():
    seconds, samples = delay_for_distance(1.0, 48_000, 343.0)
    assert seconds == pytest.approx(2 / 343.0, rel=1e-12)
    assert distance_from_delay(samples, 48_000, 343.0) == pytest.approx(1.0, abs=0.002)
    with pytest.raises(ValueError):
        delay_for_distance(-1, 48_000, 343.0)
    with pytest.raises(ValueError):
        delay_for_distance(1, 0, 343.0)


def test_metrics_use_explicit_definitions():
    assert absolute_error(1.0, 0.9) == pytest.approx(0.1)
    assert relative_error(1.0, 0.9) == pytest.approx(0.1)
    summary = summarize_errors([1.0, 2.0, 3.0], [0.9, 2.2, 2.7])
    assert summary.mean_absolute_error_m == pytest.approx(0.2)
    assert summary.median_absolute_error_m == pytest.approx(0.2)
    assert summary.p95_absolute_error_m == pytest.approx(0.29)
