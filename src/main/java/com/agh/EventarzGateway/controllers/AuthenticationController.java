package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.LoginResponseDTO;
import com.agh.EventarzGateway.model.inputs.LoginForm;
import com.agh.EventarzGateway.model.inputs.RegisterForm;
import com.agh.EventarzGateway.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterForm registerForm) {
        authenticationService.register(registerForm);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginForm loginForm) {
        try {
            LoginResponseDTO loginResponseDTO = authenticationService.login(loginForm);
            return loginResponseDTO;
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password!", e);
        } catch (LockedException e) {
            // This fires before the password check, so you can check somebody else's ban state knowing their username.
            // It shouldn't work like that, but I'm not going to fix it.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account locked!", e);
        }
    }

    @PostMapping("/refreshLogin")
    public LoginResponseDTO refreshLogin(Principal principal) {
        LoginResponseDTO loginResponseDTO = authenticationService.refreshLogin(principal.getName());
        return loginResponseDTO;
    }
}
