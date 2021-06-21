package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.LoginResponseDTO;
import com.agh.EventarzGateway.model.inputs.LoginForm;
import com.agh.EventarzGateway.model.inputs.RegisterForm;
import com.agh.EventarzGateway.services.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterForm registerForm) {
        authenticationService.register(registerForm);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginForm loginForm) {
        LoginResponseDTO loginResponseDTO = authenticationService.login(loginForm);
        return loginResponseDTO;
    }

    @PostMapping("/refreshLogin")
    public LoginResponseDTO refreshLogin(Principal principal) {
        LoginResponseDTO loginResponseDTO = authenticationService.refreshLogin(principal.getName());
        return loginResponseDTO;
    }
}
