package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class ResetPasswordToken {
    private static final Integer TOKEN_DURATION_DAYS = 1;
    private String token;
    private long id;
    private long userId;
    private LocalDateTime expirationDate;

    public ResetPasswordToken(String token, long id, long userId, LocalDateTime expirationDate) {
        this.token = token;
        this.id = id;
        this.userId = userId;
        this.expirationDate = expirationDate;
    }

    public static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    public boolean isValid() {
        return expirationDate.compareTo(LocalDateTime.now()) > 0;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
