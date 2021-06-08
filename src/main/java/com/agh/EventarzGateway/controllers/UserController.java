package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.UserDTO;
import com.agh.EventarzGateway.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // Username is actually unused
    @GetMapping("/users/{username}")
    public UserDTO getUser(@PathVariable String username, Principal principal) {
        UserDTO userDTO = userService.getUser(principal.getName());
        return userDTO;
    }
}
