package main;

import java.util.HashMap;
import java.util.Map;

public class AccountService {
    protected Map<String, UserProfile> users = new HashMap<>();
    protected Map<String, UserProfile> sessions = new HashMap<>();

    public boolean addUser(UserProfile userProfile) {
        String userName = userProfile.getName();
        if (users.containsKey(userName)) {
            return false;
        }
        users.put(userName, userProfile);
        return true;
    }

    public void signIn(String sessionId, UserProfile userProfile) {
        sessions.put(sessionId, userProfile);
    }

    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    public UserProfile getUserByName(String login) {
        return users.get(login);
    }

    public long getUsersCount() {
        return this.users.size();
    }

    public long getOnlineCount() {
        return this.sessions.size();
    }

    public UserProfile getUser(String sid) {
        return this.sessions.get(sid);
    }
}
