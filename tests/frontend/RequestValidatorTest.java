package frontend;

import base.RequestValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestValidatorTest {

    @Test
    public void testAreRequiredFieldsValidStrings() throws Exception {
        Map<Object, Object> request = new HashMap<>();
        request.put("foo", "baz");
        request.put("bar", "baz");
        request.put("baz", "boo");
        Map<Object, Object> body = new HashMap<>();
        final String[] REQUIRED_FIELDS = {"foo", "bar", "baz"};
        RequestValidator validator = new RequestValidator(REQUIRED_FIELDS);
        assertTrue(validator.areRequiredFieldsValid(request, body));
    }

    @Test
    public void testAreRequiredFieldsValidStringsPlusInts() throws Exception {
        Map<Object, Object> request = new HashMap<>();
        request.put("foo", "baz");
        request.put("bar", (long) 31416);
        request.put("baz", "boo");
        Map<Object, Object> body = new HashMap<>();
        final String[] REQUIRED_FIELDS = {"foo", "int bar", "baz"};
        RequestValidator validator = new RequestValidator(REQUIRED_FIELDS);
        assertTrue(validator.areRequiredFieldsValid(request, body));
    }

    @Test
    public void testAreRequiredFieldsValidStringsNoString() throws Exception {
        Map<Object, Object> request = new HashMap<>();
        request.put("foo", "baz");
        request.put("bar", (long) 31416);
        Map<Object, Object> body = new HashMap<>();
        final String[] REQUIRED_FIELDS = {"foo", "int bar", "baz"};
        RequestValidator validator = new RequestValidator(REQUIRED_FIELDS);
        assertFalse(validator.areRequiredFieldsValid(request, body));
        Map<Object, Object> error = (Map<Object, Object>) body.get("baz");
        assertEquals("required", error.get("error"));
        assertNull(error.get("value"));
    }

    @Test
    public void testAreRequiredFieldsValidStringsBadInt() throws Exception {
        Map<Object, Object> request = new HashMap<>();
        request.put("foo", "baz");
        request.put("bar", "31416");
        request.put("baz", "boo");
        Map<Object, Object> body = new HashMap<>();
        final String[] REQUIRED_FIELDS = {"foo", "int bar", "baz"};
        RequestValidator validator = new RequestValidator(REQUIRED_FIELDS);
        assertFalse(validator.areRequiredFieldsValid(request, body));
        Map<Object, Object> error = (Map<Object, Object>) body.get("bar");
        assertEquals("wrong_type", error.get("error"));
        assertEquals("31416", error.get("value"));
    }

    @Test
    public void testAreRequiredFieldsValidStringsNoInt() throws Exception {
        Map<Object, Object> request = new HashMap<>();
        request.put("foo", "baz");
        request.put("baz", "boo");
        Map<Object, Object> body = new HashMap<>();
        final String[] REQUIRED_FIELDS = {"foo", "int bar", "baz"};
        RequestValidator validator = new RequestValidator(REQUIRED_FIELDS);
        assertFalse(validator.areRequiredFieldsValid(request, body));
        Map<Object, Object> error = (Map<Object, Object>) body.get("bar");
        assertEquals("required", error.get("error"));
        assertNull(error.get("value"));
    }

    @Test
    public void testAreRequiredFieldsValidDoesntPropagatesPassword() throws Exception {
        Map<Object, Object> request = new HashMap<>();
        request.put("foo", "baz");
        request.put("bar", 31416);
        request.put("password", 271828);
        Map<Object, Object> body = new HashMap<>();
        final String[] REQUIRED_FIELDS = {"foo", "int bar", "int password"};
        RequestValidator validator = new RequestValidator(REQUIRED_FIELDS);
        assertFalse(validator.areRequiredFieldsValid(request, body));
        Map<Object, Object> error = (Map<Object, Object>) body.get("password");
        assertEquals("wrong_type", error.get("error"));
        assertNull(error.get("password"));
    }
}