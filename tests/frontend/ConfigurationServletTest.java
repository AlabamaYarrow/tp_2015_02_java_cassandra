package frontend;

import base.UserProfileTest;
import base.dataSets.UserDataSet;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import resources.ConfigurationResource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServletTest extends UserProfileTest {

    @Mock
    private ConfigurationResource resource;

    @Before
    public void before() {
        this.resource.gameWebSocketUrl = "foo";
    }

    @Test
    public void testDoGetOK() throws Exception {
        ConfigurationServlet authCheck = new ConfigurationServlet(this.accountService, this.resource);

        HttpServletResponse response = this.getMockedResponse();
        UserDataSet user = this.createUserProfile();
        HttpServletRequest request = this.getSignedInRequest(user, "");

        authCheck.doGet(request, response);
        this.checkStatusCode(HttpServletResponse.SC_OK, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        this.checkUserProfileHydrated(user, (Map<Object, Object>) body.get("user"));
        assertEquals("foo", body.get("game_web_socket_url"));
    }

    @Test
    public void testDoGetUnauthorized() throws Exception {
        ConfigurationServlet authCheck = new ConfigurationServlet(this.accountService, this.resource);

        HttpServletResponse response = this.getMockedResponse();
        HttpServletRequest request = this.getMockedRequest("");

        authCheck.doGet(request, response);
        this.checkStatusCode(HttpServletResponse.SC_UNAUTHORIZED, response);

        JSONObject json = (JSONObject) JSONValue.parse(response.toString());
        JSONObject body = (JSONObject) json.get("body");
        assertEquals("foo", body.get("game_web_socket_url"));
    }
}