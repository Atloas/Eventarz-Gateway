package com.agh.EventarzGateway.model.groups;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Group {
    private String uuid;
    private String name;
    private String description;
    private String createdDate;
    private String founderUsername;
    private List<GroupMember> members;
    private List<GroupEvent> events;

    public boolean checkIfUserIsInGroup(String username) {
        for (GroupMember member : this.getMembers()) {
            if (member.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getEventUuids() {
        ArrayList<String> eventUuids = new ArrayList<>();
        for (GroupEvent event : this.getEvents()) {
            eventUuids.add(event.getEventUuid());
        }
        return eventUuids;
    }
}
