package amortizedcola

/**
 * A Level of an amortized COLA contains one array,
 * since that will be easier to visualize than two arrays.
 * To support this one-array implementation,
 * Cola will do a k-way merge to the next free level,
 * instead of a cascading merge on each level.
 * This may make Level redundant with Array,
 * but a deamortized COLA would have three arrays per Level.
 */
class Level {

    final int k                   // the number of this level
    final Array array

    Level(int k) {
        assert k > 0
        this.k = k
        array = new Array(k)
    }

    def search(key, int lower) {
        array.search(key, lower)
    }

    boolean getAvailable() {
        array.nItems == 0
    }

    void addItem(Item item) {
        array.addItem(item)
    }
}
