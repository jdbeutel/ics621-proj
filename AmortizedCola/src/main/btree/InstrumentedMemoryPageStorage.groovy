package btree

import btree.com.sun.electric.database.geometry.btree.MemoryPageStorage

/**
 * Created with IntelliJ IDEA.
 * User: jdb
 * Date: 5/6/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
class InstrumentedMemoryPageStorage extends MemoryPageStorage {

    int lastpage = -1
    def sequentialReads, sequentialWrites, seekingReads, seekingWrites

    InstrumentedMemoryPageStorage(int nBytes) {
        super(nBytes)
        clear()
    }

    void clear() {
        sequentialReads = []
        sequentialWrites = []
        seekingReads = []
        seekingWrites = []
    }

    @Override
    void writePage(int pageid, byte[] buf, int ofs) {
        super.writePage(pageid, buf, ofs)
        if (lastpage == pageid - 1) {
            sequentialWrites << pageid
        } else {
            seekingWrites << pageid
        }
        lastpage = pageid
    }

    @Override
    void readPage(int pageid, byte[] buf, int ofs) {
        super.readPage(pageid, buf, ofs)
        if (lastpage == pageid - 1) {
            sequentialReads << pageid
        } else {
            seekingReads << pageid
        }
        lastpage = pageid
    }
}
