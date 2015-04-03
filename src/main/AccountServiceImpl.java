package main;

import base.AccountService;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
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

    public boolean signIn(String sessionId, String name, String password) {
        UserProfile user = this.getUserByName(name);
        if (null == user || !user.checkPassword(password)) {
            return false;
        }
        sessions.put(sessionId, user);
        return true;
    }

    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    public UserProfile getUserByName(String name) {
        return users.get(name);
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
