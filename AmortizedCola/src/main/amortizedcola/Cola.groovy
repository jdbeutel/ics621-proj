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
        def a = levels[1].array
        if (a.nItems == 0) {
            a.elements[0] = item
            a.nItems++
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

    private void addLevel() {
        def k = ++nLevels
        levels[k] = new Level(k)
    }

}
