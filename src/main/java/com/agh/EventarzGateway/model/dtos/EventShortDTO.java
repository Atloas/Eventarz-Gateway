package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDTO {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private int participantCount;
    private String eventDate;
    private boolean happened;

    public EventShortDTO(Event event) {
        this.uuid = event.getUuid();
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.participantCount = event.getParticipants().size();
        this.eventDate = event.getEventDate();
        this.happened = event.isHappened();
    }
}
