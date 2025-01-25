/*
Freeplane Export nodes, notes and details to xhtml
Notes and Details are exported as plain text rather than details.

Version History
V0.1 original script (export to OPML, by Adxsoft)
V0.2. Modified to export to XHTML (with attributes)

Installation
1. Open the User Directory in Freeplane (Tools/Open User directory)
2. Open the scripts folder
3. Save this script as 'Export to XHTML.groovy' in the scripts folder
4. Restart Freeplane

To Use Script
1. Select the node you wish to export from (generally would be the root node of your map)
2. In Freeplane 'Tools' menu select 'Scripts'
3. Choose the script 'Export to XHTML'
4. Enter the saved file name when requested
5. XHTML nodes, notes ande details will be exported from the selected node and its childr
*/

import javax.swing.JFileChooser

class XHTMLmodWriter {

    def rootNode;
    def output=''
    final INDENT= "    ";
    def outline_tag='<li $$attributes$$>'
    def indentSpace(level) {
    	return INDENT*(level+1);
    }

    def processNode(newNode, childPosition) {
        def nodeLevel = newNode.getNodeLevel(true)

	output += '<span class="nodetext">' + newNode.plainText + '</span>'

	if (newNode.noteText) {
	   output +="\n" + indentSpace(nodeLevel)
	   output += '<div class="note">' + removeHtmlTags(newNode.noteText) + '</div>\n'
        }
        if (newNode.detailsText) {
	   output +="\n" + indentSpace(nodeLevel)
	   output += '<div class="details">'+ removeHtmlTags(newNode.detailsText) + '</div>\n'
        }
        if (newNode.attributes) {
	   output +="\n" + indentSpace(nodeLevel)
	   output += '<div class="attributes"><table>\n'

	  def nodeAttributes = newNode.attributes
	  def att_table = indentSpace(nodeLevel)
	  nodeAttributes.names.eachWithIndex { name,i -> att_table+="<tr><td>"+name+"</td><td>"+nodeAttributes.get(i)+"</td></tr>\n"}
	  output += att_table
	   output += '</table></div>\n'
        }

        def i = 0
        if (newNode.children) {
	   output += indentSpace(nodeLevel) + "<ul>\n"
            newNode.children.each {
                i++
		output +=  indentSpace(nodeLevel+1) +"<li>"
		processNode(it, i)
		output+="    "*(nodeLevel+1)+'</li>\n'
            }
	   output += indentSpace(nodeLevel) + "</ul>\n"
        }
    }


    def removeHtmlTags(text) {
        def strippedText = text.replaceAll('\n\\s*','\n') // remove extra spaces after line breaks
        strippedText = strippedText.replaceAll('<.*?>', '') // remove anythiing in between < and >
        strippedText = strippedText.replaceAll('^\\s*', '') // remove whitespace
        strippedText = strippedText.replaceAll('\n\n\n','\n') // replace multiple line feed with single line feed
        return strippedText
    }

    def traverse() {
    	output += '<div class="rootnode">'
        processNode(rootNode, 0)
    	output += '</div>'
    }

}

//=================
// MAIN ENTRY POINT
//=================


// get saved file details
chooser = new JFileChooser()
chooser.setDialogTitle('Export to XHTML from selected node')
returnVal = chooser.showSaveDialog();
if (returnVal == JFileChooser.APPROVE_OPTION) {

    // save file chosen so commence export

    c.statusInfo = 'User will save to file ' + chooser.selectedFile
    xhtmlfilename = chooser.selectedFile.toString()

    // instantiate the XHTMLWriter class and set
    // starting node to selected node ('node')
    def xhtmlwriter = new XHTMLmodWriter(rootNode:node);

    // walk through the selected node and its children
    xhtmlwriter.traverse()

    // build the xhtml file
    //
    // .. header
    xhtmldata="""
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>$node.text</title>
    </head>
    <body>
"""
    // .. main body - outline tags
    xhtmldata+=xhtmlwriter.output

    // .. tail
    xhtmldata+="""
</body>
</html>
"""

    // write the output xhtml file
    def outputfile = new File(xhtmlfilename)
    outputfile.write(xhtmldata.toString())

    c.statusInfo = 'Export to xhtml file ' + chooser.selectedFile+' completed.'

} else {
    c.statusInfo="Export XHTML cancelled by user"
}
