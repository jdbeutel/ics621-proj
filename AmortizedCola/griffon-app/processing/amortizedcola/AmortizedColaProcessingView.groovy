package amortizedcola

import griffon.plugins.processing.artifact.AbstractGriffonProcessingView
import processing.core.PFont

class AmortizedColaProcessingView extends AbstractGriffonProcessingView {

    // private static final String FONT_NAME = "Liberation Sans Narrow"     // Linux
    private static final String FONT_NAME = "ArialNarrow"     // Mac
    // private static final String FONT_NAME = "PTSans-Narrow"     // Mac

    AmortizedColaModel model
    AmortizedColaController controller
    Cola cola = new Cola()
    def rand = null
    def sequence = 1
    def insertKey
    def searchKey
    String searchResult = null
    def searchRand = new Random(6)
    def contents = [:]

    PFont font4, font6, font8, font10, font12, font14, font16, myFont
    def fonts
    def red = color(255, 0, 0)
//    def green = color(0, 255, 0)
    def darkGreen = color(43, 117, 0)
    def blue = color(0, 0, 255)
    def lightCyan = color(132, 238, 250)
    def darkBlue = color(34, 25, 209)

    final static HEADER = 30
    final static CELL_WIDTH = 28
    final static CELL_HEIGHT = 20
    final static LEVEL_SPACING = 40

    // The statements in the setup() function 
    // execute once when the program begins
    void setup() {
        size(1024, 768)  // Size should be the first statement, XGA in the hope that's what the projector is
        // That size seems to have no effect, though; the preferredSize in AmortizedColaView does.
        frameRate(30)

        println PFont.list()
        font16 = createFont(FONT_NAME, 16)
        font14 = createFont(FONT_NAME, 14)
        font12 = createFont(FONT_NAME, 12)
        font10 = createFont(FONT_NAME, 10)
        font8 = createFont(FONT_NAME, 8)
        font6 = createFont(FONT_NAME, 6)
        font4 = createFont(FONT_NAME, 4)
        myFont = createFont("SansSerif", 18)
        fonts = [font16, font14, font12, font10, font8, font6, font4]
        noLoop()
    }

    private drawLegend() {
        def (x, y) = cellCoordinate(1, 1)
        def (southWestX, southWestY) = cellCoordinate(4, 0)
        fill(lightCyan)
        stroke(darkBlue)
        rect((int) southWestX-10, (int) y-10, (int) (x - southWestX) * 2 + 20, (int) southWestY - y + CELL_HEIGHT + 20)
        textFont(myFont)
        textAlign(RIGHT, TOP)
        fill(darkBlue)
        text("N = ${cola.getN()}" as String, (int) southWestX-20, (int) y)
        text("seeks = ${cola.nSeeks}" as String, (int) southWestX-20, (int) y + 20)
        text("inserts = ${cola.nInserts}" as String, (int) southWestX-20, (int) y + 40)
        text("searches = ${cola.nSearches}" as String, (int) southWestX-20, (int) y + 60)
        text("RAM" as String, (int) x + (x - southWestX) - 10, (int) y + 80)
        textAlign(LEFT, TOP)
        text("disk" as String, (int) x + (x - southWestX) + 30, (int) y + 110)
    }

    synchronized void draw() {
        background(255)   // Set the background to white
        drawLegend()
        for (level in 1..cola.nLevels) {
            cola.levels[level].array.elements.eachWithIndex {element, idx ->
                drawElement(level, element, idx)
            }
            if (cola.merging) {
                stroke(blue)
                fill(blue)
                textFont(myFont)
                textAlign(CENTER, TOP)
                text("inserting $insertKey" as String, (int) width/2, 5)
                def a = cola.levels[level].array
                if (level == cola.mergeTarget && a.nRight) {
                    assert a.lowerBoundInclusive
                    drawCursor(level, a.lowerBoundInclusive)
                } else if (level < cola.mergeTarget && cola.nextItems[level]) {
                    drawCursor(level, (int) cola.iterators[level].mostRecent)
                }
            }
            if (cola.searching || searchResult) {
                stroke(darkGreen)
                fill(darkGreen)
                def a = cola.levels[level].array
                if (level == cola.searchLevel) {
                    drawCursor(level, (int) a.mostRecentSearchIndex)
                }
            }
        }
        if (cola.searching) {
            String msg = "searching ${contents[searchKey] == null ? '(missing)' : ''} $searchKey"
            drawSearchMsg(msg)
        }
        if (searchResult) {
            String msg = "search completed: $searchResult"
            drawSearchMsg(msg)
            searchResult = null
        }
    }

    void drawSearchMsg(String msg) {
        textFont(myFont)
        textAlign(CENTER, TOP)
        text(msg, (int) width/2, HEADER)
    }

    void drawCursor(int level, int index) {
        int x, y, cellWidth
        (x, y, cellWidth) = cellCoordinate(level, index)
        int centerX = x + (int)(cellWidth/2)
        int bottomY = y + CELL_HEIGHT
        triangle(centerX, bottomY+1, centerX-5, bottomY+10, centerX+5, bottomY+10)
    }

