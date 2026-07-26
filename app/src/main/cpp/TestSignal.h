#pragma once
#include <atomic>
#include <cstdint>
class TestSignal {
public:
    void configure(int32_t sampleRate) noexcept;
    void trigger() noexcept;
    void cancel() noexcept;
    float next() noexcept;
    bool active() const noexcept { return remaining_.load(std::memory_order_relaxed) > 0; }
private:
    std::atomic<int32_t> remaining_{0};
    int32_t durationFrames_{0};
    int32_t sampleRate_{0};
    int32_t position_{0};
};
