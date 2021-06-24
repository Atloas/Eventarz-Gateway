package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.UserDTO;
import com.agh.EventarzGateway.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{username}")
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public UserDTO getUser(@PathVariable String username) {
        UserDTO userDTO = userService.getUser(username);
        return userDTO;
    }
}
