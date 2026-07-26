#include "TestSignal.h"
#include <algorithm>
#include <cmath>
void TestSignal::configure(int32_t sampleRate) noexcept {
    durationFrames_ = std::max(1, sampleRate / 10); // at most approximately 100 ms
    phaseStep_ = 2.0F * 3.14159265358979323846F * 1000.0F / static_cast<float>(sampleRate);
    phase_ = 0.0F;
}
void TestSignal::trigger() noexcept { remaining_.store(durationFrames_, std::memory_order_release); }
void TestSignal::cancel() noexcept { remaining_.store(0, std::memory_order_release); }
float TestSignal::next() noexcept {
    auto left = remaining_.load(std::memory_order_relaxed);
    if (left <= 0) return 0.0F;
    const float value = 0.08F * std::sin(phase_);
    phase_ += phaseStep_;
    if (phase_ >= 2.0F * 3.14159265358979323846F) phase_ -= 2.0F * 3.14159265358979323846F;
    remaining_.store(left - 1, std::memory_order_relaxed);
    return value;
}
