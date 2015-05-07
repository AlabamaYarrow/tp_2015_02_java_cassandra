package frontend;

import base.AccountService;
import utils.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminServlet extends HttpServlet {
    private static final String TEMPLATE = "admin.ftl";

    private final AccountService accountService;
    private final PageGenerator pageGenerator;
    private final Timer timer;

    public AdminServlet(AccountService accountService, PageGenerator pageGenerator, Timer timer) {
        this.accountService = accountService;
        this.pageGenerator = pageGenerator;
        this.timer = timer;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("registered_count", this.accountService.getUsersCount());
        pageVariables.put("online_count", this.accountService.getOnlineCount());

        response.getWriter().print(this.pageGenerator.getPage(AdminServlet.TEMPLATE, pageVariables));
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

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        };
        this.timer.schedule(task, delay);

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("stopping", true);
        response.getWriter().print(this.pageGenerator.getPage(AdminServlet.TEMPLATE, pageVariables));
    }
}
