package base;

import com.sun.istack.internal.NotNull;
import frontend.GameWebSocket;
import mechanics.Team;

public interface GameMechanics {

    @NotNull
    Team addToTeam(GameWebSocket webSocket);

    void onWebSocketClosed(GameWebSocket webSocket);
}
