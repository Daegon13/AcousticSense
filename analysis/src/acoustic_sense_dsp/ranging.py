"""Round-trip delay and distance conversions."""

from .models import DetectionResult, RangingEstimate


def delay_for_distance(distance_m: float, sample_rate_hz: int, speed_of_sound_mps: float) -> tuple[float, int]:
    if distance_m < 0:
        raise ValueError("distance_m cannot be negative")
    if sample_rate_hz <= 0 or speed_of_sound_mps <= 0:
        raise ValueError("sample rate and speed of sound must be positive")
    seconds = 2 * distance_m / speed_of_sound_mps
    return seconds, round(seconds * sample_rate_hz)


def distance_from_delay(delay_samples: int, sample_rate_hz: int, speed_of_sound_mps: float) -> float:
    if delay_samples < 0:
        raise ValueError("delay_samples cannot be negative")
    if sample_rate_hz <= 0 or speed_of_sound_mps <= 0:
        raise ValueError("sample rate and speed of sound must be positive")
    return delay_samples * speed_of_sound_mps / (2 * sample_rate_hz)


def estimate_distance(detection: DetectionResult, sample_rate_hz: int, speed_of_sound_mps: float) -> RangingEstimate:
    distance = None
    seconds = None
    if detection.found and detection.delay_samples is not None:
        distance = distance_from_delay(detection.delay_samples, sample_rate_hz, speed_of_sound_mps)
        seconds = detection.delay_samples / sample_rate_hz
    return RangingEstimate(
        detection.found, distance, seconds, detection.delay_samples,
        detection.peak_index, detection.score, detection.confidence,
    )
