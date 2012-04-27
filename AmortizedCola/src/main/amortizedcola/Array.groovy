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
    int nLeft = 0   // merged items, on low side of array
    int nRight = 0  // look-ahead pointers before merge, on high side of array
    boolean merging = false
    int lastRealLapIdx = 0

    Array(int k) {
        assert k > 0
        this.k = k
        elements = new Element[2^k]
        maxItems = 2^(k-1)
    }

    private int getLowerBoundInclusive() {
        nRight ? elements.size() - nRight : 0
    }

    private int getUpperBoundExclusive() {
        nRight ? elements.size() : nLeft
    }

    def search(key, int lower) {
        assert !merging
        assert key != null
        assert lowerBoundInclusive <= upperBoundExclusive
        if (lower < lowerBoundInclusive) {
            assert nRight && !nLeft && lower == 0
            lower = lowerBoundInclusive
        }
        assert lower <= upperBoundExclusive
        int i = lower
        // find target key or next greater (if any)
        while (i < upperBoundExclusive) {
            assert i - lower < 8    // look-ahead pointers provide a lower within 8 elements of target or greater
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
                    return [x.value, 0]         // found; stop looking ahead
                }
            }
            assert x.key > key
            break
        }
        return [null, findLookaheadIndex(key, i)]
    }

    // finds the next-lesser real look-ahead pointer of the element that is next greater-than the key
    private int findLookaheadIndex(key, int greaterIdx) {

        assert greaterIdx == upperBoundExclusive || elements[greaterIdx].key > key

        int lesserIdx = greaterIdx - 1  // back within upperBound or less than key
        if (lesserIdx < lowerBoundInclusive ||              // there is no less-than look-ahead pointer
            !nRealLaps ||                                   // there are no look-ahead pointers at all
            lowerBoundInclusive == upperBoundExclusive) {   // there are no elements at all
            return 0
        }
        def r
        if (nRight) {                       // only real look-ahead pointers
            r = elements[lesserIdx]
        } else {
            assert nLeft > nItems + nRealLaps           // there must be duplicate look-ahead pointers
            int dupIdx = lesserIdx - (lesserIdx % 4)    // every index % 4 == 0
            assert dupIdx % 4 == 0
            def d = elements[dupIdx]
            assert d instanceof DupLap
            if (d.right && d.right <= lesserIdx) {
                r = elements[d.right]
            } else if (d.left) {
                assert d.left < lesserIdx
                r = elements[d.left]
            } else {
                return 0    // duplicate pointer says that there is no less-than look-ahead pointer
            }
        }
        assert r instanceof RealLap
        assert r.key < key
        return r.index
    }

    private int getTotalElements() {
        assert !merging
        assert !nLeft || !nRight
        nLeft ?: nRight
    }

    void addLaps(Array target) {
        assert !merging && !target.merging
        assert !nItems && !nRealLaps && !totalElements
        assert k + 1 == target.k
        nRealLaps = Math.floor(target.totalElements / 8)
        assert nRealLaps <= elements.size() / 4
        nRight = nRealLaps  // in advance, to get lowerBoundInclusive
        assert lowerBoundInclusive + nRealLaps == upperBoundExclusive
        for (int i = 0; i < nRealLaps; i++) {
            elements[lowerBoundInclusive + i] = target.elements[i*8 + 7 + target.lowerBoundInclusive]
            if (i) {
                assert elements[i-1].key < elements[i].key
            }
        }
    }

    void startMerge() {
        assert !merging
        merging = true
    }

    void addItem(Item item) {
        assert merging && nItems < maxItems
        // Merge any smaller look-ahead pointers, even if the key of the first one duplicates the last merged item,
        // to maintain the landing density in the target array for searches on greater keys.
        // I.e., don't merge them like items, where duplicate keys on larger levels are excluded.
        while (nRight && elements[lowerBoundInclusive].key < item.key) {
            moveNextLapFromRightToLeft()
        }
        addElement(item)
        nItems++
    }

    private void moveNextLapFromRightToLeft() {
        assert merging && nRight
        def r = elements[lowerBoundInclusive]
        assert r instanceof RealLap
        addElement(r)
        int insertedIdx = nLeft - 1
        if (insertedIdx < lowerBoundInclusive) {
            elements[lowerBoundInclusive] = null
        } else {
            assert insertedIdx == lowerBoundInclusive     // element was copied to itself, so don't clear it
        }
        nRight--
        lastRealLapIdx = insertedIdx

        // For any duplicate pointers to the left of the inserted real pointer,
        // up to the next real pointer, set their right-hand pointer to the index of the inserted real pointer.
        assert elements[insertedIdx] == r
        for (i in (insertedIdx-1)..0) {
            def x = elements[i]
            if (x instanceof RealLap) {
                break
            }
            if (x instanceof DupLap) {
                x.right = insertedIdx
            }
        }
    }

    private void addElement(Element x) {
        assert merging
        if (nRealLaps && nLeft % 4 == 0) {  // need to add a duplicate look-ahead pointer here too
            def d = new DupLap(left: lastRealLapIdx)
            elements[nLeft++] = d
        }
        elements[nLeft++] = x
    }

    void finishMerge() {
        assert merging
        while (nRight) {
            moveNextLapFromRightToLeft()
        }
        merging = false
    }
}
