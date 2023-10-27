package ar.edu.itba.paw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private final long id;
    private final String email;
    private final String password;
    private final String name;
    private final String lastname;
    private final boolean isAdmin;
    private final boolean isVerified;
}
