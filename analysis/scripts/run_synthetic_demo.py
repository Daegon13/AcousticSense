#!/usr/bin/env python3
"""Run and plot one reproducible synthetic ranging experiment."""

import argparse
from pathlib import Path
import sys

ANALYSIS_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ANALYSIS_ROOT / "src"))

import matplotlib.pyplot as plt
import numpy as np

from acoustic_sense_dsp import Echo, detect_echo, estimate_distance, generate_chirp, simulate_received_signal
from acoustic_sense_dsp.metrics import absolute_error


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--distance", type=float, default=1.0)
    parser.add_argument("--noise", type=float, default=0.02)
    parser.add_argument("--seed", type=int, default=2026)
    parser.add_argument("--speed-of-sound", type=float, default=343.0)
    parser.add_argument("--output", type=Path, default=ANALYSIS_ROOT / "output" / "synthetic_demo.png")
    args = parser.parse_args()
    sample_rate = 48_000
    transmitted = generate_chirp(4_000, 12_000, 0.01, sample_rate, amplitude=0.8)
    received = simulate_received_signal(
        transmitted, sample_rate, [Echo(args.distance, 0.55)], 0.08,
        args.speed_of_sound, args.noise, direct_path_amplitude=0.9, seed=args.seed,
    )
    # A short explicit guard rejects the zero-delay direct-path correlation lobe.
    detection = detect_echo(received, transmitted, min_delay_samples=32)
    estimate = estimate_distance(detection, sample_rate, args.speed_of_sound)

    print(f"Ground truth distance: {args.distance:.3f} m")
    if not estimate.found:
        print("Estimated distance: no reliable echo")
        print(f"Confidence: {estimate.confidence:.3f} (normalized correlation; not a probability)")
    else:
        error = absolute_error(args.distance, estimate.distance_m)
        print(f"Estimated distance: {estimate.distance_m:.3f} m")
        print(f"Absolute error: {error:.3f} m")
        print(f"Delay samples: {estimate.delay_samples}")
        print(f"Confidence: {estimate.confidence:.3f} (normalized correlation; not a probability)")

    args.output.parent.mkdir(parents=True, exist_ok=True)
    time_ms = np.arange(received.size) / sample_rate * 1_000
    fig, axes = plt.subplots(3, 1, figsize=(10, 8), constrained_layout=True)
    axes[0].plot(np.arange(transmitted.size) / sample_rate * 1_000, transmitted)
    axes[0].set(title="Transmitted synthetic chirp", ylabel="Amplitude")
    axes[1].plot(time_ms, received)
    axes[1].set(title="Synthetic received signal", ylabel="Amplitude")
    axes[2].plot(np.arange(detection.correlation.size) / sample_rate * 1_000, detection.correlation)
    if detection.peak_index is not None:
        axes[2].axvline(detection.peak_index / sample_rate * 1_000, color="red", label="Detected peak")
        axes[2].legend()
    title_estimate = "no reliable echo" if estimate.distance_m is None else f"estimated {estimate.distance_m:.3f} m"
    axes[2].set(title=f"Matched filter — actual {args.distance:.3f} m, {title_estimate}", xlabel="Delay (ms)", ylabel="Score")
    fig.savefig(args.output, dpi=150)
    plt.close(fig)
    print(f"Plot saved to: {args.output}")
    return 0 if estimate.found else 2


if __name__ == "__main__":
    raise SystemExit(main())
