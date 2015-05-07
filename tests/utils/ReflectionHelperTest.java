package utils;

import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import resources.GameResource;

public class ReflectionHelperTest extends TestCase {

    private static final Logger LOGGER = LogManager.getLogger(ReflectionHelperTest.class);

    @Test
    public void testCreateInstance() throws Exception {
        Object object = ReflectionHelper.createInstance(GameResource.class.getName());
        assertTrue(object instanceof GameResource);
    }

    @Test
    public void testAddValue() throws Exception {
        GameResource gameResource = new GameResource();
        final String[] WORDS = {"Hello", "here"};
        ReflectionHelper.addToList(gameResource, "words", WORDS[0]);
        ReflectionHelper.addToList(gameResource, "words", WORDS[1]);
        assertEquals(WORDS[0], gameResource.words.get(0));
        assertEquals(WORDS[1], gameResource.words.get(1));
    }
}