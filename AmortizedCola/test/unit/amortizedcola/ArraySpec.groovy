package amortizedcola

import spock.lang.*

class ArraySpec extends Specification {

    def 'level 1 array can hold 1 item'() {

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
}
