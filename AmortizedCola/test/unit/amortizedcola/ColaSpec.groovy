package amortizedcola

import spock.lang.*

class ColaSpec extends Specification {

    def "can search on empty cola"() {

        given:
        def cola = new Cola()

        expect:
        cola.search(42) == null
    }

    @Unroll
    def "sequential insert and search, N=#N"() {

        given:
        def cola = new Cola()
        def m = [:]
        for (i in 1..N) {
            m[i] = "value $i"
        }

        when:
        m.each {k, v ->
            cola.insert(k, v)
        }

        then:
//        cola.search(121) == 'value 121'
        m.each {k, v ->
//            println "searching for $k"
            assert cola.search(k) == v
        }

        and: "a search for a nonexistant key"
        cola.search(-1) == null

        where:
        N << (1..513)
    }
}
