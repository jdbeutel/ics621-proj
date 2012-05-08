package btree

import spock.lang.Specification
import com.sun.electric.database.geometry.btree.LeafNodeCursor
import com.sun.electric.database.geometry.btree.InteriorNodeCursor
import spock.lang.Unroll

class BTreeSpec extends Specification {

    def ezbtree = new EasyBTree()

    def "inner nodes can hold up to four keys"() {

        expect:
        new InteriorNodeCursor(ezbtree.btree).INTERIOR_MAX_BUCKETS == 4
    }

    def "leaf nodes can hold up to four items"() {

        expect:
        new LeafNodeCursor(ezbtree.btree).LEAF_MAX_BUCKETS == 4
    }

    @Unroll
    def "tree of #N items has #nNodes nodes"() {

        when:
        for (i in N..1) {
            ezbtree.btree.insert(i, i)
        }

        then:
        ezbtree.btree.size() == N
        ezbtree.imps.numPages == nNodes

        where:
        N   | nNodes
        1   | 2
        4   | 2
        5   | 3
        6   | 3
        7   | 3
        8   | 3
        9   | 4
    }
}
