package btree

import com.sun.electric.database.geometry.btree.unboxed.UnboxedInt

/**
 * Created with IntelliJ IDEA.
 * User: jdb
 * Date: 5/6/12
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
class FatUnboxedInt extends UnboxedInt {

    final int claimedSize

    FatUnboxedInt(int size) {
        claimedSize = size
    }

    // just says that it needs more space than it does, so both interior and leaf nodes can have 4 buckets
    @Override
    int getSize() {
        assert super.getSize() == 4 && claimedSize >= super.getSize()
        return claimedSize
    }
}
