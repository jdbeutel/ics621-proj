package btree

import btree.com.sun.electric.database.geometry.btree.CachingPageStorage
import btree.com.sun.electric.database.geometry.btree.CachingPageStorageWrapper
import btree.com.sun.electric.database.geometry.btree.unboxed.Pair
import btree.com.sun.electric.database.geometry.btree.BTree
import btree.com.sun.electric.database.geometry.btree.unboxed.UnboxedInt
import btree.com.sun.electric.database.geometry.btree.InteriorNodeCursor

/**
 * Created with IntelliJ IDEA.
 * User: jdb
 * Date: 5/7/12
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
class EasyBTree {

    static final MAX_CACHE_PAGES = 8

    // 36 for 2 (breaks BTree), 48 for 3, or w/ FatUnboxedInt(6) value, 60 for 4 or 72 for 5
    InstrumentedMemoryPageStorage imps = new InstrumentedMemoryPageStorage(72)

    def ps = new CachingPageStorageWrapper(imps, MAX_CACHE_PAGES, false)
    BTree<Integer, Integer, Pair<Integer, Integer>> btree =
        new BTree<Integer,Integer,Pair<Integer,Integer>>(ps, UnboxedInt.instance, new FatUnboxedInt(6), null, null)
    int btreeDegree = new InteriorNodeCursor(btree).INTERIOR_MAX_BUCKETS
}
