package main;

import base.AccountService;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest extends TestCase {

    private static final String SID = "foobar";

    @Mock
    private UserProfile user;

    public void testIsAuthorized() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        assertFalse(accountService.isAuthorized(SID));
        accountService.signIn(SID, this.user.getName(), "topsecret");
        assertTrue(accountService.isAuthorized(SID));
    }

    @Before
    public void setUp() {
        when(user.checkPassword("topsecret")).thenReturn(true);
        when(user.getEmail()).thenReturn("tom@mail.com");
        when(user.getID()).thenReturn(31416);
        when(user.getName()).thenReturn("Thomas");
        when(user.getScoreTotal()).thenReturn(314159);
    }

    @Test(expected = NoUserException.class)
    public void testSignOutOK() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        accountService.signIn(SID, this.user.getName(), "topsecret");
        accountService.signOut(SID);
        accountService.getUser(SID);
    }

    @Test(expected = NoUserException.class)
    public void testSignOutClearSession() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.signOut(SID);
    }

    @Test
    public void testGetUsersCount() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        assertEquals(0, accountService.getUsersCount());
        accountService.addUser(this.user);
        assertEquals(1, accountService.getUsersCount());
    }

    @Test
    public void testGetOnlineCount() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        assertEquals(0, accountService.getOnlineCount());
        accountService.signIn(SID, this.user.getName(), "topsecret");
        assertEquals(1, accountService.getUsersCount());
    }

    @Test(expected = AuthException.class)
    public void testAddUserAlreadyExists() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        accountService.addUser(this.user);
    }

    @Test(expected = NullPointerException.class)
    public void testAddUserNull() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(null);
    }

    @Test
    public void testAddUserOK() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        assertEquals(this.user, accountService.getUserByName(this.user.getName()));
    }

    @Test(expected = NoUserException.class)
    public void testGetUserClearSession() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.getUser(SID);
    }

    @Test(expected = AuthException.class)
    public void testSignInBadName() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        accountService.signIn(SID, "some name", "topsecret");
    }

    @Test(expected = AuthException.class)
    public void testSignInBadPassword() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        accountService.signIn(SID, user.getName(), "not_a_secret");
    }

    @Test
    public void testSignInOK() throws Exception {
        AccountService accountService = new AccountServiceImpl();
        accountService.addUser(this.user);
        assertEquals(this.user, accountService.signIn(SID, user.getName(), "topsecret"));
        assertEquals(this.user, accountService.getUser(SID));
    }
}