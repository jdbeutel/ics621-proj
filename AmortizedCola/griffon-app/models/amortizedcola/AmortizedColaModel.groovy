package amortizedcola

import groovy.beans.Bindable

class AmortizedColaModel {
    @Bindable pApplet = new AmortizedColaProcessingView()  // todo: is there an official way, using DI automatically?
    @Bindable cola = new Cola()
}