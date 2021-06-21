package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import com.agh.EventarzGateway.model.inputs.EventForm;
import com.agh.EventarzGateway.services.EventService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Secured("USER")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventHomeDTO> getMy(Principal principal) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getMyEvents(principal);
        return eventHomeDTOs;
    }

    @GetMapping(value = "/events", params = {"home"})
    public List<EventHomeDTO> getHome(Principal principal) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getHomeEvents(principal);
        return eventHomeDTOs;
    }

    @PostMapping("/events")
    public EventDTO post(@Valid @RequestBody EventForm eventForm, Principal principal) {
        EventDTO eventDTO = eventService.postEvent(eventForm, principal);
        return eventDTO;
    }

    @GetMapping("/events/{uuid}")
    public EventDTO get(@PathVariable String uuid, Principal principal) {
        EventDTO eventDTO = eventService.getEvent(uuid, principal);
        return eventDTO;
    }

    @PutMapping("/events/{uuid}")
    public EventDTO update(@Valid @RequestBody EventForm eventForm, @PathVariable String uuid, Principal principal) {
        EventDTO eventDTO = eventService.updateEvent(uuid, eventForm, principal);
        return eventDTO;
    }

    @DeleteMapping("/events/{uuid}")
    public void delete(@PathVariable String uuid, Principal principal) {
        eventService.deleteEvent(uuid, principal);
    }

    @PostMapping("/events/{uuid}/participants")
    public EventDTO join(@PathVariable String uuid, Principal principal) {
        EventDTO eventDTO = eventService.join(uuid, principal);
        return eventDTO;
    }

    // username is unused
    @DeleteMapping("/events/{uuid}/participants/{username}")
    public EventDTO leave(@PathVariable String uuid, @PathVariable String username, Principal principal) {
        EventDTO eventDTO = eventService.leave(uuid, principal);
        return eventDTO;
    }
}
