package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.FounderAttemptingToLeaveException;
import com.agh.EventarzGateway.exceptions.NotFounderException;
import com.agh.EventarzGateway.feignClients.EventsClient;
import com.agh.EventarzGateway.feignClients.GroupsClient;
import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupsClient groupsClient;
    @Autowired
    private EventsClient eventsClient;

    public List<GroupSearchedDTO> getMyGroups(Principal principal) {
        List<Group> groups = groupsClient.getMyGroups(principal.getName());
        List<GroupSearchedDTO> groupSearchedDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupSearchedDTOs.add(new GroupSearchedDTO(group));
        }
        return groupSearchedDTOs;
    }

    public List<GroupSearchedDTO> getGroupsByName(String name) {
        List<Group> groups = groupsClient.getGroupsByName(name);
        List<GroupSearchedDTO> groupSearchedDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupSearchedDTOs.add(new GroupSearchedDTO(group));
        }
        return groupSearchedDTOs;
    }

    public GroupDTO createGroup(GroupForm groupForm, Principal principal) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String createdDate = LocalDate.now().format(dtf);
        GroupForm completedForm = new GroupForm(groupForm);
        completedForm.setFounderUsername(principal.getName());
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

    public GroupDTO editGroup(GroupForm groupForm, String uuid, Principal principal) throws NotFounderException {
        Group group = groupsClient.getGroup(uuid);
        if (!group.getFounderUsername().equals(principal.getName())) {
            throw new NotFounderException("User " + principal.getName() + " is not the founder of group " + uuid + " and cannot edit it.");
        }
        group = groupsClient.updateGroup(uuid, groupForm);
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public void deleteGroup(String uuid, Principal principal) throws NotFounderException {
        Group group = groupsClient.getGroup(uuid);
        if (!group.getFounderUsername().equals(principal.getName())) {
            throw new NotFounderException("User " + principal.getName() + " is not the founder of group " + uuid + " and cannot delete it.");
        }
        eventsClient.deleteEvents(group.getEventUuids().toArray(new String[0]));
        groupsClient.deleteGroup(uuid);
    }

    // TODO: Is it really necessary to include this return here? Maybe just reload the group through the proper getGroup endpoint?
    public GroupDTO join(String uuid, Principal principal) {
        Group group = groupsClient.joinGroup(uuid, principal.getName());
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public GroupDTO leave(String uuid, Principal principal) throws FounderAttemptingToLeaveException {
        Group group = groupsClient.getGroup(uuid);
        if (group.getFounderUsername().equals(principal.getName())) {
            throw new FounderAttemptingToLeaveException("The founder can't leave their group!");
        }
        group = groupsClient.leaveGroup(uuid, principal.getName());
        eventsClient.removeUserFromEventsByGroupUuid(uuid, principal.getName());
        List<Event> events = eventsClient.getEventsByGroupUuid(group.getUuid());
        GroupDTO groupDTO = new GroupDTO(group, events);
        return groupDTO;
    }

    public void adminDeleteGroup(String uuid) {
        // TODO: Just get the Uuids? Here and in deleteGroup
        Group group = groupsClient.getGroup(uuid);
        eventsClient.deleteEvents(group.getEventUuids().toArray(new String[0]));
        groupsClient.deleteGroup(uuid);
    }
}
