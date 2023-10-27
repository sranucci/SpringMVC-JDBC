package ar.edu.itba.paw.servicesInterface;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> createUser(String email, String password, String name, String lastname);
    Optional<User> findById(long id);
    Optional<User> findByRecipeId(long recipeId);
    int updatePasswordById(long userId, String password);
    Optional<User> findByEmail(final String email);

    void updateProfile(long userId, String name, String lastName, byte[] userPhoto, boolean deleteProfilePhoto);
    long getLikes(long id);
    long getCountComments(long id);
    Optional<User> resetPassword(String email, String token, String newPassword);
    void generateResetPasswordToken(User user);
    boolean validateToken(String token);
    void deleteGeneratedToken(String token);

    Optional<User> verifyAccount(String token);

    void resendVerificationToken(String email);

    void sendSuccessfulPasswordChange(String email);

    //NO ELIMINAR PORQUE NO BUILDEA, ES METODO SCHEDULED
    void deleteNonVerifiedUsers();

}

