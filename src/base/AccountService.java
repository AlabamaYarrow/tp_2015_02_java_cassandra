package base;

import com.sun.istack.internal.NotNull;
import main.NoUserException;
import main.SignInException;
import main.UserProfile;

public interface AccountService {

    @NotNull
    UserProfile getUser(String sid) throws NoUserException;

    @NotNull
    UserProfile signIn(String sid, String name, String password) throws SignInException;

    long getUsersCount();

    long getOnlineCount();

    void signOut(String sid) throws NoUserException;

    boolean addUser(@NotNull UserProfile newUser);

    @NotNull
    UserProfile getUserByName(String name) throws NoUserException;
}
