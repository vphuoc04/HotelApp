package com.example.backend.modules.users.resources;

public class LoginResource {
    private final String token;
    private final String refreshToken;
    private final UserResource user;

    public LoginResource(
        String token,
        String refreshToken,
        UserResource user
    ){
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserResource getUser() {
        return user;
    }
}
