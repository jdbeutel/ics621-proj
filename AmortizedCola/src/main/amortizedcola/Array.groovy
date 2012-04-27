package amortizedcola

/**
 * A sorted array of 0..2^(k-1) items and 0..2^(k-2) real look-ahead pointers,
 * with duplicate look-ahead pointers every index % 4 == 0 elements
 * if there are both types of other elements.
 */
class Array {

    final int k                   // the number of the level of this Array
    final Element[] elements
    final int maxItems

    int nItems = 0
    int nRealLaps = 0
    int nElements = 0

    Array(int k) {
        assert k > 0
        this.k = k
        elements = new Element[2^k]
        maxItems = 2^(k-1)
    }

    def search(key, int lower) {
        assert key != null
        int upper
        if (lapOnly) {  // they're at the end of the array
            upper = elements.size()
            if (lower < upper - nRealLaps) {
                assert lower == 0
                lower = upper - nRealLaps
            }
        } else {
            upper = nElements
        }
        int i = lower
        while (i < upper) {
            assert i - lower < 8
            def x = elements[i]
            if (x.key == null || x.key < key) {
                i++
                continue
            }
            if (x.key == key) {
                if (x instanceof RealLap) {
                    return [null, x.index]
                } else {
                    assert x instanceof Item
                    return [x.value, 0]
                }
            }
            assert x.key > key
            break
        }
        i--     // back into range or less than key
        if (i < 0 ||        // no less-than look-ahead pointer
            !nRealLaps ||   // no look-ahead pointers at all
            !nElements) {   // no elements at all
            return [null, 0]
        }
        if (lapOnly) {
            def r = elements[i]
            assert r instanceof RealLap
            return [null, r.index]
        }
        assert nElements > nItems + nRealLaps       // there must be duplicate look-ahead pointers
        def d = elements[i - (i % 4)]
        assert d instanceof DupLap
        def r
        if (d.right && d.right <= i) {
            r = elements[d.right]
        } else if (d.left) {
            assert d.left < i
            r = elements[d.left]
        } else {
            return [null, 0]    // duplicate pointer says that there is no less-than look-ahead pointer
        }
        assert r instanceof RealLap
        assert r.key < key
        return [null, r.index]
    }

    private getLapOnly() {
        nRealLaps && !nItems
    }
}
