package frontend;

import base.AccountService;
import base.ValidatedServlet;
import base.dataSets.UserDataSet;
import main.AuthException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignInServlet extends ValidatedServlet {

    private static final String[] LOGIN_REQUIRED_FIELDS = {"name", "password",};
    private final AccountService accountService;

    public SignInServlet(AccountService accountService) {
        super(SignInServlet.LOGIN_REQUIRED_FIELDS);
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        if (this.accountService.isAuthorized(request.getSession().getId())) {
            status = HttpServletResponse.SC_FORBIDDEN;
            jsonBody.put("message", "You're already authorized.");
        } else {
            Map<Object, Object> requestJson = (Map<Object, Object>) JSONValue.parse(request.getReader());
            if (requestJson != null && this.areRequiredFieldsValid(requestJson, jsonBody)) {
                String name = (String) requestJson.get("name");
                String sid = request.getSession().getId();
                try {
                    UserDataSet user = this.accountService.signIn(sid, name, (String) requestJson.get("password"));
                    user.hydrate(jsonBody);
                } catch (AuthException e) {
                    status = HttpServletResponse.SC_UNAUTHORIZED;
                    jsonBody.put("message", "Incorrect username or password.");
                }
            } else {
                status = HttpServletResponse.SC_BAD_REQUEST;
            }
        }

        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
