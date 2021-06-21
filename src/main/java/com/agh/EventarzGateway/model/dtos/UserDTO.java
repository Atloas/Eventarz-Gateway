package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String registerDate;
    private boolean banned;
    private List<UserGroupDTO> foundedGroups;
    private List<UserGroupDTO> joinedGroups;
    private List<UserEventDTO> organizedEvents;
    private List<UserEventDTO> joinedEvents;

    public UserDTO(User user, List<Group> foundedGroups, List<Group> joinedGroups, List<Event> organizedEvents, List<Event> joinedEvents) {
        this.username = user.getUsername();
        this.registerDate = user.getRegisterDate();
        this.banned = user.isBanned();

        this.foundedGroups = new ArrayList<>();
        for (Group group : foundedGroups) {
            this.foundedGroups.add(new UserGroupDTO(group));
        }

        this.joinedGroups = new ArrayList<>();
        for (Group group : joinedGroups) {
            this.joinedGroups.add(new UserGroupDTO(group));
        }

        this.organizedEvents = new ArrayList<>();
        for (Event event : organizedEvents) {
            this.organizedEvents.add(new UserEventDTO(event));
        }

        this.joinedEvents = new ArrayList<>();
        for (Event event : joinedEvents) {
            this.joinedEvents.add(new UserEventDTO(event));
        }
    }
}
