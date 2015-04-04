package utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PageGeneratorTest extends TestCase {

    protected static final Map<String, Object> DATA = new HashMap<>();
    protected static final String FILENAME = "some_template.html";
    protected static final String TEMPLATES_DIR = "/my/templates_dir";

    @Mock
    protected Configuration configuration;

    protected PageGenerator pageGenerator;

    @Mock
    protected Template template;

    @Before
    public void setUp() {
        this.pageGenerator = new PageGenerator(TEMPLATES_DIR, this.configuration);
    }

    @Test
    public void testGetPageOK() throws Exception {
        PageGenerator pageGenerator = new PageGenerator(TEMPLATES_DIR, configuration);
        final String TEXT = "foo bar baz";
        doAnswer((InvocationOnMock invocation) -> ((Writer) invocation.getArguments()[1]).append(TEXT)).when(this.template).process(eq(DATA), any(Writer.class));
        when(configuration.getTemplate(PageGeneratorTest.TEMPLATES_DIR + File.separator + FILENAME)).thenReturn(this.template);
        assertEquals(TEXT, pageGenerator.getPage(FILENAME, DATA));
        reset(this.configuration, this.template);
    }
}
