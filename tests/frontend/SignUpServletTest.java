package frontend;

import base.UserProfileTest;
import base.dataSets.UserDataSet;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SignUpServletTest extends UserProfileTest {

    @Test
    public void testDoPostNoPassword() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        /* Password isn't specified. */
        String requestJson = "{ \"email\": \"paul@mail.com\", \"name\": \"Paul\" }";
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("password");
        assertEquals("required", error.get("error"));
        assertNull(error.get("value"));
    }

    @Test
    public void testDoPostInvalidPassword() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        /* Password is a number. */
        String requestJson = "{ \"email\": \"paul@mail.com\", \"name\": \"Paul\", \"password\": 31415 }";
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("password");
        assertEquals("wrong_type", error.get("error"));
        assertNull(error.get("value"));
    }

    @Test
    public void testDoPostInvalidName() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        /* Name is a number. */
        final long INVALID_NAME = 31415;
        String requestJson = String.format("{ \"email\": \"paul@mail.com\", \"name\": %d, \"password\": \"topsecret\" }", INVALID_NAME);
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("name");
        assertEquals("wrong_type", error.get("error"));
        assertEquals(INVALID_NAME, error.get("value"));
    }

    @Test
    public void testDoPostInvalidJson() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest("not a json string");

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);
    }

    @Test
    public void testDoPostAlreadyExists() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserDataSet user = this.createUserProfile();
        String requestJson = String.format("{ \"email\": \"%s\", \"name\": \"%s\", \"password\": \"topsecret\" }", user.getEmail(), user.getName());
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("name");
        assertEquals("already_exists", error.get("error"));
        assertEquals(user.getName(), error.get("value"));
    }

    @Test
    public void testDoPostForbidden() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserDataSet user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user, "");

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_FORBIDDEN, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        assertNotNull(body);
    }

    @Test
    public void testDoPostOK() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserDataSet user = mock(UserDataSet.class);
        final String EMAIL = "tom@mail.com";
        final String NAME = "Thomas";
        when(user.getID()).thenReturn(99901L);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getName()).thenReturn(NAME);
        String requestJson = String.format("{ \"email\": \"%s\", \"name\": \"%s\", \"password\": \"topsecret\" }", EMAIL, NAME);
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signUp.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_OK, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, body);
    }
}
