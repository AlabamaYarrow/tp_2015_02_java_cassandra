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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class AdminServlet extends HttpServlet {
    protected static final String TEMPLATE = "admin.ftl";

    protected final AccountService accountService;

    public AdminServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("registered_count", this.accountService.getUsersCount());
        pageVariables.put("online_count", this.accountService.getOnlineCount());

        response.getWriter().println(PageGenerator.getPage(AdminServlet.TEMPLATE, pageVariables));
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        int delay;
        try {
            delay = Integer.parseInt(request.getParameter("delay"));
        } catch (NullPointerException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        };
        timer.schedule(task, delay);

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("stopping", true);
        response.getWriter().println(PageGenerator.getPage(AdminServlet.TEMPLATE, pageVariables));
    }
}
