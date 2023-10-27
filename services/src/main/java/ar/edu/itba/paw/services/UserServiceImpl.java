package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.ResetPasswordToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.TokenService;
import ar.edu.itba.paw.servicesInterface.UserImageService;
import ar.edu.itba.paw.servicesInterface.UserService;
import ar.edu.itba.persistenceInterface.CommentsDao;
import ar.edu.itba.persistenceInterface.LikesDao;
import ar.edu.itba.persistenceInterface.ResetPasswordTokenDao;
import ar.edu.itba.persistenceInterface.UserDao;
import ar.edu.itba.persistenceInterface.UserImageDao;
import ar.edu.itba.persistenceInterface.UserVerificationTokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    // we are using commentsDao and likesDao to avoid circular dependency problems (because they both autowire userService)
    private final CommentsDao commentsDao;
    private final LikesDao likesDao;
    private final PasswordEncoder passwordEncoder;
    private final MailService ms;
    private final ResetPasswordTokenDao resetPasswordTokenDao;
    private final UserImageService userImageService;



    private final UserVerificationTokenDao userVerificationTokenDao;

    private final TokenService tokenService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(final UserDao userDao, final PasswordEncoder passwordEncoder,
                           final MailService ms,
                           final UserImageService userImageService,
                           final ResetPasswordTokenDao resetPasswordTokenDao,
                           final UserVerificationTokenDao userVerificationTokenDao,
                           final TokenService tokenService,
                           final LikesDao likesDao,
                           final CommentsDao commentsDao) {
        this.userDao = userDao;
        this.likesDao = likesDao;
        this.commentsDao = commentsDao;
        this.passwordEncoder = passwordEncoder;
        this.ms = ms;
        this.userImageService = userImageService;
        this.resetPasswordTokenDao = resetPasswordTokenDao;
        this.userVerificationTokenDao = userVerificationTokenDao;
        this.tokenService = tokenService;
    }

    @Transactional
    @Override
    public Optional<User> createUser(String email, String password, String name, String lastname) {
        Optional<User> user = userDao.create(email, passwordEncoder.encode(password), name, lastname, false, false);
//        user.ifPresent(user -> ms.sendRegisterEmail(user.getEmail(), user.getName()));
        if (user.isPresent()) {
            final UserVerificationToken token = tokenService.generateUserVerificationToken(user.get().getId());
            ms.sendVerificationToken(user.get().getEmail(), token.getToken(), user.get().getName())
                    .thenAccept(mailStatus -> {
                        if (!mailStatus.isSend())
                            LOGGER.error("Error sending token mail to user with id {} for verification,reason\n{}",user.get().getId(),mailStatus.getError());
                    });
        }
        return user;
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByRecipeId(long recipeId) {
        return userDao.findByRecipeId(recipeId);
    }

    @Transactional
    @Override
    public int updatePasswordById(long userId, String password) {
        return userDao.updatePasswordById(userId, passwordEncoder.encode(password));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(final String email) {
        return userDao.findByEmail(email);
    }

    @Transactional
    @Override
    public void updateProfile(long userId, String name, String lastName, byte[] userPhoto, boolean deleteProfilePhoto){
        if(userPhoto.length != 0){
            userImageService.uploadUserPhoto(userId, userPhoto);
        }
        if(deleteProfilePhoto){
            userImageService.deleteUserPhoto(userId);
        }
        userDao.updateNameById(userId, name, lastName);
    }

    @Transactional(readOnly = true)
    @Override
    public long getLikes(long id) {
        return likesDao.getLikes(id);
    }

    @Transactional(readOnly = true)
    @Override
    public long getCountComments(long id) {
        return commentsDao.getCountComments(id);
    }

    @Transactional
    @Override
    public Optional<User> resetPassword(String email, String token, String newPassword) {

        final Optional<ResetPasswordToken> resetPasswordTokenOptional = resetPasswordTokenDao.getToken(token);

        if(!resetPasswordTokenOptional.isPresent()){
            LOGGER.error("Could not get resetPasswordTokenOptional for email {} with token {}",email,token);
            return Optional.empty();
        }
        final ResetPasswordToken resetPasswordToken = resetPasswordTokenOptional.get();
        if (!resetPasswordToken.isValid()) {
            return Optional.empty();
        }

        return userDao.updatePasswordByEmail(email, passwordEncoder.encode(newPassword));
    }

    @Transactional
    @Override
    public void generateResetPasswordToken(User user) {
        resetPasswordTokenDao.removeTokenByUserID(user.getId());
        final ResetPasswordToken resetPasswordToken = tokenService.generateResetPasswordToken(user.getId());
        ms.sendResetPassword(user.getEmail(), resetPasswordToken.getToken());
    }

    @Transactional
    @Override
    public boolean validateToken(String token) {
        final Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenDao.getToken(token);

        return resetPasswordToken.isPresent() && resetPasswordToken.get().isValid();
    }

    @Transactional
    @Override
    public void deleteGeneratedToken(String token) {
        resetPasswordTokenDao.removeToken(token);
    }

    @Transactional
    @Override
    public Optional<User> verifyAccount(String token) {
        final Optional<UserVerificationToken> userVerificationToken = userVerificationTokenDao.getToken(token);

        if (!userVerificationToken.isPresent()) {
            return Optional.empty();
        }

        Optional<User> user = userDao.findById(userVerificationToken.get().getUserId());
        if (user.isPresent()) {
            return userDao.verifyUser(user.get().getId());
        }

        return Optional.empty();
    }

    @Transactional
    @Override
    public void resendVerificationToken(String email) {
        Optional<User> user = findByEmail(email);
        if (user.isPresent()) {
            final UserVerificationToken token = tokenService.generateUserVerificationToken(user.get().getId());
            ms.sendVerificationToken(user.get().getEmail(), token.getToken(), user.get().getName());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void sendSuccessfulPasswordChange(String email) {
        Optional<User> user = findByEmail(email);
        user.ifPresent(value -> ms.sendSuccessfulPasswordChange(value.getEmail(), value.getName()).thenAccept(mailStatus -> {
            if (!mailStatus.isSend())
                LOGGER.error("could not send successful change mail to user with id {}.reason\n{}", value.getId(), mailStatus.getError());
        }));
    }


    @Scheduled(cron = "0 0 */5 * * *")//every 5 hours
    @Transactional
    public void deleteNonVerifiedUsers(){
        long usersDeleted = userDao.deleteNonVerifiedUsers();
        LOGGER.info("Scheduled task deleted {} users that were not verified",usersDeleted);
    }
}
