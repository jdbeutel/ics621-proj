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
    def "increasing sequential insert and search, N=#N"() {

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

    @Unroll
    def "decreasing sequential insert and search, N=#N"() {

        given:
        def cola = new Cola()
        def m = [:]
        for (i in N..1) {
            m[i] = "value $i"
        }

        when:
        m.each {k, v ->
            cola.insert(k, v)
        }

        then:
        m.each {k, v ->
            assert cola.search(k) == v
        }

        and: "a search for a nonexistant key"
        cola.search(-1) == null

        where:
        N << (1..513)
    }

    @Unroll
    def "pseudo-random insert and search, N=#N"() {

        given:
        def cola = new Cola()
        def m = [:]
        def r = new Random(42)  // same seed, for consistent results
        for (i in 1..N) {
            def key = r.nextInt((int)2**30)
            m[key] = "value $key"
        }

        when:
        m.each {k, v ->
            cola.insert(k, v)
        }

        then:
        m.each {k, v ->
            assert cola.search(k) == v
        }

        and: "a search for a nonexistant key"
        cola.search(-1) == null

        where:
        N << (1..513)
    }

    @Unroll
    def "overlapping insert and search, N=#N, ranges #ranges have nItems #expectedNItems"() {

        given:
        def cola = new Cola()
        def m = [:]

        expect:
        ranges.eachWithIndex { range, rangeIdx ->
            for (i in range) {
                def key = i
                def value = "value $key range $rangeIdx"
                m[key] = value
                cola.insert(key, value)
                m.each {k, v ->
                    assert cola.search(k) == v
                }
                assert cola.search(-1) == null
            }
            assert cola.NItems == expectedNItems[rangeIdx]
        }

        where:
        N   | ranges                                | expectedNItems
        1   | [1..1, 1..1, 1..1, 1..1, 1..1]        | [[1], [0, 1], [1, 1], [0, 0, 1], [1, 0, 1]]
        2   | [1..2, 2..1]                          | [[0, 2], [0, 0, 2]]
        2   | [2..1, 2..1]                          | [[0, 2], [0, 0, 2]]
        2   | [2..1, 1..2]                          | [[0, 2], [0, 0, 2]]
        3   | [1..3, 1..3, 1..3, 1..3]              | [[1, 2], [0, 2, 3], [1, 0, 0, 3], [0, 0, 3, 3]]
        3   | [1..3, 1..3, 1..3, 1..3, 1..3]        | [[1, 2], [0, 2, 3], [1, 0, 0, 3], [0, 0, 3, 3], [1, 2, 3, 3]]
        3   | [1..3, 1..3, 1..3, 1..3, 1..3, 1..3]  | [[1, 2], [0, 2, 3], [1, 0, 0, 3], [0, 0, 3, 3], [1, 2, 3, 3], [0, 2, 0, 0, 3]]
        3   | [1..3, 1..3, 1..3, 1..3, 3..2, 2..3, 3..2]  | [[1, 2], [0, 2, 3], [1, 0, 0, 3], [0, 0, 3, 3], [0, 2, 3, 3], [0, 0, 0, 0, 3], [0, 2, 0, 0, 3]]
        3   | [1..3, 1..3, 1..3, 1..3, 3..2, 2..3, 3..2, 2..3]  |  [[1, 2], [0, 2, 3], [1, 0, 0, 3], [0, 0, 3, 3], [0, 2, 3, 3], [0, 0, 0, 0, 3], [0, 2, 0, 0, 3], [0, 0, 2, 0, 3]]
        3   | [1..3, 1..3, 1..3, 1..3, 3..2, 2..3, 3..2, 2..3, 3..1]  |  [[1, 2], [0, 2, 3], [1, 0, 0, 3], [0, 0, 3, 3], [0, 2, 3, 3], [0, 0, 0, 0, 3], [0, 2, 0, 0, 3], [0, 0, 2, 0, 3], [1, 2, 2, 0, 3]]
        15  | [1..15, 1..15]                        | [[1, 2, 4, 8], [0, 2, 4, 8, 15]]
        15  | [1..15, 8..15]                        | [[1, 2, 4, 8], [1, 2, 4, 0, 15]]
        15  | [1..15, 4..10]                        | [[1, 2, 4, 8], [0, 2, 4, 0, 15]]
        15  | [4..10, 1..15]                        | [[1, 2, 4], [0, 2, 4, 0, 10]]
        16  | [1..16, 1..16]                        | [[0, 0, 0, 0, 16], [0, 0, 0, 0, 0, 16]]
        16  | [1..16, 8..16]                        | [[0, 0, 0, 0, 16], [1, 0, 0, 8, 16]]
        16  | [1..16, 9..16]                        | [[0, 0, 0, 0, 16], [0, 0, 0, 8, 16]]
        16  | [1..16, 4..10]                        | [[0, 0, 0, 0, 16], [1, 2, 4, 0, 16]]
        16  | [4..10, 1..16]                        | [[1, 2, 4], [1, 2, 4, 0, 10]]
        60  | [48..10, 1..16, 33..60]               | [[1, 2, 4, 0, 0, 32], [1, 2, 4, 0, 16, 32], [1, 2, 0, 0, 16, 0, 48]]
        255 | [1..255]                              | [[1, 2, 4, 8, 16, 32, 64, 128]]
        257 | [1..257]                              | [[1, 0, 0, 0, 0, 0, 0, 0, 256]]
    }
}
