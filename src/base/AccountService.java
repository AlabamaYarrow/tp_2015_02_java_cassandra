package base;

import com.sun.istack.internal.NotNull;
import main.AuthException;
import main.NoUserException;
import main.UserProfile;

public interface AccountService {

    @NotNull
    UserProfile getUser(String sid) throws NoUserException;

    boolean isAuthorized(String sid);

    @NotNull
    UserProfile signIn(String sid, String name, String password) throws AuthException;

    long getUsersCount();

    long getOnlineCount();

    void signOut(String sid) throws NoUserException;

    void addUser(@NotNull UserProfile newUser) throws AuthException;

    @NotNull
    UserProfile getUserByName(String name) throws NoUserException;

    UserProfile getUserById(int userId) throws NoUserException;
}
