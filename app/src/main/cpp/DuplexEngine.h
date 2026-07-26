#pragma once
#include <oboe/Oboe.h>
#include <array>
#include <atomic>
#include <memory>
#include <mutex>
#include <string>
#include "AudioMetrics.h"
#include "TestSignal.h"
class DuplexEngine final : public oboe::AudioStreamDataCallback, public oboe::AudioStreamErrorCallback {
public:
 bool start(); void stop(); bool pulse() noexcept; std::string snapshotJson();
 oboe::DataCallbackResult onAudioReady(oboe::AudioStream*, void*, int32_t) override;
 void onErrorAfterClose(oboe::AudioStream*, oboe::Result) override;
private:
 bool openOutput(oboe::SharingMode); bool openInput(oboe::SharingMode, oboe::InputPreset); void closeStreams();
 std::string streamJson(const char*, const std::shared_ptr<oboe::AudioStream>&, const char*) const;
 std::mutex lifecycleMutex_; std::shared_ptr<oboe::AudioStream> output_, input_;
 std::array<float, 8192> inputBuffer_{}; TestSignal signal_; AudioMetrics metrics_;
 std::atomic<bool> running_{false}, disconnected_{false}, readError_{false}; std::atomic<int32_t> starts_{0}, stops_{0}; std::atomic<int64_t> startedAtMillis_{0}, stoppedAtMillis_{0};
 std::string lastError_, inputPreset_="Unprocessed", retainedOutputJson_, retainedInputJson_; bool outputShared_=false, inputShared_=false;
};
