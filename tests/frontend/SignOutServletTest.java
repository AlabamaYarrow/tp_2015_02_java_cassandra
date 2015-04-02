package frontend;

import base.UserProfileTest;
import junit.framework.TestCase;
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
        HttpServletRequest request = this.getSignedInRequest(user);

        signOut.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_OK, response, json);

        JSONObject body = (JSONObject) json.get("body");
        TestCase.assertNotNull(body);
    }

    @Test
    public void testDoPostUnauthorized() throws Exception {
        SignOutServlet signOut = new SignOutServlet(this.accountService);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest();

        signOut.doPost(request, response);
        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        this.checkStatusCode(HttpServletResponse.SC_UNAUTHORIZED, response, json);

        JSONObject body = (JSONObject) json.get("body");
        TestCase.assertNotNull(body);
    }
}