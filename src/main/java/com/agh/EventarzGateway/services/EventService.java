package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.EventFullException;
import com.agh.EventarzGateway.exceptions.NotOrganizerException;
import com.agh.EventarzGateway.exceptions.UserNotInEventsGroupException;
import com.agh.EventarzGateway.feignClients.EventsClientWrapper;
import com.agh.EventarzGateway.feignClients.GroupsClientWrapper;
import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.EventForm;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final EventsClientWrapper eventsClient;
    private final GroupsClientWrapper groupsClient;

    public EventService(EventsClientWrapper eventsClient, GroupsClientWrapper groupsClient) {
        this.eventsClient = eventsClient;
        this.groupsClient = groupsClient;
    }

    public List<EventHomeDTO> getMyEvents(String username) {
        List<Event> events = eventsClient.getMyEvents(username);
        Map<String, Group> eventUuidToGroup = this.mapEventsToGroups(events);
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event, eventUuidToGroup.get(event.getUuid())));
        }
        return eventHomeDTOs;
    }

    public List<EventHomeDTO> getHomeEvents(String username) {
        List<Event> events = eventsClient.getHomeEvents(username, true);
        Map<String, Group> eventUuidToGroup = this.mapEventsToGroups(events);
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event, eventUuidToGroup.get(event.getUuid())));
        }
        return eventHomeDTOs;
    }

    public EventDTO postEvent(EventForm eventForm, String username) throws UserNotInEventsGroupException {
        Group group = groupsClient.getGroup(eventForm.getGroupUuid());
        if (!group.checkIfUserIsInGroup(username)) {
            throw new UserNotInEventsGroupException("User " + username + " is not in the target group!");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String publishedDate = LocalDateTime.now().format(dtf);
        EventForm completedForm = new EventForm(eventForm);
        completedForm.setOrganizerUsername(username);
        completedForm.setPublishedDate(publishedDate);
        Event event = eventsClient.createEvent(completedForm);
        EventDTO eventDTO = new EventDTO(event, group, true);
        return eventDTO;
    }

    public EventDTO getEvent(String uuid, String username) {
        Event event = eventsClient.getEvent(uuid);
        Group group = groupsClient.getGroup(event.getGroupUuid());
        return new EventDTO(event, group, group.checkIfUserIsInGroup(username));
    }

    public EventDTO updateEvent(String uuid, EventForm eventForm, String username) throws NotOrganizerException {
        Event event = eventsClient.getEvent(uuid);
        if (!event.getOrganizerUsername().equals(username)) {
            throw new NotOrganizerException("User " + username + " is not the organizer of event " + uuid + "!");
        }
        event = eventsClient.updateEvent(uuid, eventForm);
        Group group = groupsClient.getGroup(event.getGroupUuid());
        EventDTO eventDTO = new EventDTO(event, group, true);
        return eventDTO;
    }

    public void deleteEvent(String uuid, String username) throws NotOrganizerException {
        Event event = eventsClient.getEvent(uuid);
        if (!event.getOrganizerUsername().equals(username)) {
            throw new NotOrganizerException("User " + username + " is not the organizer of event " + uuid + " and cannot delete it.");
        }
        eventsClient.deleteEvents(new String[]{uuid});
    }

    public EventDTO join(String uuid, String username) throws UserNotInEventsGroupException, EventFullException {
        Event event = eventsClient.getEvent(uuid);
        Group group = groupsClient.getGroup(event.getGroupUuid());
        if (!group.checkIfUserIsInGroup(username)) {
            throw new UserNotInEventsGroupException("User " + username + " is not in the group of event " + uuid + "!");
        }
        if (event.getParticipants().size() < event.getMaxParticipants()) {
            event = eventsClient.joinEvent(uuid, username);
            return new EventDTO(event, group, true);
        } else {
            throw new EventFullException("This event is already full!");
        }
    }

    public EventDTO leave(String uuid, String username) {
        Event event = eventsClient.leaveEvent(uuid, username);
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
        Group group = groupsClient.getGroup(event.getGroupUuid());
        return new EventDTO(event, group, false);
    }

    public void adminDeleteEvent(String uuid) {
        eventsClient.deleteEvents(new String[]{uuid});
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
