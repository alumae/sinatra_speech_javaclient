package ee.phon.ioc.awebrec.client;



import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;




import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;


class XMLUtils {

	
    public static Map<String, String> load(Reader reader) throws IOException {
		try {        
	        // Load XML into JDOM Document
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db;
	
			db = dbf.newDocumentBuilder();
	    	
	        Document doc = db.parse(new InputSource(reader));
	        doc.getDocumentElement().normalize();
	        
	        Map<String, String> result = new HashMap<String, String>();
	        loadFromElements(result, doc.getDocumentElement().getChildNodes(), 
	        		new StringBuffer(doc.getDocumentElement().getNodeName()));
	        return result;
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
    }    
	
	

	
    /**
     * <p>This helper method loads the XML properties from a specific
     *   XML element, or set of elements.</p>
     *
     * @param nodeList <code>List</code> of elements to load from.
     * @param baseName the base name of this property.
     */
    private static void loadFromElements(Map<String, String> result, NodeList nodeList, StringBuffer baseName) {
        // Iterate through each element
    	for (int s = 0; s < nodeList.getLength(); s++) {
    		Node current = nodeList.item(s);
    		if (current.getNodeType() == Node.ELEMENT_NODE) {
	            String name = current.getNodeName();
	            String text = null;
	            NodeList childNodes = current.getChildNodes();
	            
	            if (childNodes.getLength() > 0) {
	            	text = current.getChildNodes().item(0).getNodeValue();
	            } 
	            
	            // String text = current.getAttributeValue("value");            
	            
	            // Don't add "." if no baseName
	            if (baseName.length() > 0) {
	                baseName.append(".");
	            }            
	            baseName.append(name);
	            
	            // See if we have an element value
	            if ((text == null) || (text.equals(""))) {
	                // If no text, recurse on children
	                loadFromElements(result, current.getChildNodes(),
	                                 baseName);
	            } else {                
	                // If text, this is a property
	                result.put(baseName.toString(), text);
	            }            
	            
	            // On unwind from recursion, remove last name
	            if (baseName.length() == name.length()) {
	                baseName.setLength(0);
	            } else {                
	                baseName.setLength(baseName.length() - 
	                    (name.length() + 1));
	            }
    		}
        }        
    }    	
}
