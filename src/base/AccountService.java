package base;

import base.dataSets.UserDataSet;
import com.sun.istack.internal.NotNull;
import main.AuthException;
import main.NoUserException;

public interface AccountService {

    @NotNull
    UserDataSet getUser(String sid) throws NoUserException;

    boolean isAuthorized(String sid);

    @NotNull
    UserDataSet signIn(String sid, String name, String password) throws AuthException;

    long getUsersCount();

    long getOnlineCount();

    void signOut(String sid) throws NoUserException;

    void addUser(@NotNull UserDataSet newUser) throws AuthException;

    @NotNull
    UserDataSet getUserByName(String name) throws NoUserException;

    UserDataSet getUserById(int userId) throws NoUserException;

    void shutdown();
}
