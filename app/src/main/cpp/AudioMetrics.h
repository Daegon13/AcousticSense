#pragma once
#include <atomic>
#include <cstdint>
struct MetricsSnapshot { int64_t framesRead, framesWritten, callbacks, emptyReads, partialReads, pulses; int32_t inputXruns, outputXruns; float peak, rms; };
class AudioMetrics {
public:
 void reset() noexcept; void input(const float* data, int32_t frames, int32_t requested) noexcept;
 void output(int32_t frames) noexcept; void pulse() noexcept { pulses_.fetch_add(1); }
 MetricsSnapshot snapshot(int32_t inputXruns, int32_t outputXruns) const noexcept;
private:
 std::atomic<int64_t> framesRead_{0}, framesWritten_{0}, callbacks_{0}, emptyReads_{0}, partialReads_{0}, pulses_{0};
 std::atomic<float> peak_{0}, rms_{0};
};
