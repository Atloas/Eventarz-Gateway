package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.inputs.EventForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("eventarz-events")
public interface EventsClient {

    @GetMapping(value = "/events", params = {"organizerUsername"})
    List<Event> getOrganizedEvents(@RequestParam String organizerUsername);

    @GetMapping(value = "/events", params = {"memberUsername"})
    List<Event> getJoinedEvents(@RequestParam String memberUsername);

    @GetMapping(value = "/events", params = {"username"})
    List<Event> getMyEvents(@RequestParam String username);

    @GetMapping("/events")
    List<Event> getHomeEvents(@RequestParam String username, @RequestParam boolean home);

    @GetMapping("/events")
    List<Event> getEventsByName(@RequestParam String name);

    @GetMapping("/events")
    List<Event> getEventsByGroupUuid(@RequestParam String groupUuid);

    @GetMapping(value = "/events", params = {"groupUuids", "counts"})
    Map<String, Integer> getEventCountsByGroupUuids(@RequestParam String[] groupUuids, @RequestParam boolean counts);

    @PostMapping("/events")
    Event createEvent(@RequestBody EventForm eventForm);

    @DeleteMapping(value = "/events", params = {"groupUuid"})
    void deleteEventsByGroupUuid(@RequestParam String groupUuid);

    @DeleteMapping(value = "/events", params = {"groupUuid", "username"})
    void removeUserFromEventsByGroupUuid(@RequestParam String groupUuid, @RequestParam String username);

    @GetMapping("/events/{uuid}")
    Event getEvent(@PathVariable String uuid);

    @PutMapping("/events/{uuid}")
    Event updateEvent(@PathVariable String uuid, @RequestBody EventForm eventForm);

    @DeleteMapping("/events/{uuids}")
    void deleteEvents(@PathVariable String[] uuids);

    @GetMapping(value = "/events/{uuid}/groupUuid")
    String getGroupUuid(@PathVariable String uuid);

    @PostMapping("/events/{uuid}/participants")
    Event joinEvent(@PathVariable String uuid, @RequestBody String username);

    @DeleteMapping("/events/{uuid}/participants/{username}")
    Event leaveEvent(@PathVariable String uuid, @PathVariable String username);
}
