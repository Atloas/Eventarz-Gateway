package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.config.JwtUtility;
import com.agh.EventarzGateway.exceptions.UserAlreadyExistsException;
import com.agh.EventarzGateway.feignClients.UsersClientWrapper;
import com.agh.EventarzGateway.model.dtos.LoginResponseDTO;
import com.agh.EventarzGateway.model.inputs.LoginForm;
import com.agh.EventarzGateway.model.inputs.RegisterForm;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    // Autowired alongside other security stuff to avoid a circular dependency
    @Autowired
    private UsersClientWrapper usersClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtility jwtUtility;

    public void register(RegisterForm registerForm) throws UserAlreadyExistsException {
        if (checkIfUsernameExists(registerForm.getUsername())) {
            throw new UserAlreadyExistsException("There already is an account with username " + registerForm.getUsername() + "!");
        }
        com.agh.EventarzGateway.model.users.User user = new com.agh.EventarzGateway.model.users.User(
                registerForm.getUsername(),
                passwordEncoder.encode(registerForm.getPassword()),
                "USER"
        );
        usersClient.createUser(user);
    }

    public LoginResponseDTO login(LoginForm loginForm) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));

        User user = (User) authenticate.getPrincipal();

        String token = jwtUtility.generateAccessToken(user);

        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            roles.add(authority.toString());
        }
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token, user.getUsername(), roles.get(0));
        return loginResponseDTO;
    }

    public LoginResponseDTO refreshLogin(String username) {
        com.agh.EventarzGateway.model.users.User user = usersClient.getUser(username);
        String token = jwtUtility.generateAccessToken(username);
        return new LoginResponseDTO(token, username, user.getRole());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            com.agh.EventarzGateway.model.users.User user = usersClient.getUser(username);
            return new User(
                    username,
                    user.getPasswordHash(), true, true,
                    true, !user.isBanned(),
                    getAuthorities(Collections.singletonList(user.getRole()))
            );
        } catch (FeignException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                throw new UsernameNotFoundException("No user found for username " + username);
            } else {
                throw e;
            }
        }
    }

    private boolean checkIfUsernameExists(String username) {
        try {
            usersClient.checkIfUserExists(username);
            return true;
        } catch (FeignException e) {
            if (e.status() != HttpStatus.NOT_FOUND.value()) {
                throw e;
            }
            return false;
        }
    }

    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
