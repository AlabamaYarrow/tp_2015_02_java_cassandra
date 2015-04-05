package frontend;

import base.AccountService;
import base.GameMechanics;
import base.WebSocketService;
import main.NoUserException;
import main.UserProfile;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class GameWebSocketCreator implements WebSocketCreator {
    protected AccountService accountService;
    protected GameMechanics gameMechanics;
    protected WebSocketService webSocketService;

    public GameWebSocketCreator(AccountService accountService, GameMechanics gameMechanics, WebSocketService webSocketService) {
        this.accountService = accountService;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest request, ServletUpgradeResponse response) {
        String sid = request.getHttpServletRequest().getSession().getId();
        UserProfile user;
        try {
            user = this.accountService.getUser(sid);
        } catch (NoUserException e) {
            e.printStackTrace();
            return null;
        }
        String name = user.getName();
        return new GameWebSocket(name, this.gameMechanics, this.webSocketService);
    }
}
