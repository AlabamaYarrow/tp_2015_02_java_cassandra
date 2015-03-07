package main;

import java.util.HashMap;
import java.util.Map;

public class AccountService {
    private Map<String, UserProfile> users = new HashMap<>();
    private Map<String, UserProfile> sessions = new HashMap<>();

    public boolean addUser(String userName, UserProfile userProfile) {
        if (users.containsKey(userName)) {
            return false;
        }
        users.put(userName, userProfile);
        return true;
    }

    public void login(String sessionId, UserProfile userProfile) {
        sessions.put(sessionId, userProfile);
    }

    public UserProfile getUserByLogin(String login) {
        return users.get(login);
    }

    public UserProfile getUser(String sid) {
        return this.sessions.get(sid);
    }

    public UserProfile getSessions(String sessionId) {
        return sessions.get(sessionId);
    }
}
