#include "TestSignal.h"
#include <algorithm>
#include <cassert>
#include <cmath>
int main() {
    TestSignal signal;
    signal.configure(48000);
    for (int i = 0; i < 512; ++i) assert(signal.next() == 0.0F); // silent by default
    signal.trigger();
    int nonSilent = 0;
    float maximum = 0.0F;
    for (int i = 0; i < 480; ++i) {
        const float value = signal.next();
        maximum = std::max(maximum, std::abs(value));
        if (value != 0.0F) ++nonSilent;
    }
    assert(nonSilent > 0);
    assert(maximum <= 0.05001F);
    assert(!signal.active());
    assert(signal.next() == 0.0F); // exactly one bounded pulse
}
