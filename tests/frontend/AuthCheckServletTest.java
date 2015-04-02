package frontend;

import base.UserProfileTest;
import junit.framework.TestCase;
import main.UserProfile;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthCheckServletTest extends UserProfileTest {

    @Test
    public void testDoGetOK() throws Exception {
        AuthCheckServlet authCheck = new AuthCheckServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user);

        authCheck.doGet(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_OK, response, json);

        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, body);
    }

    @Test
    public void testDoGetUnauthorized() throws Exception {
        AuthCheckServlet authCheck = new AuthCheckServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest();

        authCheck.doGet(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_UNAUTHORIZED, response, json);

        JSONObject body = (JSONObject) json.get("body");
        TestCase.assertNotNull(body);
    }
}