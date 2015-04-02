package frontend;

import base.ValidatedServlet;
import main.AccountService;
import main.UserProfile;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SigninServlet extends ValidatedServlet {

    protected final AccountService accountService;

    private static final String[] LOGIN_REQUIRED_FIELDS = {"name", "password", };

    public SigninServlet(AccountService accountService) {
        super(SigninServlet.LOGIN_REQUIRED_FIELDS);
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null != user) {
            status = HttpServletResponse.SC_FORBIDDEN;
            jsonBody.put("message", "You're already authorized.");
        } else if (!this.areRequiredFieldsValid(request, jsonBody)) {
            status = HttpServletResponse.SC_BAD_REQUEST;
        } else {
            String name = request.getParameter("name");
            UserProfile loggedInUser = this.accountService.getUserByLogin(name);
            if (null == loggedInUser || !loggedInUser.checkPassword(request.getParameter("password"))) {
                status = HttpServletResponse.SC_UNAUTHORIZED;
                jsonBody.put("message", "Incorrect username or password.");
            } else {
                loggedInUser.hydrate(jsonBody);
            }
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().println(json.toJSONString());
    }
}