    void drawElement(int level, Element element, int idx) {
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
                def (targetX, targetY, targetCellWidth) = cellCoordinate(level + 1, element.index)
                int targetCenterX = targetX + (int)(targetCellWidth/2)
                line(centerX, y + CELL_HEIGHT, targetCenterX, (int) targetY-6)
                triangle(targetCenterX, (int) targetY-1, targetCenterX-3, (int) targetY-5, targetCenterX+3, (int) targetY-5)
            }
            textFont(font)
            textAlign(CENTER, CENTER)
            translate(centerX, centerY)
            if (font == fonts[3] || font == fonts[4]) {
                rotate(radians(-90))     // vertical text, bottom to top
            }
            text("$element.key" as String, 0, 0)
            if (font == fonts[3] || font == fonts[4]) {
                rotate(radians(90))     // back to horizontal
            }
            translate(-centerX, -centerY)
        } else if (element instanceof DupLap) {
            fill(0)         // set ellipse filling color to black
            int diameter = cellWidth < CELL_WIDTH/4 ? 3 : cellWidth < CELL_WIDTH/2 ? 4 : 6
            ellipse(centerX, centerY, diameter, diameter)
            stroke(0)   // set line drawing color to black
            int dupCrossY = (int)(y - LEVEL_SPACING/2)
            line(centerX, centerY, centerX, dupCrossY)
            if (element.left) {
                drawDupArrow(centerX, dupCrossY, level, element.left)
            }
            if (element.right) {
                drawDupArrow(centerX, dupCrossY, level, element.right)
            }
        }
    }

    private void drawDupArrow(int centerX, int dupCrossY, int level, int index) {
        def (targetX, targetY, targetCellWidth) = cellCoordinate(level, index)
        int halfCellWidth = (int)(targetCellWidth/2)
        int targetSideX = targetX + halfCellWidth + 3 * Integer.signum((int) centerX - targetX)
        line(centerX, dupCrossY, targetSideX, dupCrossY)
        line(targetSideX, dupCrossY, targetSideX, (int) targetY)
        triangle(targetSideX, (int) targetY-1, targetSideX-2, (int) targetY-4, targetSideX+2, (int) targetY-4)
    }

    private void reset() {
        cola = new Cola()
        contents = [:]
        insertKey = null
        searchKey = null
        searchResult = null
        searchRand = new Random(6)
    }

    synchronized void keyTyped() {
        if (key == 'R') {
            rand = new Random(42)
            reset()
        } else if (key == 'C') {
            sequence = 1
            rand = null
            reset()
        } else if (key == 'i' && !cola.merging && !cola.searching) {
            insertKey = nextInsertKey
            def value = "value $insertKey"
            contents[insertKey] = value
            cola.insert(insertKey, value, false)
        } else if (key == 'I' && !cola.merging && !cola.searching) {
            insertKey = nextInsertKey
            def value = "value $insertKey"
            contents[insertKey] = value
            cola.insert(insertKey, "value $insertKey", true)
        } else if (key == 's' && !cola.searching && !cola.merging) {
            searchKey = randomSearchKey()
            publishSearchResult(cola.search(searchKey, false))
        } else if (key == 'S' && !cola.searching && !cola.merging) {
            searchKey = randomSearchKey()
            if (!cola.search(searchKey, true)) {
                publishSearchResult(cola.searchResult)
            }
        } else if (key == ' ') {
            if (cola.merging) {
                cola.mergeStep()
            }
            if (cola.searching) {
                if (!cola.searchStep(true)) {
                    publishSearchResult(cola.searchResult)
                }
            }
        }
        redraw()
    }

    private randomSearchKey() {
        if (contents.size() && searchRand.nextInt(100) < 66) {
            def keys = contents.keySet() as List
            return keys[searchRand.nextInt(keys.size())]
        } else {
            return searchRand.nextInt(999)
        }
    }

    private publishSearchResult(result) {
        searchResult = result == null ? 'missing' : "found $result"
    }

    def getNextInsertKey() {
        if (rand) {
            rand.nextInt(999)
        } else {
            sequence++
        }
    }

    private cellCoordinate(int level, int index) {
        PFont font = fonts[0]
        int center = (int)(width/2)
        int nCells = 2**level
        int cellWidth = CELL_WIDTH
        int levelWidth = cellWidth * nCells
        if (levelWidth > width/2) {
            cellWidth = (int)(cellWidth * 2 / 3)
            font = fonts[2]
            levelWidth = cellWidth * nCells
        }
        if (levelWidth > width) {
            cellWidth = (int)(cellWidth / 2) + 1
            font = fonts[3]
            levelWidth = cellWidth * nCells
        }
        if (levelWidth > width) {
            cellWidth = (int)(cellWidth / 2) + 1
            font = fonts[4]
            levelWidth = cellWidth * nCells
        }
        int start = center - (int)(levelWidth/2)
        int x = start + index * cellWidth
        int y = LEVEL_SPACING * level + CELL_HEIGHT * level + HEADER
        [x, y, cellWidth, font]
    }
}
