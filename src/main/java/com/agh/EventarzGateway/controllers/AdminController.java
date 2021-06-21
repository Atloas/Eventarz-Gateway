package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import com.agh.EventarzGateway.model.dtos.UserDTO;
import com.agh.EventarzGateway.model.dtos.UserShortDTO;
import com.agh.EventarzGateway.model.inputs.BanForm;
import com.agh.EventarzGateway.services.EventService;
import com.agh.EventarzGateway.services.GroupService;
import com.agh.EventarzGateway.services.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Secured("ADMIN")
public class AdminController {

    private final UserService userService;
    private final GroupService groupService;
    private final EventService eventService;

    public AdminController(UserService userService, GroupService groupService, EventService eventService) {
        this.userService = userService;
        this.groupService = groupService;
        this.eventService = eventService;
    }

    // USERS

    @GetMapping("admin/users")
    public List<UserShortDTO> findUsersByUsername(@RequestParam String username) {
        List<UserShortDTO> userShortDTOs = userService.findUsersByUsername(username);
        return userShortDTOs;
    }

    @GetMapping("admin/users/{username}")
    public UserDTO getUser(@PathVariable String username) {
        UserDTO userDTO = userService.getUser(username);
        return userDTO;
    }

    @PutMapping("admin/users/{username}/banned")
    public UserDTO changeBannedStatus(@RequestBody BanForm banForm, @PathVariable String username) {
        UserDTO userDTO = userService.changeBannedStatus(username, banForm);
        return userDTO;
    }

    // GROUPS

    @GetMapping("admin/groups")
    public List<GroupSearchedDTO> adminGetGroupsByName(@RequestParam String name) {
        List<GroupSearchedDTO> groupSearchedDTOs = groupService.getGroupsByName(name);
        return groupSearchedDTOs;
    }

    @GetMapping("admin/groups/{uuid}")
    public GroupDTO adminGetGroup(@PathVariable String uuid) {
        GroupDTO groupDTO = groupService.getGroup(uuid);
        return groupDTO;
    }

    @DeleteMapping("admin/groups/{uuid}")
    public void adminDeleteGroup(@PathVariable String uuid) {
        groupService.adminDeleteGroup(uuid);
    }

    // EVENTS

    @GetMapping("admin/events")
    public List<EventHomeDTO> getEventsByRegex(@RequestParam String name) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getEventsByName(name);
        return eventHomeDTOs;
    }

    @GetMapping("admin/events/{uuid}")
    public EventDTO adminGetEvent(@PathVariable String uuid) {
        EventDTO eventDTO = eventService.adminGetEvent(uuid);
        return eventDTO;
    }

    @DeleteMapping("admin/events/{uuid}")
    public void adminDeleteEvent(@PathVariable String uuid) {
        eventService.adminDeleteEvent(uuid);
    }
}
