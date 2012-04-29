package amortizedcola

import griffon.plugins.processing.artifact.AbstractGriffonProcessingView
import processing.core.PFont

class AmortizedColaProcessingView extends AbstractGriffonProcessingView {
    AmortizedColaModel model
    AmortizedColaController controller
    Cola cola = new Cola()
    int key = 1
    PFont font8, font10, font12, font14, font16
    def fonts
    def red = color(255, 0, 0)

    final static CELL_WIDTH = 28
    final static CELL_HEIGHT = 20
    final static LEVEL_SPACING = 20

    // The statements in the setup() function 
    // execute once when the program begins
    void setup() {
        size(1024, 768)  // Size should be the first statement, XGA in the hope that's what the projector is
        // That size seems to have no effect, though; the preferredSize in AmortizedColaView does.
        frameRate(30)

//        for (i in 100..149) {
//            cola.insert(i, 'foo')
//        }
        println PFont.list()
        font16 = createFont("Liberation Sans Narrow", 16)
        font14 = createFont("Liberation Sans Narrow", 14)
        font12 = createFont("Liberation Sans Narrow", 12)
        font10 = createFont("Liberation Sans Narrow", 10)
        font8 = createFont("Liberation Sans Narrow", 8)
//        myFont = createFont("SansSerif", 10)
        fonts = [font16, font14, font12, font10, font8]
        noLoop()
    }

    // The statements in draw() are executed until the 
    // program is stopped. Each statement is executed in 
    // sequence and after the last line is read, the first 
    // line is executed again.
    void draw() {
        background(255)   // Set the background to white
        for (level in 1..cola.nLevels) {
            cola.levels[level].array.elements.eachWithIndex {element, idx ->
                int x, y, cellWidth
                PFont font
                (x, y, cellWidth, font) = cellCoordinate(level, idx)
                int centerX = x + (int)(cellWidth/2)
                int centerY = y + (int)(CELL_HEIGHT/2)
                fill(230)         // set rectangle filling color to light-grey
                stroke(0)     // Set line drawing color to black
                rect(x, y, cellWidth, CELL_HEIGHT)
                if (element?.key) {
                    if (element instanceof Item) {
                        fill(0)         // set text filling color to black
                    } else {
                        assert element instanceof RealLap
                        fill(red)    // set triangle and text filling color to red
                        stroke(red)    // set line drawing color to red
                        def (targetX, targetY, targetCellWidth, unused) = cellCoordinate(level + 1, element.index)
                        int targetCenterX = targetX + (int)(targetCellWidth/2)
                        line(centerX, y + CELL_HEIGHT, targetCenterX, (int) targetY-4)
                        triangle(targetCenterX, (int) targetY, targetCenterX-3, (int) targetY-4, targetCenterX+3, (int) targetY-4)
                    }
                    textFont(font)
                    textAlign(CENTER, CENTER)
                    translate(centerX, centerY)
                    if (font == fonts[3]) {
                        rotate(radians(-90))     // vertical text, bottom to top
                    }
                    text("$element.key" as String, 0, 0)
                    if (font == fonts[3]) {
                        rotate(radians(90))     // back to horizontal
                    }
                    translate(-centerX, -centerY)
                } else if (element instanceof DupLap) {
                    fill(0)         // set ellipse filling color to black
                    ellipse(centerX, centerY, 6, 6)
                }
            }
        }
        // controller.finishDraw()
        cola.insert(key, "value ${key++}")
    }

    void keyTyped() {
        redraw()
    }

    private cellCoordinate(int level, int index) {
        PFont font = fonts[0]
        int center = (int)(width/2)
        int nCells = 2**level
        int cellWidth = CELL_WIDTH
        int levelWidth = cellWidth * nCells
        if (levelWidth > width) {
            cellWidth = (int)(cellWidth / 3) + 1
            font = fonts[3]
            levelWidth = cellWidth * nCells
        } else if (levelWidth > width/2) {
            cellWidth = (int)(cellWidth * 2 / 3)
            font = fonts[2]
            levelWidth = cellWidth * nCells
        }
        int start = center - (int)(levelWidth/2)
        int x = start + index * cellWidth
        int y = LEVEL_SPACING * level + CELL_HEIGHT * (level - 1)
        [x, y, cellWidth, font]
    }
}
