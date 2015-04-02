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

public class SignUpServletTest extends UserProfileTest {

    @Test
    public void testDoPostBadRequest() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = mock(UserProfile.class);
        final String EMAIL = "paul@mail.com";
        final String NAME = "Paul";
        when(user.getID()).thenReturn(99900);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getName()).thenReturn(NAME);
        HttpServletRequest request = this.getMockedRequest();
        when(request.getParameter("email")).thenReturn(EMAIL);
        when(request.getParameter("name")).thenReturn(NAME);
        /* Password isn't specified. */

        signUp.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response, json);

        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("password");
        TestCase.assertEquals("required", error.get("error"));
        TestCase.assertNull(error.get("value"));
    }

    @Test
    public void testDoPostAlreadyExists() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getMockedRequest();
        when(request.getParameter("email")).thenReturn(user.getEmail());
        when(request.getParameter("name")).thenReturn(user.getName());
        when(request.getParameter("password")).thenReturn("topsecret");

        signUp.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_BAD_REQUEST, response, json);

        JSONObject body = (JSONObject) json.get("body");
        JSONObject error = (JSONObject) body.get("name");
        TestCase.assertEquals("already_exists", error.get("error"));
        TestCase.assertEquals(user.getName(), error.get("value"));
    }

    @Test
    public void testDoPostForbidden() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user);

        signUp.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_FORBIDDEN, response, json);

        JSONObject body = (JSONObject) json.get("body");
        TestCase.assertNotNull(body);
    }

    @Test
    public void testDoPostOK() throws Exception {
        SignUpServlet signUp = new SignUpServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = mock(UserProfile.class);
        final String EMAIL = "tom@mail.com";
        final String NAME = "Thomas";
        when(user.getID()).thenReturn(99901);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getName()).thenReturn(NAME);
        HttpServletRequest request = this.getMockedRequest();
        when(request.getParameter("email")).thenReturn(EMAIL);
        when(request.getParameter("name")).thenReturn(NAME);
        when(request.getParameter("password")).thenReturn("topsecret");

        signUp.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_OK, response, json);

        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, body);
    }
}
