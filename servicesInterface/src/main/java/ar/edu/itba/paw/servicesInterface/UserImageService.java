package ar.edu.itba.paw.servicesInterface;

import java.util.Optional;

public interface UserImageService {

    int deleteUserPhoto(long userId);

    long uploadUserPhoto(long userId, byte[] photoData);

    Optional<byte[]> findUserPhotoByUserId(long id);


}
