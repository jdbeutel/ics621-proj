package amortizedcola

import griffon.plugins.processing.artifact.AbstractGriffonProcessingView
import processing.core.PFont
import btree.InstrumentedMemoryPageStorage
import com.sun.electric.database.geometry.btree.CachingPageStorage
import com.sun.electric.database.geometry.btree.CachingPageStorageWrapper
import com.sun.electric.database.geometry.btree.unboxed.Pair
import com.sun.electric.database.geometry.btree.BTree
import com.sun.electric.database.geometry.btree.unboxed.UnboxedInt
import btree.FatUnboxedInt
import com.sun.electric.database.geometry.btree.LeafNodeCursor
import com.sun.electric.database.geometry.btree.NodeCursor
import com.sun.electric.database.geometry.btree.InteriorNodeCursor

class AmortizedColaProcessingView extends AbstractGriffonProcessingView {

    private static final String FONT_NAME = "Liberation Sans Narrow"     // Linux
    // private static final String FONT_NAME = "ArialNarrow"     // Mac
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

    static final MAX_CACHE_PAGES = 8

    InstrumentedMemoryPageStorage imps = new InstrumentedMemoryPageStorage()
    CachingPageStorage ps = new CachingPageStorageWrapper(imps, MAX_CACHE_PAGES, false)
    BTree<Integer, Integer, Pair<Integer, Integer>> btree =
        new BTree<Integer,Integer,Pair<Integer,Integer>>(ps, UnboxedInt.instance, FatUnboxedInt.instance, null, null)
    int btreeDegree = new InteriorNodeCursor(btree).INTERIOR_MAX_BUCKETS

    PFont font4, font6, font8, font10, font12, font14, font16, myFont
    def fonts
    def red = color(255, 0, 0)
//    def green = color(0, 255, 0)
    def darkGreen = color(43, 117, 0)
    def blue = color(0, 0, 255)
    def lightCyan = color(132, 238, 250)
    def darkBlue = color(34, 25, 209)

