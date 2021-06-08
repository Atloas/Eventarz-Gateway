package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.config.JwtUtility;
import com.agh.EventarzGateway.exceptions.UserAlreadyExistsException;
import com.agh.EventarzGateway.feignClients.DataClient;
import com.agh.EventarzGateway.model.LoginForm;
import com.agh.EventarzGateway.model.NewUser;
import com.agh.EventarzGateway.model.RegisterForm;
import com.agh.EventarzGateway.model.SecurityDetails;
import com.agh.EventarzGateway.model.dtos.LoginResponseDTO;
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
import java.util.Arrays;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private DataClient dataClient;
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
        NewUser newUser = new NewUser();
        newUser.setUsername(registerForm.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(registerForm.getPassword()));
        newUser.setRoles(Arrays.asList("USER"));
        dataClient.createUser(newUser);
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
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token, user.getUsername(), roles);
        return loginResponseDTO;
    }

    public LoginResponseDTO refreshLogin(String username) {
        SecurityDetails securityDetails = dataClient.getSecurityDetails(username);
        String token = jwtUtility.generateAccessToken(username);
        return new LoginResponseDTO(token, username, securityDetails.getRoles());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Rethink authentication data flow?
        SecurityDetails securityDetails = dataClient.getSecurityDetails(username);
        if (securityDetails == null) {
            throw new UsernameNotFoundException("No user found for username " + username);
        }
        return new User(
                username,
                securityDetails.getPasswordHash(), true, true,
                true, !securityDetails.isBanned(),
                getAuthorities(securityDetails.getRoles())
        );
    }

    private boolean checkIfUsernameExists(String username) {
        try {
            dataClient.checkIfUserExists(username);
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
