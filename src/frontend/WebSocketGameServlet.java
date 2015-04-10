package frontend;

import base.AccountService;
import base.GameMechanics;
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

@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/api/v1/game/"})
public class WebSocketGameServlet extends WebSocketServlet {
    protected final static int IDLE_TIME = 60 * 1000;
    protected AccountService accountService;
    protected GameMechanics gameMechanics;
    //protected WebSocketService webSocketService;

    public WebSocketGameServlet(/*WebSocketService webSocketService, */AccountService accountService, GameMechanics gameMechanics) {
        //this.webSocketService = webSocketService;
        this.accountService = accountService;
        this.gameMechanics = gameMechanics;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(WebSocketGameServlet.IDLE_TIME);
        factory.setCreator(new GameWebSocketCreator(this.accountService, this.gameMechanics/*, this.webSocketService*/));
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getSession().getId();
        try {
            this.accountService.getUser(sid);
            super.service(request, response);
        } catch (NoUserException e) {
            int status = HttpServletResponse.SC_UNAUTHORIZED;
            JSONObject json = new JSONObject();
            Map<Object, Object> jsonBody = new HashMap<>();
            json.put("body", jsonBody);
            json.put("status", status);
            response.setStatus(status);
            response.getWriter().print(json.toJSONString());
        }
    }
}
