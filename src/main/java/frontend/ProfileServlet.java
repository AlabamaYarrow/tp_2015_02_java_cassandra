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

public class ProfileServlet extends HttpServlet {
    protected static final String TEMPLATE = "profile.ftl";

    protected AccountService accountService;

    public ProfileServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        UserProfile user = this.accountService.getUser(request.getSession().getId());
        if (null == user) {
            response.setHeader("Location", "/login/");
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        } else {
            pageVariables.put("user", user);
            response.getWriter().println(PageGenerator.getPage(ProfileServlet.TEMPLATE, pageVariables));
        }
    }
}
