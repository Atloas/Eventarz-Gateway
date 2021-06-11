package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.feignClients.EventsClient;
import com.agh.EventarzGateway.feignClients.GroupsClient;
import com.agh.EventarzGateway.feignClients.UsersClient;
import com.agh.EventarzGateway.model.dtos.UserDTO;
import com.agh.EventarzGateway.model.dtos.UserShortDTO;
import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.BanForm;
import com.agh.EventarzGateway.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UsersClient usersClient;
    private final GroupsClient groupsClient;
    private final EventsClient eventsClient;

    public UserService(UsersClient usersClient, GroupsClient groupsClient, EventsClient eventsClient) {
        this.usersClient = usersClient;
        this.groupsClient = groupsClient;
        this.eventsClient = eventsClient;
    }

    public List<UserShortDTO> getUsersByRegex(String username) {
        List<User> users = usersClient.getUsersByName(username);
        List<UserShortDTO> userShortDTOs = new ArrayList<>();
        for (User user : users) {
            userShortDTOs.add(new UserShortDTO(user));
        }
        return userShortDTOs;
    }

    public UserDTO getUser(String username) {
        User user = usersClient.getUser(username);
        // TODO: Parallelize
        List<Group> foundedGroups = groupsClient.getFoundedGroups(username);
        List<Group> joinedGroups = groupsClient.getJoinedGroups(username);
        List<Event> organizedEvents = eventsClient.getOrganizedEvents(username);
        List<Event> joinedEvents = eventsClient.getJoinedEvents(username);
        UserDTO userDTO = new UserDTO(user, foundedGroups, joinedGroups, organizedEvents, joinedEvents);
        return userDTO;
    }

    // TODO: Is it necessary to update this info on front?
    public UserDTO changeBannedStatus(String username, BanForm banForm) {
        User user = usersClient.changeBanStatus(username, banForm);
        List<Group> foundedGroups = groupsClient.getFoundedGroups(username);
        List<Group> joinedGroups = groupsClient.getJoinedGroups(username);
        List<Event> organizedEvents = eventsClient.getOrganizedEvents(username);
        List<Event> joinedEvents = eventsClient.getJoinedEvents(username);
        UserDTO userDTO = new UserDTO(user, foundedGroups, joinedGroups, organizedEvents, joinedEvents);
        return userDTO;
    }
}
