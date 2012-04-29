package amortizedcola;

import griffon.plugins.processing.artifact.AbstractGriffonProcessingView;

public class AmortizedColaProcessingView extends AbstractGriffonProcessingView {
    AmortizedColaModel model
    AmortizedColaController controller

    void setModel(AmortizedColaModel model) {
        this.model = model;
    }

    void setController(AmortizedColaController controller) {
        this.controller = controller
    }

    private float y = 100

    // The statements in the setup() function 
    // execute once when the program begins
    void setup() {
        size(1024, 768)  // Size should be the first statement, XGA in the hope that's what the projector is
        // That size seems to have no effect, though; the preferredSize in AmortizedColaView does.
        stroke(0)     // Set line drawing color to black
        frameRate(30)
    }

    // The statements in draw() are executed until the 
    // program is stopped. Each statement is executed in 
    // sequence and after the last line is read, the first 
    // line is executed again.
    void draw() {
        background(255)   // Set the background to white
        y = y - 1
        if (y < 0) { y = height }
        line(0, y, width, y)
    }
}
