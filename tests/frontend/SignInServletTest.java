package frontend;

import base.UserProfileTest;
import main.UserProfile;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class SignInServletTest extends UserProfileTest {

    @Test
    public void testDoPostNoPassword() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        /* Password isn't specified. */
        String requestJson = String.format("{ \"name\": \"Paul\" }");
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        Map<Object, Object> json = (Map<Object, Object>) JSONValue.parse(response.toString());
        Map<Object, Object> body = (Map<Object, Object>) json.get("body");
        Map<Object, Object> error = (Map<Object, Object>) body.get("password");
        assertEquals("required", error.get("error"));
        assertNull(error.get("value"));
    }

    @Test
    public void testDoPostInvalidJson() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest("not a json");

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);
    }

    @Test
    public void testDoPostInvalidPassword() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        /* Password is a number. */
        String requestJson = String.format("{ \"name\": \"Paul\", \"password\": 31415 }");
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        Map<Object, Object> json = (Map<Object, Object>) JSONValue.parse(response.toString());
        Map<Object, Object> body = (Map<Object, Object>) json.get("body");
        Map<Object, Object> error = (Map<Object, Object>) body.get("password");
        assertEquals("wrong_type", error.get("error"));
        assertNull(error.get("value"));
    }

    @Test
    public void testDoPostInvalidName() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        /* Name is a number. */
        final long INVALID_NAME = 31415;
        String requestJson = String.format("{ \"name\": %d, \"password\": \"topsecret\" }", INVALID_NAME);
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response);

        Map<Object, Object> json = (Map<Object, Object>) JSONValue.parse(response.toString());
        Map<Object, Object> body = (Map<Object, Object>) json.get("body");
        Map<Object, Object> error = (Map<Object, Object>) body.get("name");
        assertEquals("wrong_type", error.get("error"));
        assertEquals(INVALID_NAME, error.get("value"));
    }

    @Test
    public void testDoPostForbidden() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user, "");

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_FORBIDDEN, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        assertNotNull(body);
    }

    @Test
    public void testDoPostOK() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        String requestJson = String.format("{ \"name\": \"%s\", \"password\": \"topsecret\" }", user.getName());
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_OK, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, body);
    }

    @Test
    public void testDoPostUnauthorized() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        String requestJson = String.format("{ \"name\": \"%s\", \"password\": \"not_a_secret\" }", user.getName());
        HttpServletRequest request = this.getMockedRequest(requestJson);

        signIn.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_UNAUTHORIZED, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        assertNotNull(body.get("message"));
    }
}