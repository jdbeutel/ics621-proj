package amortizedcola

class AmortizedColaController {
    // these will be injected by Griffon
    AmortizedColaModel model
    AmortizedColaView view

    // this method is called after model and view are injected
    void mvcGroupInit(Map args) {
        model.pApplet.model = model     // todo: redundant?
        model.pApplet.controller = this // todo: redundant?
        // model.pApplet.init()         // todo: necessary?
    }

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
        Remember that actions will be called outside of the UI thread
        by default. You can change this setting of course.
        Please read chapter 9 of the Griffon Guide to know more.
       
    def action = { evt = null ->
    }
    */
}
