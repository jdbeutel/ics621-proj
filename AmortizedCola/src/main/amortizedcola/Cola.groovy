package amortizedcola

/**
 * A domain for the simulation of an amortized cache-oblivious look-ahead array.
 * This is object-oriented, as opposed to an actual implementation with disks,
 * which would separate small parts in memory from large parts on disk.
 */
class Cola {

    int nLevels = 0
    List<Level> levels = []

    Cola() {
        levels[0] = null    // skipping to avoid fractional real Item
        addLevel()
    }

    void insert(key, value) {
        def item = new Item(key, value)
        if (levels[1].available) {
            levels[1].addOnlyItem(item)
        } else {
            merge(item)
        }
    }

    def search(key) {
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

    private void merge(Item item) {
        int target = nextAvailableLevel
        assert target > 1
        List<Iterator<Item>> iterators = []
        List<Item> nextItems = []
        iterators[0] = null
        nextItems[0] = item
        for (k in 1..(target-1)) {
            def itr = levels[k].itemIterator
            iterators[k] = itr
            nextItems[k] = itr.hasNext() ? itr.next() : null
        }
        levels[target].startMerge()
        for (def least = firstLeastItem(nextItems); least; least = firstLeastItem(nextItems)) {
            levels[target].addItem(least)
            for (k in 0..(target-1)) {
                if (nextItems[k]?.key == least.key) {
                    def itr = iterators[k]
                    nextItems[k] = itr?.hasNext() ? itr.next() : null
                }
            }
        }
        levels[target].finishMerge()
        for (k in (target-1)..1) {
            levels[k].clear()
            levels[k].addLaps(levels[k+1])
        }
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
}
