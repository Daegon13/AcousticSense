#include "AudioMetrics.h"
#include <algorithm>
#include <cmath>
void AudioMetrics::reset() noexcept { framesRead_=0; framesWritten_=0; callbacks_=0; emptyReads_=0; partialReads_=0; pulses_=0; peak_=0; rms_=0; }
void AudioMetrics::input(const float* data, int32_t frames, int32_t requested) noexcept {
 if(frames<=0){ emptyReads_.fetch_add(1); peak_=0; rms_=0; return; }
 if(frames<requested) partialReads_.fetch_add(1); framesRead_.fetch_add(frames);
 float peak=0, sum=0; for(int32_t i=0;i<frames;i++){ float v=std::abs(data[i]); peak=std::max(peak,v); sum += data[i]*data[i]; }
 peak_.store(peak); rms_.store(std::sqrt(sum/static_cast<float>(frames)));
}
void AudioMetrics::output(int32_t frames) noexcept { framesWritten_.fetch_add(frames); callbacks_.fetch_add(1); }
MetricsSnapshot AudioMetrics::snapshot(int32_t ix, int32_t ox) const noexcept { return {framesRead_,framesWritten_,callbacks_,emptyReads_,partialReads_,pulses_,ix,ox,peak_,rms_}; }
