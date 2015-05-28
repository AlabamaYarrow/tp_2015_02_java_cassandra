package frontend.console;

import base.AccountService;
import base.GameMechanics;
import base.dataSets.UserDataSet;
import com.sun.istack.internal.Nullable;
import frontend.GameWebSocket;
import main.NoUserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class ConsoleWebSocketCreator implements WebSocketCreator {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleWebSocketCreator.class);
    private AccountService accountService;
    private ConsoleService consoleService;

    public ConsoleWebSocketCreator(AccountService accountService, ConsoleService consoleService) {
        this.accountService = accountService;
        this.consoleService = consoleService;
    }

    @Override
    @Nullable
    public Object createWebSocket(ServletUpgradeRequest request, ServletUpgradeResponse response) {
        String sid = request.getHttpServletRequest().getSession().getId();
        try {
            UserDataSet user = this.accountService.getUser(sid);
            ConsoleWebSocket webSocket = new ConsoleWebSocket(user, this.consoleService);
            return webSocket;
        } catch (NoUserException e) {
            LOGGER.error("ConsoleWebSocketCreator accepts only authorized users.", e);
            return null;
        }
    }
}
