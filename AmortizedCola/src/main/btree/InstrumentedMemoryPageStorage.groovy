package btree

import com.sun.electric.database.geometry.btree.MemoryPageStorage

/**
 * Created with IntelliJ IDEA.
 * User: jdb
 * Date: 5/6/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
class InstrumentedMemoryPageStorage extends MemoryPageStorage {

    InstrumentedMemoryPageStorage(int nBytes) {
        super(nBytes)
    }
}
