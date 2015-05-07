package utils.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import utils.ReflectionHelper;

public class SaxHandler extends DefaultHandler {
    private static String ROOT_ELEMENT = "class";
    private static String ITEM_ELEMENT = "item";
    private static Logger LOGGER = LogManager.getLogger(SaxHandler.class);
    private String element = null;
    private Object object = null;
    private boolean item = false;
    private boolean list = false;

    public void startDocument() throws SAXException {
        LOGGER.debug("Start document");
    }

    public void endDocument() throws SAXException {
        LOGGER.debug("End document");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(ROOT_ELEMENT)) {
            String className = attributes.getValue(0);
            LOGGER.debug("Class name: {}", className);
            this.object = ReflectionHelper.createInstance(className);
        } else if (qName.equals(ITEM_ELEMENT)) {
            this.item = true;
        } else {
            this.element = qName;
            this.list = ReflectionHelper.isList(this.object, qName);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.item) {
            this.item = false;
        } else {
            this.element = null;
            this.list = false;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        String value = new String(ch, start, length);
        if (this.item) {
            LOGGER.debug("{} add \"{}\"", element, value);
            ReflectionHelper.addToList(this.object, this.element, value);
        } else if (this.element != null && !this.list) {
            LOGGER.debug("{} = {}", element, value);
            ReflectionHelper.setFieldValue(this.object, this.element, value);
        }
    }

    public Object getObject() {
        return object;
    }
}
