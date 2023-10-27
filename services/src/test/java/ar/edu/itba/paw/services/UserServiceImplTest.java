
package ar.edu.itba.paw.services;

import ar.edu.itba.paw.mailstatus.MailStatus;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.TokenService;
import ar.edu.itba.persistenceInterface.UserDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final boolean IS_ADMIN = false;
    private static final long ID = 1;

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private  MailService ms;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserServiceImpl us;

    @Test
    public void testCreateUser(){

        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);

        when(userDao.create(eq(EMAIL), eq(PASSWORD), eq(FIRST_NAME), eq(LAST_NAME), eq(IS_ADMIN), eq(false)))
                .thenReturn(Optional.of(new User(ID, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, IS_ADMIN, true)));

        when(tokenService.generateUserVerificationToken(ID)).thenReturn(new UserVerificationToken("token",ID,ID));
        when(ms.sendVerificationToken(EMAIL, "token", FIRST_NAME)).thenReturn(CompletableFuture.completedFuture(new MailStatus(true)));

        Optional<User> newUser = us.createUser(EMAIL, PASSWORD, FIRST_NAME,LAST_NAME);

        Assert.assertNotNull(newUser);
        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(ID, newUser.get().getId());
        Assert.assertEquals(EMAIL, newUser.get().getEmail());
        Assert.assertEquals(PASSWORD, newUser.get().getPassword());
        Assert.assertEquals(FIRST_NAME, newUser.get().getName());
        Assert.assertEquals(LAST_NAME, newUser.get().getLastname());
    }

    @Test
    public void testCreateUserAlreadyExists(){

        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);

        when(userDao.create(eq(EMAIL), eq(PASSWORD), eq(FIRST_NAME), eq(LAST_NAME), eq(IS_ADMIN), eq(false)))
                .thenReturn(Optional.empty());

        Optional<User> newUser = us.createUser(EMAIL, PASSWORD, FIRST_NAME,LAST_NAME);

        Assert.assertNotNull(newUser);
        Assert.assertFalse(newUser.isPresent());
    }

    @Test
    public void testFindById(){

        when(userDao.findById(eq(ID)))
                .thenReturn(Optional.of(new User(ID, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, IS_ADMIN, false)));

        Optional<User> newUser = us.findById(ID);

        Assert.assertNotNull(newUser);
        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(ID, newUser.get().getId());
        Assert.assertEquals(EMAIL, newUser.get().getEmail());
        Assert.assertEquals(PASSWORD, newUser.get().getPassword());
        Assert.assertEquals(FIRST_NAME, newUser.get().getName());
        Assert.assertEquals(LAST_NAME, newUser.get().getLastname());
    }
    @Test
    public void testFindByRecipeId(){

        when(userDao.findByRecipeId(eq(ID)))
                .thenReturn(Optional.of(new User(ID, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, IS_ADMIN, false)));

        Optional<User> newUser = us.findByRecipeId(ID);

        Assert.assertNotNull(newUser);
        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(ID, newUser.get().getId());
        Assert.assertEquals(EMAIL, newUser.get().getEmail());
        Assert.assertEquals(PASSWORD, newUser.get().getPassword());
        Assert.assertEquals(FIRST_NAME, newUser.get().getName());
        Assert.assertEquals(LAST_NAME, newUser.get().getLastname());
    }
}

