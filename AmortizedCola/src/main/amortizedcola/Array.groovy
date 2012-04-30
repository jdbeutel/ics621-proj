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
    int nRight = 0  // look-ahead pointers only, before merge, on high side of array
    boolean merging = false
    int lastRealLapIdx = 0

    boolean searching = false
    int searchIndex, mostRecentSearchIndex, searchLowerBound
    def searchKey

    Array(int k) {
        assert k > 0
        this.k = k
        elements = new Element[2**k]
        maxItems = 2**(k-1)
    }

    void clear() {
        assert !merging && !searching
        nItems = 0
        nRealLaps = 0
        nLeft = 0
        nRight = 0
        lastRealLapIdx = 0
        for (i in 0..(elements.size()-1)) {
            elements[i] = null
        }
    }

    int getLowerBoundInclusive() {
        nRight ? elements.size() - nRight : 0
    }

    private int getUpperBoundExclusive() {
        nRight ? elements.size() : nLeft
    }

    def search(key, int lower, boolean stepping = false) {
        assert !merging && !searching
        searching = true
        assert key != null
        assert lowerBoundInclusive <= upperBoundExclusive
        if (lower < lowerBoundInclusive) {
            assert nRight && !nLeft && lower == 0
            lower = lowerBoundInclusive
        }
        assert lower <= upperBoundExclusive
        searchIndex = searchLowerBound = lower
        searchKey = key
        def result = searchStep()
        while (!stepping && !result) {
            result = searchStep()
        }
        result
    }

    def searchStep() {
        assert searching
        def result
        // find target key or next greater (if any)
        assert !(searchIndex > upperBoundExclusive)
        mostRecentSearchIndex = searchIndex // display even if it's on the upper bound, one past the end of the array
        if (searchIndex == upperBoundExclusive) {
            result = [null, findLookaheadIndex(searchKey, searchIndex)]
        } else {
            assert searchIndex < upperBoundExclusive && searchIndex >= searchLowerBound
            assert searchIndex - searchLowerBound <= 8    // look-ahead pointers provide a lower within 8 elements of target or greater
            def x = elements[searchIndex]
            if (x.key == null || x.key < searchKey) {
                searchIndex++
                result = null
            } else if (x.key == searchKey) {
                if (x instanceof RealLap) {
                    result = [null, x.index]
                } else {
                    assert x instanceof Item    // because all DupLap key are null
                    result = [x.value, 0]         // found; stop looking ahead
                }
            } else {
                assert x.key > searchKey
                result = [null, findLookaheadIndex(searchKey, searchIndex)]
            }
        }
        if (result) {
            searching = false
        }
        result
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
        def realLapIdx = 0
        if (nRight) {                       // only real look-ahead pointers
            realLapIdx = lesserIdx
        } else {
            assert nLeft > nItems + nRealLaps           // there must be duplicate look-ahead pointers
            int dupIdx = lesserIdx - (lesserIdx % 4)    // every index % 4 == 0
            assert dupIdx % 4 == 0
            // check for a real look-ahead pointer in at most the 3 most-recent elements
            for (int i = lesserIdx; i > dupIdx; i--) {
                if (elements[i] instanceof RealLap) {
                    realLapIdx = i
                    break
                }
            }
            if (!realLapIdx) {
                def d = elements[dupIdx]
                assert d instanceof DupLap
                if (d.left) {
                    assert d.left < lesserIdx
                    realLapIdx = d.left     // going back as far as necessary; could be optimized by storing a copy
                } else {
                    return 0    // there is no less-than look-ahead pointer
                }
            }
        }
        assert realLapIdx
        for (int i = realLapIdx + 1; i < greaterIdx; i++) {
            assert !(elements[i] instanceof RealLap)
        }
        def r = elements[realLapIdx]
        assert r instanceof RealLap
        assert r.key < key
        return r.index
    }

    int getTotalElements() {
        assert !merging
        assert !nLeft || !nRight
        nLeft ?: nRight
    }

    void addLaps(Array target) {
        assert !merging && !target.merging && !searching && !target.searching
        assert !nItems && !nRealLaps && !totalElements
        assert k + 1 == target.k
        nRealLaps = Math.floor(target.totalElements / 8)
        assert nRealLaps <= elements.size() / 4
        nRight = nRealLaps  // in advance, to get lowerBoundInclusive
        assert lowerBoundInclusive + nRealLaps == upperBoundExclusive
        for (int i = 0; i < nRealLaps; i++) {
            int srcIdx = i + lowerBoundInclusive
            int targetIdx = i*8 + 7 + target.lowerBoundInclusive
            elements[srcIdx] = new RealLap(key: target.elements[targetIdx].key, index: targetIdx)
            if (i) {
                assert elements[srcIdx-1].key < elements[srcIdx].key
            }
        }
    }

    void startMerge() {
        assert !merging && !searching
        merging = true
    }

    void addItem(Item item) {
        assert merging && nItems < maxItems && !searching
        // Merge any smaller look-ahead pointers, even if the key of the first one duplicates the last merged item,
        // to maintain the landing density in the target array for searches on greater keys.
        // I.e., don't merge them like items, where duplicate keys on larger levels are excluded.
        while (nRight && elements[lowerBoundInclusive].key < item.key) {
            moveNextLapFromRightToLeft()
        }
        addElement(item)
        nItems++
    }

    void addOnlyItem(Item item) {
        assert k == 1 && nItems == 0 && maxItems == 1
        startMerge()
        addItem(item)
        finishMerge()
        assert nItems == 1
    }

    private void moveNextLapFromRightToLeft() {
        assert merging && nRight && !searching
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
        // It's not used for a single-point search, but maybe it would be for a range search?
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
        assert merging && !searching
        if (nRealLaps && nLeft % 4 == 0) {  // need to add a duplicate look-ahead pointer here too
            def d = new DupLap(left: lastRealLapIdx, right: lastRealLapIdx)
            elements[nLeft++] = d
        }
        elements[nLeft++] = x
    }

    void finishMerge() {
        assert merging && !searching
        while (nRight) {
            moveNextLapFromRightToLeft()
        }
        merging = false
    }

    // source for merges
    Iterator<Item> getItemIterator() {
        new Iterator() {

            int i = 0
            int mostRecent = 0

            @Override
            boolean hasNext() {
                while (i < nLeft && !(elements[i] instanceof Item)) {
                    i++
                }
                i < nLeft
            }

            @Override
            Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException()
                }
                mostRecent = i
                def x = elements[i++]
                assert x instanceof Item
                x
            }

            @Override
            void remove() {
                throw new UnsupportedOperationException()
            }
        }
    }
}
