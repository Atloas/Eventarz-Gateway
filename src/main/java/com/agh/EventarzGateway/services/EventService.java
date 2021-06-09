package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.EventFullException;
import com.agh.EventarzGateway.exceptions.NotOrganizerException;
import com.agh.EventarzGateway.exceptions.UserNotInEventsGroupException;
import com.agh.EventarzGateway.feignClients.EventsClient;
import com.agh.EventarzGateway.feignClients.GroupsClient;
import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.EventForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    @Autowired
    private EventsClient eventsClient;
    @Autowired
    private GroupsClient groupsClient;

    public List<EventHomeDTO> getMyEvents(Principal principal) {
        List<Event> events = eventsClient.getMyEvents(principal.getName());
        Map<String, Group> eventUuidToGroup = this.mapEventsToGroups(events);
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event, eventUuidToGroup.get(event.getUuid())));
        }
        return eventHomeDTOs;
    }

    public List<EventHomeDTO> getHomeEvents(Principal principal) {
        List<Event> events = eventsClient.getHomeEvents(principal.getName(), true);
        Map<String, Group> eventUuidToGroup = this.mapEventsToGroups(events);
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event, eventUuidToGroup.get(event.getUuid())));
        }
        return eventHomeDTOs;
    }

    public EventDTO postEvent(EventForm eventForm, Principal principal) throws UserNotInEventsGroupException {
        Group group = groupsClient.getGroup(eventForm.getGroupUuid());
        if (!group.checkIfUserIsInGroup(principal.getName())) {
            throw new UserNotInEventsGroupException("User " + principal.getName() + " is not in the target group!");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String publishedDate = LocalDateTime.now().format(dtf);
        EventForm completedForm = new EventForm(eventForm);
        completedForm.setOrganizerUsername(principal.getName());
        completedForm.setPublishedDate(publishedDate);
        Event event = eventsClient.createEvent(completedForm);
        EventDTO eventDTO = new EventDTO(event, group, true);
        return eventDTO;
    }

    public EventDTO getEvent(String uuid, Principal principal) throws UserNotInEventsGroupException {
        Event event = eventsClient.getEvent(uuid);
        Group group = groupsClient.getGroup(event.getGroupUuid());
        return new EventDTO(event, group, group.checkIfUserIsInGroup(principal.getName()));
    }

    public EventDTO updateEvent(String uuid, EventForm eventForm, Principal principal) throws NotOrganizerException {
        Event event = eventsClient.getEvent(uuid);
        if (!event.getOrganizerUsername().equals(principal.getName())) {
            throw new NotOrganizerException("User " + principal.getName() + " is not the organizer of event " + uuid + "!");
        }
        event = eventsClient.updateEvent(uuid, eventForm);
        Group group = groupsClient.getGroup(event.getGroupUuid());
        EventDTO eventDTO = new EventDTO(event, group, true);
        return eventDTO;
    }

    public void deleteEvent(String uuid, Principal principal) throws NotOrganizerException {
        Event event = eventsClient.getEvent(uuid);
        if (!event.getOrganizerUsername().equals(principal.getName())) {
            throw new NotOrganizerException("User " + principal.getName() + " is not the organizer of event " + uuid + " and cannot delete it.");
        }
        eventsClient.deleteEvents(new String[]{uuid});
        groupsClient.removeEvents(event.getGroupUuid(), new String[]{uuid});
    }

    public EventDTO join(String uuid, Principal principal) throws UserNotInEventsGroupException, EventFullException {
        Event event = eventsClient.getEvent(uuid);
        Group group = groupsClient.getGroup(event.getGroupUuid());
        if (!group.checkIfUserIsInGroup(principal.getName())) {
            throw new UserNotInEventsGroupException("User " + principal.getName() + " is not in the group of event " + uuid + "!");
        }
        if (event.getParticipants().size() < event.getMaxParticipants()) {
            event = eventsClient.joinEvent(uuid, principal.getName());
            return new EventDTO(event, group, true);
        } else {
            throw new EventFullException("This event is already full!");
        }
    }

    public EventDTO leave(String uuid, Principal principal) {
        Event event = eventsClient.leaveEvent(uuid, principal.getName());
        Group group = groupsClient.getGroup(event.getGroupUuid());
        return new EventDTO(event, group, true);
    }

    public List<EventHomeDTO> getEventsByName(String name) {
        List<Event> events = eventsClient.getEventsByName(name);
        Map<String, Group> eventUuidToGroup = this.mapEventsToGroups(events);
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event, eventUuidToGroup.get(event.getUuid())));
        }
        return eventHomeDTOs;
    }

    public EventDTO adminGetEvent(String uuid) {
        Event event = eventsClient.getEvent(uuid);
        Group group = groupsClient.getGroup(event.getUuid());
        return new EventDTO(event, group, false);
    }

    public void adminDeleteEvent(String uuid) {
        String groupUuid = eventsClient.getGroupUuid(uuid);
        eventsClient.deleteEvents(new String[]{uuid});
        groupsClient.removeEvents(groupUuid, new String[]{uuid});
    }

    private Map<String, Group> mapEventsToGroups(List<Event> events) {
        Map<String, String> eventUuidToGroupUuid = new HashMap<>();
        for (Event event : events) {
            eventUuidToGroupUuid.put(event.getUuid(), event.getGroupUuid());
        }
        List<Group> groups = groupsClient.getGroupsByUuids(eventUuidToGroupUuid.values().toArray(new String[0]));
        Map<String, Group> eventUuidToGroup = new HashMap<>();
        for (Event event : events) {
            for (Group group : groups) {
                if (event.getGroupUuid().equals(group.getUuid())) {
                    eventUuidToGroup.put(event.getUuid(), group);
                }
            }
        }
        return eventUuidToGroup;
    }
}
