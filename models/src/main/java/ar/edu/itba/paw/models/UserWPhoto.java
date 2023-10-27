package ar.edu.itba.paw.models;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UserWPhoto extends User {
    private final Optional<byte[]> image;

    public UserWPhoto(long id, String email, String password, String name, String lastname, boolean isAdmin, boolean isVerified, Optional<byte[]> image) {
        super(id, email, password, name, lastname, isAdmin, isVerified);
        this.image = image;
    }
}
