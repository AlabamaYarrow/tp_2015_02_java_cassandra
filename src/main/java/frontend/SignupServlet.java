package frontend;

import main.AccountService;
import main.UserProfile;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.chibrikov on 13.09.2014.
 */
public class SignupServlet extends HttpServlet {
    static final String TEMPLATE = "signup.ftl";

    AccountService accountService;

    public SignupServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println(PageGenerator.getPage(SignupServlet.TEMPLATE));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        boolean isValid = true;

        String login = request.getParameter("login");
        pageVariables.put("login", login);
        if (0 == login.length()) {
            pageVariables.put("login_error", "Login is strictly required field.");
            isValid = false;
        }

        String email = request.getParameter("email");
        pageVariables.put("email", email);
        if (0 == email.length()) {
            pageVariables.put("email_error", "Email is required field.");
            isValid = false;
        }

        String password = request.getParameter("password");
        pageVariables.put("password", password);
        if (0 == password.length()) {
            pageVariables.put("password_error", "Password is required field.");
            isValid = false;
        }

        String passwordConfirmation = request.getParameter("password_confirmation");
        pageVariables.put("password_confirmation", passwordConfirmation);
        if (!password.equals(passwordConfirmation)) {
            pageVariables.put("password_confirmation_error", "Passwords don't match.");
            isValid = false;
        }

        if (isValid) {
            UserProfile user = new UserProfile(login, password, "");
            if (accountService.addUser(login, user)) {
                pageVariables.put("user", user);
            } else {
                pageVariables.put("signup_error", "User with name " + login + " already exists.");
            }
        }

        response.getWriter().println(PageGenerator.getPage(SignupServlet.TEMPLATE, pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
