package amortizedcola

/**
 * Created with IntelliJ IDEA.
 * User: jdb
 * Date: 4/26/12
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
class Item extends Element {
    def key
    def value

    Item(key, value) {
        this.key = key
        this.value = value
    }
}
