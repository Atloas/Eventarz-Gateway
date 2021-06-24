package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.FounderAttemptingToLeaveException;
import com.agh.EventarzGateway.exceptions.NotFounderException;
import com.agh.EventarzGateway.feignClients.EventsClientWrapper;
import com.agh.EventarzGateway.feignClients.GroupsClientWrapper;
import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {

    private final GroupsClientWrapper groupsClient;
    private final EventsClientWrapper eventsClient;
    public GroupService(GroupsClientWrapper groupsClient, EventsClientWrapper eventsClient) {
        this.groupsClient = groupsClient;
        this.eventsClient = eventsClient;
    }

    public List<GroupSearchedDTO> getMyGroups(String username) {
        List<Group> groups = groupsClient.getMyGroups(username);
        List<GroupSearchedDTO> groupSearchedDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupSearchedDTOs.add(new GroupSearchedDTO(group));
        }
        fillEventCountFields(groupSearchedDTOs);
        return groupSearchedDTOs;
    }

    public List<GroupSearchedDTO> getGroupsByName(String name) {
        List<Group> groups = groupsClient.getGroupsByName(name);
        List<GroupSearchedDTO> groupSearchedDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupSearchedDTOs.add(new GroupSearchedDTO(group));
        }
        fillEventCountFields(groupSearchedDTOs);
        return groupSearchedDTOs;
    }

    public GroupDTO createGroup(GroupForm groupForm, String username) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String createdDate = LocalDate.now().format(dtf);
        GroupForm completedForm = new GroupForm(groupForm);
        completedForm.setFounderUsername(username);
        completedForm.setCreatedDate(createdDate);
        Group group = groupsClient.createGroup(completedForm);
        GroupDTO groupDTO = new GroupDTO(group, new ArrayList<>());
        return groupDTO;
    }

    public GroupDTO getGroup(String uuid) {
        Group group = groupsClient.getGroup(uuid);
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public GroupDTO editGroup(GroupForm groupForm, String uuid, String username) throws NotFounderException {
        Group group = groupsClient.getGroup(uuid);
        if (!group.getFounderUsername().equals(username)) {
            throw new NotFounderException("User " + username + " is not the founder of group " + uuid + " and cannot edit it.");
        }
        group = groupsClient.updateGroup(uuid, groupForm);
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public void deleteGroup(String uuid, String username) throws NotFounderException {
        Group group = groupsClient.getGroup(uuid);
        if (!group.getFounderUsername().equals(username)) {
            throw new NotFounderException("User " + username + " is not the founder of group " + uuid + " and cannot delete it.");
        }
        eventsClient.deleteEventsByGroupUuid(uuid);
        groupsClient.deleteGroup(uuid);
    }

    // TODO: Is it really necessary to include this return here? Maybe just reload the group through the proper getGroup endpoint?
    public GroupDTO join(String uuid, String username) {
        Group group = groupsClient.joinGroup(uuid, username);
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public GroupDTO leave(String uuid, String username) throws FounderAttemptingToLeaveException {
        Group group = groupsClient.getGroup(uuid);
        if (group.getFounderUsername().equals(username)) {
            throw new FounderAttemptingToLeaveException("The founder can't leave their group!");
        }
        group = groupsClient.leaveGroup(uuid, username);
        eventsClient.removeUserFromEventsByGroupUuid(uuid, username);
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        // TODO: Don't reload events on group join/leave/edit?
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public void adminDeleteGroup(String uuid) {
        eventsClient.deleteEventsByGroupUuid(uuid);
        groupsClient.deleteGroup(uuid);
    }

    // This shouldn't modify the argument but I'd need to make deep copies of all group objects. Same above.
    private void fillEventCountFields(List<GroupSearchedDTO> groups) {
        List<String> uuids = new ArrayList<>();
        for (GroupSearchedDTO group : groups) {
            uuids.add(group.getUuid());
        }
        Map<String, Integer> counts = eventsClient.getEventCountsByGroupUuids(uuids.toArray(new String[0]), true);
        for (GroupSearchedDTO group : groups) {
            group.setEventCount(counts.get(group.getUuid()));
        }
    }
}
