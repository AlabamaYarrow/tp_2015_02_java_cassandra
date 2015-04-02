package frontend;

import base.UserProfileTest;
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

        request.getSession().getId();
        authCheck.doGet(request, response);
        JSONObject json = (JSONObject)JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_OK, response, json);

        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, body);
    }
}