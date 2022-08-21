package com.mkohan.library.service.impl;

import com.mkohan.library.exception.IncorrectCredentialsException;
import com.mkohan.library.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails authenticate(String username, String password) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new IncorrectCredentialsException();
        }

        return userDetailsService.loadUserByUsername(username);
    }
}
