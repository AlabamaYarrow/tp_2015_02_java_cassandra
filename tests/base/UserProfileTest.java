package base;

import base.dataSets.UserDataSet;
import main.AccountServiceImpl;
import main.AuthException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class UserProfileTest extends ServletTest {

    private static final Logger LOGGER = LogManager.getLogger(UserProfileTest.class);
    private static int counter = 0;
    protected AccountService accountService = new AccountServiceImpl();

    private static int getUniqueCounter() {
        return ++UserProfileTest.counter;
    }

    protected HttpServletRequest getSignedInRequest(UserDataSet user, String json) throws Exception {
        HttpServletRequest request = this.getMockedRequest(json);
        this.accountService.signIn(request.getSession().getId(), user.getName(), "topsecret");
        return request;
    }

    protected UserDataSet createUserProfile() {
        UserDataSet user = new UserDataSet("john" + UserProfileTest.getUniqueCounter() + "@mail.com", "john" + UserProfileTest.getUniqueCounter(), "topsecret");
        try {
            this.accountService.addUser(user);
        } catch (AuthException e) {
            LOGGER.error(e);
        }
        return user;
    }

    protected void checkUserProfileHydrated(UserDataSet user, Map<Object, Object> hydrated) {
        assertNotNull(hydrated);
        assertNotNull(hydrated.get("id"));
        assertEquals(user.getEmail(), hydrated.get("email"));
        assertEquals(user.getName(), hydrated.get("name"));
        assertNull(hydrated.get("password"));
        assertEquals((long) user.getScoreTotal(), hydrated.get("scoreTotal"));
    }

}
