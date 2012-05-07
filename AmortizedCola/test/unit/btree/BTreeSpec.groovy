package btree

import spock.lang.Specification
import com.sun.electric.database.geometry.btree.CachingPageStorage
import com.sun.electric.database.geometry.btree.CachingPageStorageWrapper
import com.sun.electric.database.geometry.btree.unboxed.Pair
import com.sun.electric.database.geometry.btree.BTree
import com.sun.electric.database.geometry.btree.unboxed.UnboxedInt
import com.sun.electric.database.geometry.btree.LeafNodeCursor
import com.sun.electric.database.geometry.btree.InteriorNodeCursor

class BTreeSpec extends Specification {

    static final MAX_CACHE_PAGES = 8

    InstrumentedMemoryPageStorage imps = new InstrumentedMemoryPageStorage()
    CachingPageStorage ps = new CachingPageStorageWrapper(imps, MAX_CACHE_PAGES, false)
    BTree<Integer, Integer, Pair<Integer, Integer>> btree =
        new BTree<Integer,Integer,Pair<Integer,Integer>>(ps, UnboxedInt.instance, FatUnboxedInt.instance, null, null)

    def "inner nodes can hold up to four keys"() {

        expect:
        new InteriorNodeCursor(btree).INTERIOR_MAX_BUCKETS == 4
    }

    def "leaf nodes can hold up to four items"() {

        expect:
        new LeafNodeCursor(btree).LEAF_MAX_BUCKETS == 4
    }
}
