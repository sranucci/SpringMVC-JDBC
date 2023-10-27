package ar.edu.itba.paw.models;

public class UserVerificationToken {

    private String token;
    private long id;
    private long userId;

    public UserVerificationToken(String token, long id, long userId) {
        this.token = token;
        this.id = id;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
