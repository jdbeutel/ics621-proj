package amortizedcola

import spock.lang.*

class ArraySpec extends Specification {

    def "level 1 array can hold 1 item"() {

        given:
        def a = new Array(1)

        when:
        a.addOnlyItem(new Item(42, 'foo'))

        then:
        a.nItems == 1
        a.nLeft == 1
        a.search(42, 0) == ['foo', 0]
        a.search(6, 0) == [null, 0]
    }

    def "empty level can be searched"() {

        given:
        def a = new Array(1)

        expect:
        a.search(42, 0) == [null, 0]
    }

    def "items can be added without look-ahead pointers"() {

        given:
        def a = new Array(2)
        a.startMerge()

        when:
        a.addItem(new Item(6, 'foo'))
        a.addItem(new Item(42, 'bar'))

        and:
        a.finishMerge()

        then:
        a.nItems == 2
        a.nLeft == 2
        a.search(42, 0) == ['bar', 0]
        a.search(6, 0) == ['foo', 0]
    }

    private Array createTarget4() {
        def target = new Array(4)
        target.startMerge()
        for (i in 10..17) {
            target.addItem(new Item(i, "value $i"))
        }
        target.finishMerge()
        target
    }

    @Unroll("item merges #description the look-ahead pointer")
    def "item merges #description the look-ahead pointer"() {

        given:
        def target = createTarget4()
        def a = new Array(3)

        when:
        a.addLaps(target)

        then:
        a.nRealLaps == 1
        a.nRight == 1
        a.nLeft == 0
        a.elements[7].key == 17
        a.elements[7].index == 7


        when:
        a.startMerge()
        a.addItem(new Item(itemKey, 'new value'))
        a.finishMerge()

        then:
        a.nLeft == 3
        a.elements[0] instanceof DupLap
        a.elements[0].left == 0
        a.elements[0].right == lapIdx

        and:
        a.elements[itemIdx].key == itemKey
        a.elements[lapIdx] instanceof RealLap
        a.elements[lapIdx].key == 17
        a.nRight == 0

        where:
        description | itemKey   | itemIdx   | lapIdx
        'before'    | 15        | 1         | 2
        'after'     | 19        | 2         | 1
        'equal to'  | 17        | 1         | 2
    }
}
