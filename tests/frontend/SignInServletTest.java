package frontend;

import base.UserProfileTest;
import junit.framework.TestCase;
import main.UserProfile;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class SignInServletTest extends UserProfileTest {

    @Test
    public void testDoPostBadRequest() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getMockedRequest();
        when(request.getParameter("name")).thenReturn(user.getName());
        /* Password isn't specified. */

        signIn.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response, json);

        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("password");
        TestCase.assertEquals("required", error.get("error"));
        TestCase.assertNull(error.get("value"));
    }

    @Test
    public void testDoPostForbidden() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user);

        signIn.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_FORBIDDEN, response, json);

        JSONObject body = (JSONObject) json.get("body");
        TestCase.assertNotNull(body);
    }

    @Test
    public void testDoPostOK() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getMockedRequest();
        when(request.getParameter("name")).thenReturn(user.getName());
        when(request.getParameter("password")).thenReturn("topsecret");

        signIn.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_OK, response, json);

        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, body);
    }

    @Test
    public void testDoPostUnauthorized() throws Exception {
        SignInServlet signIn = new SignInServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getMockedRequest();
        when(request.getParameter("name")).thenReturn(user.getName());
        when(request.getParameter("password")).thenReturn("not_a_secret");

        signIn.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_UNAUTHORIZED, response, json);

        JSONObject body = (JSONObject) json.get("body");
        TestCase.assertNotNull(body.get("message"));
    }
}