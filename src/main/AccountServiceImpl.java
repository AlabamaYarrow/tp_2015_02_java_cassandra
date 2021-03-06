package main;

import base.AccountService;
import base.dataSets.UserDataSet;
import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    private Map<Long, UserDataSet> usersById = new HashMap<>();
    private Map<String, UserDataSet> usersByName = new HashMap<>();
    private Map<String, UserDataSet> sessions = new HashMap<>();

    @Override
    public void addUser(UserDataSet userProfile) throws AuthException {
        String userName = userProfile.getName();
        if (this.usersByName.containsKey(userName)) {
            throw new AuthException();
        }
        this.usersByName.put(userName, userProfile);
        this.usersById.put(userProfile.getID(), userProfile);
    }

    @Override
    public boolean isAuthorized(String sid) {
        return this.sessions.containsKey(sid);
    }

    @Override
    public UserDataSet signIn(String sessionId, String name, String password) throws AuthException {
        UserDataSet user;
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
    public UserDataSet getUserByName(String name) throws NoUserException {
        UserDataSet user = this.usersByName.get(name);
        if (null == user) {
            throw new NoUserException();
        }
        return user;
    }

    @Override
    public long getUsersCount() {
        return this.usersByName.size();
    }

    @Override
    public long getOnlineCount() {
        return this.sessions.size();
    }

    @Override
    @NotNull
    public UserDataSet getUserById(int userId) throws NoUserException {
        UserDataSet user = this.usersById.get(userId);
        if (user == null) {
            throw new NoUserException();
        }
        return user;
    }

    @Override
    public void shutdown() {

    }

    @Override
    @NotNull
    public UserDataSet getUser(String sid) throws NoUserException {
        UserDataSet user = this.sessions.get(sid);
        if (null == user) {
            throw new NoUserException();
        }
        return user;
    }
}
