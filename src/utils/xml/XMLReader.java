package utils.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XMLReader {

    private static Logger LOGGER = LogManager.getLogger(XMLReader.class);

    public static Object read(String path) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            SaxHandler handler = new SaxHandler();
            parser.parse(path, handler);

            return handler.getObject();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}
