package com.mkohan.library.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String username;

    private String password;

    protected AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
