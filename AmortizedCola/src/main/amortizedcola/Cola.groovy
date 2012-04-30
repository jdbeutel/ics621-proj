package amortizedcola

/**
 * A domain for the simulation of an amortized cache-oblivious look-ahead array.
 * This is object-oriented, as opposed to an actual implementation with disks,
 * which would separate small parts in memory from large parts on disk.
 */
class Cola {

    int nLevels = 0
    List<Level> levels = []

    boolean merging = false
    int mergeTarget
    List<Iterator<Item>> iterators = []
    List<Item> nextItems = []

    Cola() {
        levels[0] = null    // skipping to avoid fractional real Item
        addLevel()
    }

    void insert(key, value, boolean stepping=false) {
        assert !merging
        def item = new Item(key, value)
        if (levels[1].available) {
            levels[1].addOnlyItem(item)
        } else {
            merge(item, stepping)
        }
    }

    def search(key) {
        assert !merging
        def result = null
        int lower = 0
        for (k in 1..nLevels) {
            (result, lower) = levels[k].search(key, lower)
            if (result) {
                break
            }
        }
        result
    }

    private int addLevel() {
        def k = ++nLevels
        levels[k] = new Level(k)
        k
    }

    private void merge(Item item, boolean stepping) {
        assert !merging
        merging = true
        mergeTarget = nextAvailableLevel
        assert mergeTarget > 1
        iterators[0] = null
        nextItems[0] = item
        for (k in 1..(mergeTarget-1)) {
            def itr = levels[k].itemIterator
            iterators[k] = itr
            nextItems[k] = itr.hasNext() ? itr.next() : null
        }
        levels[mergeTarget].startMerge()
        if (!stepping) {
            //noinspection GroovyEmptyStatementBody
            while(mergeStep()) {}
        }
    }

    boolean mergeStep() {
        assert merging
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
