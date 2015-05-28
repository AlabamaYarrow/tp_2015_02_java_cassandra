package frontend.console;

import base.AccountService;
import base.GameMechanics;
import base.dataSets.UserDataSet;
import frontend.GameWebSocketCreator;
import main.NoUserException;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "WebSocketConsoleServlet")
public class WebSocketConsoleServlet extends WebSocketServlet {
    private final static int IDLE_TIME = 3600 * 1000;
    private AccountService accountService;
    private ConsoleService consoleService;

    public WebSocketConsoleServlet(AccountService accountService, ConsoleService consoleService) {
        this.accountService = accountService;
        this.consoleService = consoleService;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(WebSocketConsoleServlet.IDLE_TIME);
        factory.setCreator(new ConsoleWebSocketCreator(this.accountService, this.consoleService));
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getSession().getId();
        UserDataSet user;
        try {
            user = this.accountService.getUser(sid);
        } catch (NoUserException e) {
            int status = HttpServletResponse.SC_UNAUTHORIZED;
            JSONObject json = new JSONObject();
            Map<Object, Object> jsonBody = new HashMap<>();
            json.put("body", jsonBody);
            json.put("status", status);
            response.setStatus(status);
            response.getWriter().print(json.toJSONString());
            return;
        }
        if (request.getParameter("init") == null) {
            try {
                this.consoleService.getWebSocket(user);
            } catch(NoConsoleException e) {
                int status = HttpServletResponse.SC_EXPECTATION_FAILED;
                JSONObject json = new JSONObject();
                Map<Object, Object> jsonBody = new HashMap<>();
                json.put("body", jsonBody);
                json.put("status", status);
                response.setStatus(status);
                response.getWriter().print(json.toJSONString());
                return;
            }
        } else {
            this.consoleService.ensureWebSocketClean(user);
        }
        super.service(request, response);
    }
}
