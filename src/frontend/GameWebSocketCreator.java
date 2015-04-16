package frontend;

import base.AccountService;
import base.GameMechanics;
import com.sun.istack.internal.Nullable;
import main.NoUserException;
import main.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class GameWebSocketCreator implements WebSocketCreator {
    //protected WebSocketService webSocketService;
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);
    protected AccountService accountService;
    protected GameMechanics gameMechanics;

    public GameWebSocketCreator(AccountService accountService, GameMechanics gameMechanics/*, WebSocketService webSocketService*/) {
        this.accountService = accountService;
        this.gameMechanics = gameMechanics;
        //this.webSocketService = webSocketService;
    }

    @Override
    @Nullable
    public Object createWebSocket(ServletUpgradeRequest request, ServletUpgradeResponse response) {
        String sid = request.getHttpServletRequest().getSession().getId();
        try {
            UserProfile user = this.accountService.getUser(sid);
            GameWebSocket webSocket = new GameWebSocket(user, this.gameMechanics/*, this.webSocketService*/);
            webSocket.addListener(this.gameMechanics);
            return webSocket;
        } catch (NoUserException e) {
            LOGGER.error("GameWebSocketCreator accepts only authorized users.", e);
            return null;
        }
    }
}
