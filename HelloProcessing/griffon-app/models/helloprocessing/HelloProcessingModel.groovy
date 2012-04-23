package helloprocessing

import groovy.beans.Bindable

class HelloProcessingModel {
   @Bindable pApplet = new HiThereProcessingView()  // todo: official way, using DI?
}