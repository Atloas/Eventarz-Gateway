package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.groups.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String uuid;
    private String name;
    private String description;
    private String createdDate;
    private List<UserShortDTO> members;
    private List<EventShortDTO> events;
    private UserShortDTO founder;

    public GroupDTO(Group group, List<Event> events) {
        this.uuid = group.getUuid();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createdDate = group.getCreatedDate();

        this.members = new ArrayList<>();
        for (GroupMember member : group.getMembers()) {
            this.members.add(new UserShortDTO(member.getUsername()));
        }

        this.events = new ArrayList<>();
        for (Event event : events) {
            this.events.add(new EventShortDTO(event));
        }

        this.founder = new UserShortDTO(group.getFounderUsername());
    }
}
