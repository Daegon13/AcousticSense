#include "TestSignal.h"
#include <algorithm>
#include <cmath>
void TestSignal::configure(int32_t sampleRate) noexcept {
    sampleRate_ = sampleRate;
    durationFrames_ = std::max(1, sampleRate / 100);
    position_ = 0;
}
void TestSignal::trigger() noexcept { position_ = 0; remaining_.store(durationFrames_, std::memory_order_release); }
void TestSignal::cancel() noexcept { remaining_.store(0, std::memory_order_release); }
float TestSignal::next() noexcept {
    auto left = remaining_.load(std::memory_order_relaxed);
    if (left <= 0) return 0.0F;
    const double t = static_cast<double>(position_) / sampleRate_;
    constexpr double duration = 0.010, f0 = 4000.0, slope = (12000.0 - f0) / duration;
    const double phase = 2.0 * 3.14159265358979323846 * (f0 * t + 0.5 * slope * t * t);
    const double window = durationFrames_ == 1 ? 1.0 : 0.5 * (1.0 - std::cos(2.0 * 3.14159265358979323846 * position_ / (durationFrames_ - 1)));
    const float value = static_cast<float>(0.05 * window * std::sin(phase));
    ++position_;
    remaining_.store(left - 1, std::memory_order_relaxed);
    return value;
}
