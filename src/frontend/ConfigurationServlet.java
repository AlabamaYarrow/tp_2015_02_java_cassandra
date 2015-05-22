package frontend;

import base.AccountService;
import base.dataSets.UserDataSet;
import main.NoUserException;
import org.json.simple.JSONObject;
import resources.ConfigurationResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationServlet extends HttpServlet {

    private final AccountService accountService;
    private final ConfigurationResource RESOURCE;

    public ConfigurationServlet(AccountService accountService, ConfigurationResource resource) {
        this.accountService = accountService;
        this.RESOURCE = resource;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        UserDataSet user;
        try {
            user = this.accountService.getUser(request.getSession().getId());
            jsonBody.put("user", user.getHydrated());
        } catch (NoUserException e) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        }
        jsonBody.put("game_web_socket_url", this.RESOURCE.gameWebSocketUrl);
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
