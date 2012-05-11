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

    final static SEEK_THRESHOLD = 4

    int lastpage = -42  // not -1, so initial write (page 0) counts as a seek
    def sequentialReads, sequentialWrites, seekingReads, seekingWrites
    int nSeeks = 0, nBlocksWritten = 0, nBlocksRead = 0

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
        if (Math.abs(lastpage - pageid) <= SEEK_THRESHOLD) {
            sequentialWrites << pageid
        } else {
            nSeeks++
            seekingWrites << pageid
        }
        nBlocksWritten++
        lastpage = pageid
    }

    @Override
    void readPage(int pageid, byte[] buf, int ofs) {
        super.readPage(pageid, buf, ofs)
        if (Math.abs(lastpage - pageid) <= SEEK_THRESHOLD) {
            sequentialReads << pageid
        } else {
            nSeeks++
            seekingReads << pageid
        }
        nBlocksRead++
        lastpage = pageid
    }
}
