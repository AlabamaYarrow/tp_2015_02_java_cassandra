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

public class LoginServlet extends HttpServlet {
    static final String TEMPLATE = "login.ftl";
    static final String ERROR_MESSAGE = "Incorrect username or password.";

    AccountService accountService;

    public LoginServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null != user) {
            pageVariables.put("user", user);
        }

        response.getWriter().println(PageGenerator.getPage(LoginServlet.TEMPLATE, pageVariables));
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        String sid = request.getSession().getId();
        UserProfile user = this.accountService.getUser(sid);
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

            String password = request.getParameter("password");
            pageVariables.put("password", password);
            if (password.isEmpty()) {
                pageVariables.put("password_error", "Password is required field.");
                isValid = false;
            }

            if (isValid) {
                UserProfile loggedInUser = this.accountService.getUserByLogin(login);
                if (null == loggedInUser || !loggedInUser.checkPassword(password)) {
                    pageVariables.put("login_procedure_error", LoginServlet.ERROR_MESSAGE);
                } else {
                    accountService.login(sid, loggedInUser);
                    pageVariables.put("logged_in_user", loggedInUser);
                }
            }
        }

        response.getWriter().println(PageGenerator.getPage(LoginServlet.TEMPLATE, pageVariables));
    }
}
