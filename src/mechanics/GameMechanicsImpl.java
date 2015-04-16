package mechanics;

import base.GameMechanics;
import base.Listenable;
import base.Team;
import frontend.GameWebSocket;
import resources.GameResource;

import java.util.*;

public class GameMechanicsImpl implements GameMechanics {
    Map<GameWebSocket, Team> webSocketsToTeams = new HashMap<>();
    Deque<PlayersTeam> teams = new LinkedList<>();
    ViewersTeam viewersTeam;
    GameResource resource;

    public GameMechanicsImpl(GameResource resource) {
        this.resource = resource;
        this.viewersTeam = new ViewersTeam();
    }

    @Override
    public void onEvent(Event event) {
        Listenable target = event.getTarget();
        String type = event.getType();
        if (target instanceof GameWebSocket) {
            GameWebSocket webSocket = (GameWebSocket) target;
            if ("connected".equals(type)) {
                this.onWebSocketConnected(webSocket);
            } else if ("closed".equals(type)) {
                this.onWebSocketClosed(webSocket);
            }
        }
    }

    protected void onWebSocketClosed(GameWebSocket webSocket) {
        Team team = webSocketsToTeams.get(webSocket);
        if (team instanceof ViewersTeam) {
            ((ViewersTeam) team).remove(webSocket);
        } else { // if team is PlayersTeam
            this.teams.remove(team);
            List<GameWebSocket> users = team.getUsers();
            users.remove(webSocket);
            team.flush();
            List<GameWebSocket> viewers = this.viewersTeam.getUsers();
            if (viewers.size() == 0) {
                for (Iterator<GameWebSocket> iterator = users.iterator(); iterator.hasNext(); ) {
                    this.viewersTeam.add(iterator.next());
                }
            } else {
                GameWebSocket player = viewers.get(0);
                this.viewersTeam.remove(player);
                users.add(player);
                PlayersTeam playersTeam = new PlayersTeam(users);
                this.teams.add(playersTeam);
            }
        }
    }

    protected void onWebSocketConnected(GameWebSocket webSocket) {
        List<GameWebSocket> users = this.viewersTeam.getUsers();
        if (users.size() + 1 >= this.resource.judgesCount + 2) {
            this.viewersTeam.flush();
            users.add(webSocket);
            PlayersTeam team = new PlayersTeam(users);
            this.teams.add(team);
        } else {
            this.viewersTeam.add(webSocket);
        }
    }
}
