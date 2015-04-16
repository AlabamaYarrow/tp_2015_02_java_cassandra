package main;

import base.AccountService;
import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    protected Map<String, UserProfile> users = new HashMap<>();
    protected Map<String, UserProfile> sessions = new HashMap<>();

    @Override
    public void addUser(UserProfile userProfile) throws AuthException {
        String userName = userProfile.getName();
        if (users.containsKey(userName)) {
            throw new AuthException();
        }
        users.put(userName, userProfile);
    }

    @Override
    public boolean isAuthorized(String sid) {
        return this.sessions.containsKey(sid);
    }

    @Override
    public UserProfile signIn(String sessionId, String name, String password) throws AuthException {
        UserProfile user;
        try {
            user = this.getUserByName(name);
        } catch (NoUserException e) {
            throw new AuthException();
        }
        if (!user.checkPassword(password)) {
            throw new AuthException();
        }
        this.sessions.put(sessionId, user);
        return user;
    }

    @Override
    public void signOut(String sessionId) throws NoUserException {
        if (null == this.sessions.remove(sessionId)) {
            throw new NoUserException("There isn't anybody to logout.");
        }
    }

    @Override
    public UserProfile getUserByName(String name) throws NoUserException {
        UserProfile user = this.users.get(name);
        if (null == user) {
            throw new NoUserException();
        }
        return user;
    }

    @Override
    public long getUsersCount() {
        return this.users.size();
    }

    @Override
    public long getOnlineCount() {
        return this.sessions.size();
    }

    @Override
    @NotNull
    public UserProfile getUser(String sid) throws NoUserException {
        UserProfile user = this.sessions.get(sid);
        if (null == user) {
            throw new NoUserException();
        }
        return user;
    }
}
