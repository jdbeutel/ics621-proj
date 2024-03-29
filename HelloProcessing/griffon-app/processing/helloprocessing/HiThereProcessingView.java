package helloprocessing;

import griffon.plugins.processing.artifact.AbstractGriffonProcessingView;

public class HiThereProcessingView extends AbstractGriffonProcessingView {
    public HiThereModel model;
    public HiThereController controller;

    public void setModel(HiThereModel model) {
        this.model = model;
    }

    public void setController(HiThereController controller) {
        this.controller = controller;
    }

    // Global variables

    float radius = 50.0f;
    int X, Y;
    int nX, nY;
    int delay = 16;

    // Setup the Processing Canvas
    // The statements in the setup() function
    // execute once when the program begins
    public void setup() {
        size(400, 400);     // Size should be the first statement
        strokeWeight(10);
        frameRate(15);
        X = width / 2;
        Y = width / 2;
        nX = X;
        nY = Y;
    }

    // The statements in draw() are executed until the 
    // program is stopped. Each statement is executed in 
    // sequence and after the last line is read, the first 
    // line is executed again.
    public void draw() {

        radius = radius + sin((float) (frameCount / 4));

        // Track circle to new destination
        X += (nX - X) / delay;
        Y += (nY - Y) / delay;

        // Fill canvas grey
        background(100);

        // Set fill-color to blue
        fill(0, 121, 184);

        // Set stroke-color white
        stroke(255);

        // Draw circle
        ellipse(X, Y, radius, radius);
    }

    // Set circle's next destination
    public void mouseMoved() {
        nX = mouseX;
        nY = mouseY;
    }
}
