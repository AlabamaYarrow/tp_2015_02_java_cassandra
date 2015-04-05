package utils.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import utils.ReflectionHelper;

public class SaxHandler extends DefaultHandler {
    private static String ROOT_ELEMENT = "class";
    private static Logger LOGGER = LogManager.getLogger(SaxHandler.class);
    private String element = null;
    private Object object = null;

    public void startDocument() throws SAXException {
        LOGGER.debug("Start document");
    }

    public void endDocument() throws SAXException {
        LOGGER.debug("End document ");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(ROOT_ELEMENT)) {
            String className = attributes.getValue(0);
            LOGGER.debug("Class name: {}", className);
            object = ReflectionHelper.createInstance(className);
        } else {
            element = qName;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        element = null;
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        if (element != null) {
            String value = new String(ch, start, length);
            LOGGER.debug("{} = {}", element, value);
            ReflectionHelper.setFieldValue(object, element, value);
        }
    }

    public Object getObject() {
        return object;
    }
}
