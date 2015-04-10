package frontend;

import base.AccountService;
import base.ValidatedServlet;
import main.NoUserException;
import main.UserProfile;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpServlet extends ValidatedServlet {

    private static final String[] SIGNUP_REQUIRED_FIELDS = {"email", "name", "password",};
    protected final AccountService accountService;

    public SignUpServlet(AccountService accountService) {
        super(SignUpServlet.SIGNUP_REQUIRED_FIELDS);
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        UserProfile user;
        try {
            user = this.accountService.getUser(request.getSession().getId());
            status = HttpServletResponse.SC_FORBIDDEN;
            jsonBody.put("message", "You're already authorized.");
        } catch (NoUserException e) {
            if (this.areRequiredFieldsValid(request, jsonBody)) {
                String name = request.getParameter("name");
                user = new UserProfile(request.getParameter("email"), name, request.getParameter("password"));
                if (accountService.addUser(user)) {
                    user.hydrate(jsonBody);
                } else {
                    status = HttpServletResponse.SC_BAD_REQUEST;
                    Map<Object, Object> nameError = new HashMap<>();
                    jsonBody.put("name", nameError);
                    nameError.put("error", "already_exists");
                    nameError.put("value", name);
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
