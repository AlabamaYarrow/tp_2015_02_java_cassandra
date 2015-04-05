package main;

import base.AccountService;
import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    protected Map<String, UserProfile> users = new HashMap<>();
    protected Map<String, UserProfile> sessions = new HashMap<>();

    public boolean addUser(@NotNull UserProfile userProfile) {
        String userName = userProfile.getName();
        if (users.containsKey(userName)) {
            return false;
        }
        users.put(userName, userProfile);
        return true;
    }

    public UserProfile signIn(String sessionId, String name, String password) throws SignInException {
        UserProfile user;
        try {
            user = this.getUserByName(name);
        } catch (NoUserException e) {
            throw new SignInException();
        }
        if (!user.checkPassword(password)) {
            throw new SignInException();
        }
        this.sessions.put(sessionId, user);
        return user;
    }

    public void signOut(String sessionId) throws NoUserException {
        if (null == this.sessions.remove(sessionId)) {
            throw new NoUserException("There isn't anybody to logout.");
        }
    }

    public UserProfile getUserByName(String name) throws NoUserException {
        UserProfile user = this.users.get(name);
        if (null == user) {
            throw new NoUserException();
        }
        return user;
    }

    public long getUsersCount() {
        return this.users.size();
    }

    public long getOnlineCount() {
        return this.sessions.size();
    }

    public UserProfile getUser(String sid) throws NoUserException {
        UserProfile user = this.sessions.get(sid);
        if (null == user) {
            throw new NoUserException();
        }
        return user;
    }
}