    final static HEADER = 10
    final static CELL_WIDTH = 28
    final static CELL_HEIGHT = 20
    final static LEVEL_SPACING = 20

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
        rect((int) southWestX-7, (int) y-10, (int) (x - southWestX) * 2 + 20, (int) southWestY - y + CELL_HEIGHT + 14)
        textFont(myFont)
        textAlign(RIGHT, TOP)
        fill(darkBlue)
        text("Amortized Cola (k-way)" as String, (int) southWestX-20, (int) y)
        text("N = ${cola.getN()}" as String, (int) southWestX-20, (int) y + 20)
        text("deamortized seeks = ${cola.nSeeks}" as String, (int) southWestX-20, (int) y + 40)
        text("inserts = ${cola.nInserts}" as String, (int) southWestX-20, (int) y + 60)
        text("searches = ${cola.nSearches}" as String, (int) southWestX-20, (int) y + 80)
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
        drawBTree()
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
                def (targetX, targetY, targetCellWidth) = cellCoordinate(level + 1, element.index)
                drawLookAheadArrow(centerX, y, targetX, targetY, targetCellWidth)
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
            int dupCrossY = (int)(y - LEVEL_SPACING/2 + 2)
            line(centerX, centerY, centerX, dupCrossY)
            if (element.left) {
                drawDupArrow(centerX, dupCrossY, level, element.left)
            }
            if (element.right) {
                drawDupArrow(centerX, dupCrossY, level, element.right)
            }
        }
    }

    private void drawLookAheadArrow(centerX, y, targetX, targetY, targetCellWidth) {
        fill(red)    // set triangle and text filling color to red
        stroke(red)    // set line drawing color to red
        int targetCenterX = targetX + (int)(targetCellWidth/2)
        line((int) centerX, (int) y + CELL_HEIGHT, targetCenterX, (int) targetY-6)
        triangle(targetCenterX, (int) targetY-1, targetCenterX-3, (int) targetY-5, targetCenterX+3, (int) targetY-5)
    }

    private void drawDupArrow(int centerX, int dupCrossY, int level, int index) {
        def (targetX, targetY, targetCellWidth) = cellCoordinate(level, index)
        int halfCellWidth = (int)(targetCellWidth/2)
        int targetSideX = targetX + halfCellWidth + 3 * Integer.signum((int) centerX - targetX)
        line(centerX, dupCrossY, targetSideX, dupCrossY)
        line(targetSideX, dupCrossY, targetSideX, (int) targetY)
        triangle(targetSideX, (int) targetY-1, targetSideX-2, (int) targetY-4, targetSideX+2, (int) targetY-4)
    }

    private void drawBTree() {
        drawBTreePage([], btree.rootpage, 'X')
    }

    private void drawBTreePage(List<Integer> path, int pageid, parentKey) {
        CachingPageStorage.CachedPage cp = ps.getPage(pageid, true)

        // def cur = LeafNodeCursor.isLeafNode(cp) ? new LeafNodeCursor(btree) : new InteriorNodeCursor(btree);
        def isLeafNode = UnboxedInt.instance.deserializeInt(cp.getBuf(), 1* 4/*SIZEOF_INT*/)==0
        // def cur = isLeafNode ? new LeafNodeCursor(btree) : new InteriorNodeCursor(btree)
        def cur
        if (isLeafNode) {
            cur = new LeafNodeCursor(btree)
        } else {
            cur = new InteriorNodeCursor(btree)
        }
        // def cur = new LeafNodeCursor(btree)

        cur.setBuf(cp);
        for (int i = 0; i < cur.numBuckets; i++) {
            def key
            if (cur.leafNode || i) {
                def keyBuf = new byte[btree.uk.size]
                cur.getKey(i, keyBuf, 0)
                key = btree.uk.deserialize(keyBuf, 0)
            } else {
                // interior node has no key in its first bucket
                assert !cur.leafNode && i == 0
                key = parentKey
            }
            drawBTreeElement(path + i, key, cur.leafNode)
            if (!cur.leafNode) {
                // assert cur instanceof InteriorNodeCursor
                drawBTreePage(path + i, cur.getBucketPageId(i), key)
            }
        }
    }

    void drawBTreeElement(List<Integer> path, key, boolean leaf) {
        int x, y, cellWidth
        PFont font
        (x, y, cellWidth, font) = btreeCellCoordinate(path)
        int centerX = x + (int)(cellWidth/2)
        int centerY = y + (int)(CELL_HEIGHT/2)
        fill(230)         // set rectangle filling color to light-grey
        stroke(0)     // Set line drawing color to black
        rect(x, y, cellWidth, CELL_HEIGHT)
        if (leaf) {
            fill(0)      // set text filling color to black
        } else {
            fill(red)    // set text filling color to red
            stroke(red)  // Set line drawing color to red
        }
        textFont(font)
        textAlign(CENTER, CENTER)
        translate(centerX, centerY)
        if (font == fonts[3] || font == fonts[4]) {
            rotate(radians(-90))     // vertical text, bottom to top
        }
        text("$key" as String, 0, 0)
        if (font == fonts[3] || font == fonts[4]) {
            rotate(radians(90))     // back to horizontal
        }
        translate(-centerX, -centerY)
        if (!leaf) {
            stroke(red)  // Set line drawing color to red
            def (childX, childY, childCellWidth) = btreeCellCoordinate(path + 0)
            drawLookAheadArrow(centerX, y, childX, childY, childCellWidth)
        }
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
            if (btree.getValFromKey(insertKey)!=null) {
                btree.replace(insertKey, insertKey);
            } else {
                btree.insert(insertKey, insertKey);
            }
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
        int nCells = 2**level
        def (x, cellWidth, font) = cellX(0, width, nCells, index)
        int y = LEVEL_SPACING * level + CELL_HEIGHT * level + HEADER
        [x, y, cellWidth, font]
    }

    private cellX(int left, int right, int nCells, int index) {
        PFont font = fonts[0]
        int center = (int)((left + right)/2)
        int cellWidth = CELL_WIDTH
        int levelWidth = cellWidth * nCells
        int availableWidth = right - left
        if (levelWidth > availableWidth/2) {
            cellWidth = (int)(cellWidth * 2 / 3)
            font = fonts[2]
            levelWidth = cellWidth * nCells
        }
        if (levelWidth > availableWidth) {
            cellWidth = (int)(cellWidth / 2) + 1
            font = fonts[3]
            levelWidth = cellWidth * nCells
        }
        if (levelWidth > availableWidth) {
            cellWidth = (int)(cellWidth / 2) + 1
            font = fonts[4]
            levelWidth = cellWidth * nCells
        }
        int start = center - (int)(levelWidth/2)
        int x = start + index * cellWidth
        [x, cellWidth, font]
    }

    private btreeCellCoordinate(List<Integer> path) {
        int left = 0
        int right = width
        int y = (int) (height / 2) + HEADER
        while (path.size() > 1) {
            int idx = path[0]
            assert idx < btreeDegree
            path = path.tail()
            int availableWidth = (int)((right - left)/(btreeDegree-1))
            left += idx * availableWidth
            right = left + availableWidth
            y += CELL_HEIGHT + LEVEL_SPACING
        }
        def (x, cellWidth, font) = cellX(left, right, btreeDegree, path[0])
        [x, y, cellWidth, font]
    }
}
