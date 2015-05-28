package frontend.console;

import base.dataSets.UserDataSet;

import java.util.HashMap;
import java.util.Map;

public class ConsoleService {
    private Map<Long, ConsoleWebSocket> webSocketMap = new HashMap<>();

    public ConsoleWebSocket getWebSocket(UserDataSet user) throws NoConsoleException {
        ConsoleWebSocket webSocket = this.webSocketMap.get(user.getID());
        if (webSocket == null) {
            throw new NoConsoleException();
        }
        return webSocket;
    }

    public boolean contains(UserDataSet user) {
        return this.webSocketMap.containsKey(user.getID());
    }

    public void addWebSocket(UserDataSet user, ConsoleWebSocket webSocket) {
        this.webSocketMap.put(user.getID(), webSocket);
    }

    public void ensureWebSocketClean(UserDataSet user) {
        this.webSocketMap.remove(user.getID());
    }
}
