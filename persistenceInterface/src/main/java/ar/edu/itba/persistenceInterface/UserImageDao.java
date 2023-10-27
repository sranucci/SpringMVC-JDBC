package ar.edu.itba.persistenceInterface;

import java.util.Optional;

public interface UserImageDao {
    long uploadUserPhoto(long userId, byte[] photoData);

    Optional<byte[]> findUserPhotoByUserId(long userId);

    int deleteUserPhoto(long userId);
}
