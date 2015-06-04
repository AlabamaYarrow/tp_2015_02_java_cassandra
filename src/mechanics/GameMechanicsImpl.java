package mechanics;

import base.GameMechanics;
import base.Team;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import frontend.GameWebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resources.GameResource;

import java.util.*;

public class GameMechanicsImpl implements GameMechanics {
    private static final Logger LOGGER = LogManager.getLogger(GameMechanicsImpl.class);
    Map<GameWebSocket, Team> webSocketsToTeams = new HashMap<>();
    Deque<PlayersTeam> teams = new LinkedList<>();
    ViewersTeam viewersTeam;
    GameResource resource;

    public GameMechanicsImpl(@NotNull GameResource resource) {
        this.resource = resource;
        this.viewersTeam = new ViewersTeam();
    }

    @Override
    public void onEvent(Event event) {
        String type = event.getType();
        if ("connected".equals(type)) {
            this.onConnected(event);
        } else if ("closed".equals(type)) {
            this.onClosed(event);
        }
    }

    private void onClosed(Event event) {
        GameWebSocket webSocket = (GameWebSocket) event.getTarget();
        Team team = this.webSocketsToTeams.get(webSocket);
        if (team instanceof ViewersTeam) {
            ((ViewersTeam) team).remove(webSocket);
            this.webSocketsToTeams.remove(webSocket);
        } else { // if team is PlayersTeam
            PlayersTeam incompleteTeam = (PlayersTeam) team;
            this.teams.remove(incompleteTeam);
            List<GameWebSocket> users = incompleteTeam.getUsersCopy();
            users.remove(webSocket);
            this.webSocketsToTeams.remove(webSocket);
            incompleteTeam.flush(this.getTeamToViewAt());
            List<GameWebSocket> viewers = this.viewersTeam.getUsersCopy();
            if (viewers.size() == 0) {
                users.forEach((viewer) -> {
                    this.viewersTeam.add(viewer);
                    this.webSocketsToTeams.put(viewer, this.viewersTeam);
                });
            } else {
                GameWebSocket player = viewers.get(0);
                this.viewersTeam.remove(player);
                users.add(player);
                PlayersTeam playersTeam = new PlayersTeam(users, this.resource.getWord());
                this.teams.add(playersTeam);
                users.forEach((p) -> this.webSocketsToTeams.put(p, playersTeam));
            }
        }
    }

    private void onConnected(Event event) {
        GameWebSocket webSocket = (GameWebSocket) event.getTarget();
        List<GameWebSocket> users = this.viewersTeam.getUsersCopy();

        if (users.size() + 1 >= 2) {
            this.viewersTeam.flush(this.getTeamToViewAt());
            users.add(webSocket);
            PlayersTeam team = new PlayersTeam(users, this.resource.getWord());

            for (GameWebSocket player : users) {
                this.webSocketsToTeams.put(player, team);
            }
            this.teams.add(team);
        } else {
            this.viewersTeam.add(webSocket);
            this.webSocketsToTeams.put(webSocket, this.viewersTeam);
        }
    }

    @Nullable
    private PlayersTeam getTeamToViewAt() {
        return this.teams.peekFirst();
    }
}
