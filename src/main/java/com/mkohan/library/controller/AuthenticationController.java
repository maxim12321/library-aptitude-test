package com.mkohan.library.controller;

import com.mkohan.library.dto.AuthenticationRequest;
import com.mkohan.library.service.AuthenticationService;
import com.mkohan.library.util.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public String login(@RequestBody AuthenticationRequest request) {
        final UserDetails userDetails = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return JwtTokenUtils.generateToken(userDetails);
    }
}
