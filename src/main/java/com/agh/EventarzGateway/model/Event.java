package com.agh.EventarzGateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private String eventDate;
    @JsonIgnore
    private LocalDateTime eventDateObject;
    private String publishedDate;
    @JsonIgnore
    private LocalDateTime publishedDateObject;
    private boolean expired;
    private int participantCount;
    private boolean stripped;
    public User organizer;
    public List<User> participants;
    public Group group;

    public int getParticipantCount() {
        if (stripped) {
            return participantCount;
        } else {
            return participants.size();
        }
    }
}
