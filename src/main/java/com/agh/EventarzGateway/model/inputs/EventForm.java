package com.agh.EventarzGateway.model.inputs;

import com.agh.EventarzGateway.model.validators.FutureDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventForm {
    @Size(min = 5, message = "Name too short!")
    @Size(max = 32, message = "Name too long!")
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]+", message = "Name contains invalid characters!")
    private String name;
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]*", message = "Description contains invalid characters!")
    private String description;
    @Min(value = 1, message = "Max participants has to be at least 1!")
    @Max(value = 100, message = "Max participants has to be at most 100!")
    private int maxParticipants;
    @NotBlank(message = "Date field blank!")
    @FutureDate(message = "Date is invalid!")
    private String eventDate;
    private boolean participate;
    // Filled by Gateway with principal.getName()
    private String organizerUsername;
    @NotBlank(message = "Group uuid is blank!")
    private String groupUuid;
    // Filled by Gateway
    private String publishedDate;

    public EventForm(EventForm that) {
        this.name = that.name;
        this.description = that.description;
        this.maxParticipants = that.maxParticipants;
        this.eventDate = that.eventDate;
        this.participate = that.participate;
        this.organizerUsername = that.organizerUsername;
        this.groupUuid = that.groupUuid;
    }
}
