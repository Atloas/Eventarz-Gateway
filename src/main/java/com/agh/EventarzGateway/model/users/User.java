package com.agh.EventarzGateway.model.users;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class User {
    private String username;
    private String passwordHash;
    private String registerDate;
    private String role;
    private boolean banned;

    public User (String username, String passwordHash, String role) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String registerDate = LocalDate.now().format(dtf);
        this.username = username;
        this.passwordHash = passwordHash;
        this.registerDate = registerDate;
        this.role = role;
        this.banned = false;
    }
}
