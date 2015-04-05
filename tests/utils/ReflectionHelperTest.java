package utils;

import junit.framework.TestCase;
import org.junit.Test;
import resources.GameResource;

public class ReflectionHelperTest extends TestCase {

    @Test
    public void testCreateInstance() throws Exception {
        Object object = ReflectionHelper.createInstance(GameResource.class.getName());
        assertTrue(object instanceof GameResource);
    }

    @Test
    public void testSetFieldValue() throws Exception {
        GameResource gameResource = new GameResource();
        final int JUDGES_COUNT = 31415926;
        ReflectionHelper.setFieldValue(gameResource, "judgesCount", String.valueOf(JUDGES_COUNT));
        assertEquals(JUDGES_COUNT, gameResource.judgesCount);
    }
}