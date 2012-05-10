package btree

import spock.lang.Specification
import btree.com.sun.electric.database.geometry.btree.LeafNodeCursor
import btree.com.sun.electric.database.geometry.btree.InteriorNodeCursor
import spock.lang.Unroll

class BTreeSpec extends Specification {

    def ezbtree = new EasyBTree()

    def "inner nodes can hold up to five keys"() {

        expect:
        new InteriorNodeCursor(ezbtree.btree).INTERIOR_MAX_BUCKETS == 5
    }

    def "leaf nodes can hold up to five items"() {

        expect:
        new LeafNodeCursor(ezbtree.btree).LEAF_MAX_BUCKETS == 5
    }

    @Unroll
    def "reverse sequential tree of #N items has #nNodes nodes"() {

        when:
        for (i in N..1) {
            ezbtree.btree.insert(i, i)
        }

        then:
        ezbtree.btree.size() == N
        ezbtree.imps.numPages == nNodes

        where:
        N   | nNodes
        1   | 1
        5   | 1
        6   | 3
        7   | 4
        9   | 4
        10  | 5
        12  | 5
        13  | 8
        15  | 8
        16  | 9
        32  | 16
        65  | 32
    }
}
