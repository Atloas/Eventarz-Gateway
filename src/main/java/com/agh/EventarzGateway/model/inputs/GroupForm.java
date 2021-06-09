package com.agh.EventarzGateway.model.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupForm {
    @Size(min = 5, message = "Name is too short!")
    @Size(max = 32, message = "Name is too long!")
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]+", message = "Name contains invalid characters!")
    private String name;
    @Size(max = 1024, message = "Description is too long!")
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]+", message = "Description contains invalid characters!")
    private String description;
    // Filled by Gateway with principal.getName()
    private String founderUsername;
    // Filled by Gateway
    private String createdDate;

    public GroupForm(GroupForm that) {
        this.name = that.name;
        this.description = that.description;
        this.founderUsername = that.founderUsername;
    }
}
