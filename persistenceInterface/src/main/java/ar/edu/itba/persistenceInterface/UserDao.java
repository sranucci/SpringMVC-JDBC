package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    int updateNameById(long userId, String name, String lastname);

    int updatePasswordById(long userId, String password);

    int deleteById(long userId);

    Optional<User> create(String email, String password, String name, String lastname, boolean isAdmin, boolean isVerified);

    Optional<User> verifyUser(final long id);

    Optional<User> findById(final long id);

    Optional<User> findByRecipeId(long recipeId);


    Optional<User> findByEmail(final String email);

    Optional<User> updatePasswordByEmail(String email, String password);

    int deleteNonVerifiedUsers();

}
