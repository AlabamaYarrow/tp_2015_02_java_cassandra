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

public class SignupServlet extends HttpServlet {
    static final String TEMPLATE = "signup.ftl";

    AccountService accountService;

    public SignupServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null != user) {
            pageVariables.put("user", user);
        }

        response.getWriter().println(PageGenerator.getPage(SignupServlet.TEMPLATE, pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null != user) {
            pageVariables.put("user", user);
        } else {
            boolean isValid = true;

            String login = request.getParameter("login");
            pageVariables.put("login", login);
            if (login.isEmpty()) {
                pageVariables.put("login_error", "Login is strictly required field.");
                isValid = false;
            }

            String email = request.getParameter("email");
            pageVariables.put("email", email);
            if (email.isEmpty()) {
                pageVariables.put("email_error", "Email is required field.");
                isValid = false;
            }

            String password = request.getParameter("password");
            pageVariables.put("password", password);
            if (password.isEmpty()) {
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
                UserProfile newUser = new UserProfile(login, password, email);
                if (accountService.addUser(login, newUser)) {
                    pageVariables.put("new_user", newUser);
                } else {
                    pageVariables.put("signup_error", "User with name " + login + " already exists.");
                }
            }
        }

        response.getWriter().println(PageGenerator.getPage(SignupServlet.TEMPLATE, pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
