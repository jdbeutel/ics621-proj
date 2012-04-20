package hellojung

import edu.uci.ics.jung.graph.Graph
import edu.uci.ics.jung.graph.SparseMultigraph
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import edu.uci.ics.jung.visualization.renderers.Renderer
import org.apache.commons.collections15.Transformer
import org.apache.commons.collections15.functors.ConstantTransformer
//import java.awt.Color
import org.codehaus.griffon.jsilhouette.geom.Star
import griffon.plugins.jung.visualization.decorators.TemplateVertexShapeTransformer

int max = 10
Graph g = new SparseMultigraph()
(1..max).each { Integer i -> g.addVertex(i) }
(1..<max).each { Integer x -> g.addEdge("Edge-$x", x, x+1) }
g.addEdge("Edge-${max}", max, 1)

def colorMap = [:]
Random r = new Random()
def paintTransformer = { Integer i ->
    colorMap.get(i, new Color(r.nextInt(64)+192i, 0i, 0i))
} as Transformer

bean(id: "star", new Star(), or: 40f, ir: 20f, count: 5i)
bean(id: "starrer", new TemplateVertexShapeTransformer(star), sizeTransformer: new ConstantTransformer(40i))

application(title: 'JUNG + Griffon',
        pack: true,
        locationByPlatform:true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    visualizationViewer(g, id: "gview", preferredSize: [350,350],
            vertexShapeTransformer: starrer, edgeLabelTransformer: new ToStringLabeller(),
            vertexLabelTransformer: new ToStringLabeller(), vertexFillPaintTransformer: paintTransformer) {
        circleLayout(size: [330, 330])
        defaultModalGraphMouse()
    }
    gview.renderer.vertexLabelRenderer.position = Renderer.VertexLabel.Position.CNTR
}
//application(title: 'HelloJung',
//        preferredSize: [320, 240],
//        pack: true,
//        //location: [50,50],
//        locationByPlatform:true,
//        iconImage: imageIcon('/griffon-icon-48x48.png').image,
//        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
//                imageIcon('/griffon-icon-32x32.png').image,
//                imageIcon('/griffon-icon-16x16.png').image]) {
//    // add content here
//    label('Content Goes Here') // delete me
//}

