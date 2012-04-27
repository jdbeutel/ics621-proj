package amortizedcola

/**
 * Duplicate lookahead pointer.
 * These are placed in Arrays that have both Items and RealLaps,
 * at every index % 4 == 0.
 * Deamortized would have another pair of indexes for pointers to a secondary Array.
 */
class DupLap extends Element {
    int left    // index of the closest real lookahead pointer to the left, or 0 if none
    int right   // index of the closest real lookahead pointer to the right, or 0 if none

    def getKey() {
        null
    }
}
