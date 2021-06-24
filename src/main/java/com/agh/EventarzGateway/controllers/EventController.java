package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import com.agh.EventarzGateway.model.inputs.EventForm;
import com.agh.EventarzGateway.services.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(value = "/events", params = {"username"})
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public List<EventHomeDTO> getMy(@RequestParam String username) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getMyEvents(username);
        return eventHomeDTOs;
    }

    @GetMapping(value = "/events", params = {"username", "home"})
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public List<EventHomeDTO> getHome(@RequestParam String username) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getHomeEvents(username);
        return eventHomeDTOs;
    }

    @PostMapping("/events")
    @PreAuthorize("hasRole('ROLE_USER') AND #eventForm.organizerUsername == authentication.principal.username")
    public EventDTO post(@Valid @RequestBody EventForm eventForm) {
        EventDTO eventDTO = eventService.postEvent(eventForm, eventForm.getOrganizerUsername());
        return eventDTO;
    }

    @GetMapping("/events/{uuid}")
    // Getting the username through Principal here but through a PathVariable elsewhere feels off, though it is more REST-y
    public EventDTO get(@PathVariable String uuid, Principal principal) {
        EventDTO eventDTO = eventService.getEvent(uuid, principal.getName());
        return eventDTO;
    }

    @PutMapping("/events/{uuid}")
    @PreAuthorize("hasRole('ROLE_USER') AND #eventForm.organizerUsername == authentication.principal.username")
    public EventDTO update(@Valid @RequestBody EventForm eventForm, @PathVariable String uuid) {
        EventDTO eventDTO = eventService.updateEvent(uuid, eventForm, eventForm.getOrganizerUsername());
        return eventDTO;
    }

    @DeleteMapping("/events/{uuid}")
    public void delete(@PathVariable String uuid, Principal principal) {
        eventService.deleteEvent(uuid, principal.getName());
    }

    @PostMapping("/events/{uuid}/participants")
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public EventDTO join(@PathVariable String uuid, @RequestBody String username) {
        EventDTO eventDTO = eventService.join(uuid, username);
        return eventDTO;
    }

    @DeleteMapping("/events/{uuid}/participants/{username}")
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public EventDTO leave(@PathVariable String uuid, @PathVariable String username) {
        EventDTO eventDTO = eventService.leave(uuid, username);
        return eventDTO;
    }
}
