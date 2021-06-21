package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.events.EventParticipant;
import com.agh.EventarzGateway.model.groups.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private String eventDate;
    private String publishedDate;
    private boolean happened;
    private boolean allowed;
    private UserShortDTO organizer;
    private List<UserShortDTO> participants;
    private GroupShortDTO group;

    public EventDTO(Event event, Group group, boolean allowed) {
        this.uuid = event.getUuid();
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.eventDate = event.getEventDate();
        this.publishedDate = event.getPublishedDate();
        this.happened = event.isHappened();
        this.allowed = allowed;
        this.organizer = new UserShortDTO(event.getOrganizerUsername());
        this.participants = new ArrayList<>();
        for (EventParticipant participant : event.getParticipants()) {
            this.participants.add(new UserShortDTO(participant.getUsername()));
        }
        this.group = new GroupShortDTO(group);
    }
}
