package main;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest extends TestCase {

    protected static final String SID = "foobar";

    @Mock
    protected UserProfile user;

    @Before
    public void setUp() {
        when(user.checkPassword("topsecret")).thenReturn(true);
        when(user.getEmail()).thenReturn("tom@mail.com");
        when(user.getID()).thenReturn(31416);
        when(user.getName()).thenReturn("Thomas");
        when(user.getScore()).thenReturn(314159);
    }

    @Test
    public void testAddUserAlreadyExists() throws Exception {
        AccountService accountService = new AccountService();
        assertTrue(accountService.addUser(this.user));
        assertFalse(accountService.addUser(this.user));
    }

    @Test
    public void testAddUserOK() throws Exception {
        AccountService accountService = new AccountService();
        accountService.addUser(this.user);
        assertEquals(this.user, accountService.getUserByName(this.user.getName()));
    }

    @Test
    public void testSignInBadName() throws Exception {
        AccountService accountService = new AccountService();
        accountService.addUser(this.user);
        assertFalse(accountService.signIn(SID, "some name", "topsecret"));
        assertNull(accountService.getUser(SID));
    }

    @Test
    public void testSignInBadPassword() throws Exception {
        AccountService accountService = new AccountService();
        accountService.addUser(this.user);
        assertFalse(accountService.signIn(SID, user.getName(), "not_a_secret"));
        assertNull(accountService.getUser(SID));
    }

    @Test
    public void testSignInOK() throws Exception {
        AccountService accountService = new AccountService();
        accountService.addUser(this.user);
        assertTrue(accountService.signIn(SID, user.getName(), "topsecret"));
        assertEquals(this.user, accountService.getUser(SID));
    }
}