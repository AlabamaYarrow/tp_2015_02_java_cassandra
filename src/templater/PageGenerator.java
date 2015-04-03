package templater;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PageGenerator {

    static final Logger LOGGER = LogManager.getLogger(PageGenerator.class);

    protected final String TEMPLATES_DIR;
    protected final Configuration CFG;

    public PageGenerator(String templatesDir, Configuration configuration) {
        this.TEMPLATES_DIR = templatesDir;
        this.CFG = configuration;
    }

    public String getPage(String filename, Map<String, Object> data) {
        Writer stream = new StringWriter();
        try {
            Template template = this.CFG.getTemplate(this.TEMPLATES_DIR + File.separator + filename);
            template.process(data, stream);
        } catch (IOException | TemplateException e) {
            LOGGER.error("Troubles with template", e);
        }
        return stream.toString();
    }
}
