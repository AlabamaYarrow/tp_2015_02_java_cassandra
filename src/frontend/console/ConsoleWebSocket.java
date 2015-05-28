package frontend.console;

import base.dataSets.UserDataSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.Map;

@WebSocket
public class ConsoleWebSocket {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleWebSocket.class);
    private UserDataSet userProfile;
    private Session session;
    private boolean initial;
    private ConsoleService consoleService;

    public ConsoleWebSocket(UserDataSet userProfile, ConsoleService consoleService) {
        this.userProfile = userProfile;
        this.consoleService = consoleService;
        this.initial = !consoleService.contains(userProfile);
    }

    private void notifyClient(String type, Map<Object, Object> body) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("body", body);
        try {
            this.session.getRemote().sendString(json.toJSONString());
        } catch (IOException e) {
            LOGGER.error("Error during notifying client.", e);
        }
    }

    @OnWebSocketConnect
    public void onWebSocketConnect(Session session) {
        this.session = session;
        if (this.initial) {
            this.consoleService.addWebSocket(this.userProfile, this);
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        if (this.initial) {
            this.consoleService.ensureWebSocketClean(this.userProfile);
        }
    }

    @OnWebSocketMessage
    public void onWebSocketMessage(String text) {
        JSONObject json, body;
        String type;
        try {
            json = (JSONObject) JSONValue.parse(text);
            type = (String) json.get("type");
            body = (JSONObject) json.get("body");
        } catch (NullPointerException | ClassCastException e) {
            LOGGER.error("WebSocket message structure is wrong.", e);
            this.session.close();
            return;
        }
        try {
            this.consoleService.getWebSocket(this.userProfile).process(body);
        } catch (NoConsoleException e) {
            LOGGER.error(e);
        }
    }

    public void process(Map<Object, Object> data) {
        this.notifyClient("update", data);
    }
}
