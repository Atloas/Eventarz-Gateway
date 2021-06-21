package com.agh.EventarzGateway.model.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {
    private String uuid;
    private String groupUuid;
    private String name;
    private String description;
    private int maxParticipants;
    private String eventDate;
    @JsonIgnore
    private LocalDateTime eventDateObject;
    private String publishedDate;
    @JsonIgnore
    private LocalDateTime publishedDateObject;
    private boolean happened;
    private String organizerUsername;
    private List<EventParticipant> participants;
}
