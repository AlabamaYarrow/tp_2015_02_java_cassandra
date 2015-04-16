package frontend;

import base.UserProfileTest;
import main.UserProfile;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignOutServletTest extends UserProfileTest {

    @Test
    public void testDoPostOK() throws Exception {
        SignOutServlet signOut = new SignOutServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        UserProfile user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user, "");

        signOut.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_OK, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        assertNotNull(body);
    }

    @Test
    public void testDoPostUnauthorized() throws Exception {
        SignOutServlet signOut = new SignOutServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest("");

        signOut.doPost(request, response);
        this.checkStatusCode(HttpServletResponse.SC_UNAUTHORIZED, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        assertNotNull(body);
    }
}