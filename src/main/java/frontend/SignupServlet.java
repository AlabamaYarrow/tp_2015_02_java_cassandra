package frontend;

import main.AccountService;
import main.UserProfile;
import org.json.simple.JSONObject;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupServlet extends HttpServlet {

    protected final AccountService accountService;

    public SignupServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null != user) {
            json.put("status", HttpServletResponse.SC_FORBIDDEN);
            json.put("message", "You're already authorized.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            boolean isValid = true;

            String login = request.getParameter("name");
            if (null == login || login.isEmpty()) {
                pageVariables.put("login_error", "Login is strictly required field.");
                isValid = false;
            } else {
                pageVariables.put("name", login);
            }

            String email = request.getParameter("email");
            if (null == email || email.isEmpty()) {
                pageVariables.put("email_error", "Email is required field.");
                isValid = false;
            } else {
                pageVariables.put("email", email);
            }

            String password = request.getParameter("password");
            if (null == password || password.isEmpty()) {
                pageVariables.put("password_error", "Password is required field.");
                isValid = false;
            } else {
                pageVariables.put("password", password);
            }

            String passwordConfirmation = request.getParameter("password_confirmation");
            if (null == passwordConfirmation) {
                pageVariables.put("password_confirmation_error", "Confirm password.");
                isValid = false;
            } else {
                if (null == password || !password.equals(passwordConfirmation)) {
                    pageVariables.put("password_confirmation_error", "Passwords don't match.");
                    isValid = false;
                }
                pageVariables.put("password_confirmation", passwordConfirmation);
            }

            if (isValid) {
                UserProfile newUser = new UserProfile(login, password, email);
                if (accountService.addUser(login, newUser)) {
                    pageVariables.put("new_user", newUser);
                } else {
                    pageVariables.put("signup_error", "User with name " + login + " already exists.");
                }
            }
        }

        response.getWriter().println(json.toJSONString());
    }

}
