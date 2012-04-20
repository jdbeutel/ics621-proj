package griffon.hello

import groovy.beans.Bindable

class GriffonHelloModel {
    // @Bindable String propName
    String scriptSource
    @Bindable def scriptResult
    @Bindable boolean enabled = true
}