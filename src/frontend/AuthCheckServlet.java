package frontend;

import main.AccountService;
import main.UserProfile;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthCheckServlet extends HttpServlet {

    protected final AccountService accountService;

    public AuthCheckServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null == user) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        } else {
            user.hydrate(jsonBody);
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().println(json.toJSONString());
    }
}
