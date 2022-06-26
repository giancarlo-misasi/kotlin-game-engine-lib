package dev.misasi.giancarlo.events.input.gestures.detector

import dev.misasi.giancarlo.events.input.gestures.GestureEvent

class SingleTapDetector : TapDetector(1, GestureEvent.Type.Tap)
