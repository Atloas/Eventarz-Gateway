package com.agh.EventarzGateway.model.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {
    @NotBlank(message = "Username is blank!")
    private String username;
    @NotBlank(message = "Password is blank!")
    private String password;
}
