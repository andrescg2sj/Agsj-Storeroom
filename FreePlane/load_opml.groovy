import javax.swing.JFileChooser

import org.freeplane.plugin.script.proxy.Proxy

/**
 * @author Andres Gonzalez
 * Thanks to Cyril Crassin and Volker Boerchers for their examples
 * at the FreePlane script collection wiki page.
 */
 
JFileChooser chooser = new JFileChooser('.')
chooser.showOpenDialog()
File file = chooser.getSelectedFile()

if ( ! file)
	return

def rootNode = new XmlParser().parse(file)

assert rootNode.name() == 'opml'

Proxy.Node prevNode = node.map.root

def addNode(treeNode, xmlNode) {
    //println(xmlNode.@text);
    def newNode = treeNode.createChild(xmlNode.@text)
    xmlNode.children().each { addNode(newNode, it); }
}


rootNode.body[0].children().each { addNode(prevNode, it); }