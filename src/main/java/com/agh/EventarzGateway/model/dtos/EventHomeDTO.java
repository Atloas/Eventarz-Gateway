package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventHomeDTO {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private int participantCount;
    private String eventDate;
    private GroupShortDTO group;
    private boolean happened;

    public EventHomeDTO(Event event, Group group) {
        this.uuid = event.getUuid();
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.participantCount = event.getParticipants().size();
        this.eventDate = event.getEventDate();
        this.group = new GroupShortDTO(group);
        this.happened = event.isHappened();
    }
}
