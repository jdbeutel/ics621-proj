package amortizedcola

/**
 * A domain for the simulation of an amortized cache-oblivious look-ahead array.
 * This is object-oriented, as opposed to an actual implementation with disks,
 * which would separate small parts in memory from large parts on disk.
 */
class Cola {

    final static DISK_LEVEL = 4     // first level not in memory

    int nLevels = 0
    List<Level> levels = []

    boolean merging = false
    int mergeTarget
    List<Iterator<Item>> iterators = []
    List<Item> nextItems = []

    boolean searching = false
    def searchKey
    int searchLevel
    boolean searchNextLevel = false
    int searchLowerBound
    def searchResult

    int nInserts = 0
    int nSearches = 0
    int nSeeks = 0      // supposing 3 high-density disks with de-amortized COLA, and ignoring track changes

    Cola() {
        levels[0] = null    // skipping to avoid fractional real Item
        addLevel()
    }

    int getN() {
        (int) levels.collect {it && it.array && !it.array.merging ? it.array.nItems : 0}.sum()
    }

    void insert(key, value, boolean stepping = false) {
        assert !merging && !searching
        nInserts++
        def item = new Item(key, value)
        if (levels[1].available) {
            levels[1].addOnlyItem(item)
        } else {
            merge(item, stepping)
        }
    }

    def search(key, boolean stepping = false) {
        assert !merging && !searching
        searching = true
        nSearches++
        searchKey = key
        searchLevel = 1
        searchLowerBound = 0
        searchResult = null
        //noinspection GroovyEmptyStatementBody
        while (searchStep(stepping) && !stepping) {}
        stepping ? searching : searchResult
    }

    boolean searchStep(boolean stepping = false) {
        assert searching && !merging
        def result
        if (searchNextLevel) {
            searchLevel++
            searchNextLevel = false
        }
        if (searchLevel > nLevels) {
            searchResult = null
            searching = false
        } else {
            if (!levels[searchLevel].array.searching) {
                result = levels[searchLevel].search(searchKey, searchLowerBound, stepping)
                if (searchLevel >= DISK_LEVEL && levels[searchLevel].array.totalElements) {
                    nSeeks++
                }
            } else {
                result = levels[searchLevel].searchStep()
            }
            if (result) {
                assert !levels[searchLevel].array.searching
                def (value, lower) = result
                if (value != null) {
                    searchResult = value
                    searching = false
                } else {
                    searchNextLevel = true
                    searchLowerBound = lower
                }
            }
        }
        searching
    }

    private int addLevel() {
        def k = ++nLevels
        levels[k] = new Level(k)
        k
    }

    private void merge(Item item, boolean stepping) {
        assert !merging && !searching
        merging = true
        mergeTarget = nextAvailableLevel
        assert mergeTarget > 1
        iterators[0] = null
        nextItems[0] = item
        for (k in 1..(mergeTarget-1)) {
            def itr = levels[k].itemIterator
            iterators[k] = itr
            nextItems[k] = itr.hasNext() ? itr.next() : null
            if (k >= DISK_LEVEL) {
                nSeeks++
            }
        }
        levels[mergeTarget].startMerge()
        if (mergeTarget >= DISK_LEVEL) {
            nSeeks++
        }
        if (!stepping) {
            //noinspection GroovyEmptyStatementBody
            while(mergeStep()) {}
        }
    }

    boolean mergeStep() {
        assert merging && !searching
        def least = firstLeastItem(nextItems)
        if (least) {
            levels[mergeTarget].addItem(least)
            for (k in 0..(mergeTarget-1)) {
                if (nextItems[k]?.key == least.key) {
                    def itr = iterators[k]
                    nextItems[k] = itr?.hasNext() ? itr.next() : null
                }
            }
        } else {
            levels[mergeTarget].finishMerge()
            for (k in (mergeTarget-1)..1) {
                levels[k].clear()
                levels[k].addLaps(levels[k+1])
            }
            merging = false
        }
        merging
    }

    private static Item firstLeastItem(nextItems) {
        Item least = null
        nextItems.each {
            if (it != null && (!least || it.key < least.key)) {
                least = it
            }
        }
        least
    }

    private int getNextAvailableLevel() {
        for (k in 1..nLevels) {
            if (levels[k].available) {
                return k
            }
        }
        addLevel()
    }

    def getNItems() {
        levels[1..nLevels].array.nItems
    }
}
