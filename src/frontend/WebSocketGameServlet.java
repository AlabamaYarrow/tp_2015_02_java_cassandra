package frontend;

import base.AccountService;
import base.GameMechanics;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

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
}
