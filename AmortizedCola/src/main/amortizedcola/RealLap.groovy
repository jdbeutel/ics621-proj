package amortizedcola

/**
 * Real (not duplicate) lookahead pointer.
 */
class RealLap extends Element  {
    def key
    int index   // of this item in the next level
}
