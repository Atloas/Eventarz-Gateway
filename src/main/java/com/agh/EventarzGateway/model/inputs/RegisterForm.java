package com.agh.EventarzGateway.model.inputs;


import com.agh.EventarzGateway.model.validators.ValueMatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValueMatch(field = "password", otherField = "repeatPassword", message = "Password and repeat password don't match!")
public class RegisterForm {
    @Size(min = 5, message = "Username is too short!")
    @Size(max = 16, message = "Username is too long!")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Username contains invalid characters!")
    private String username;
    @Size(min = 8, message = "Password is too short!")
    @Size(max = 128, message = "Password is too long!")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*", message = "Password has to contain at least one upper case character!"),
            @Pattern(regexp = ".*[a-z].*", message = "Password has to contain at least one lower case character!"),
            @Pattern(regexp = ".*[0-9].*", message = "Password has to contain at least one number!")
    })
    private String password;
    @NotBlank(message = "Repeat password is blank!")
    private String repeatPassword;
}
