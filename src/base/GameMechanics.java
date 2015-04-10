package base;

import frontend.GameWebSocket;

public interface GameMechanics {

    void addToTeam(GameWebSocket webSocket);

    void onWebSocketClosed(GameWebSocket webSocket);
}
