package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.exceptions.FounderAttemptingToLeaveException;
import com.agh.EventarzGateway.exceptions.NotFounderException;
import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import com.agh.EventarzGateway.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Secured("USER")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/groups")
    public List<GroupSearchedDTO> getMyGroups(Principal principal) {
        List<GroupSearchedDTO> groupSearchedDTOs = groupService.getMyGroups(principal);
        return groupSearchedDTOs;
    }

    @GetMapping(value = "/groups", params = {"name"})
    public List<GroupSearchedDTO> getGroupsByName(@RequestParam String name) {
        List<GroupSearchedDTO> groupSearchedDTOs = groupService.getGroupsByName(name);
        return groupSearchedDTOs;
    }

    @PostMapping("/groups")
    public GroupDTO createGroup(@Valid @RequestBody GroupForm groupForm, Principal principal) {
        GroupDTO groupDTO = groupService.createGroup(groupForm, principal);
        return groupDTO;
    }

    @GetMapping("/groups/{uuid}")
    public GroupDTO getGroup(@PathVariable String uuid) {
        GroupDTO groupDTO = groupService.getGroup(uuid);
        return groupDTO;
    }

    @PutMapping("/groups/{uuid}")
    public GroupDTO editGroup(@Valid @RequestBody GroupForm groupForm, @PathVariable String uuid, Principal principal) {
        try {
            GroupDTO groupDTO = groupService.editGroup(groupForm, uuid, principal);
            return groupDTO;
        } catch (NotFounderException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this Group!", e);
        }
    }

    @DeleteMapping("/groups/{uuid}")
    public void deleteGroup(@PathVariable String uuid, Principal principal) {
        try {
            groupService.deleteGroup(uuid, principal);
        } catch (NotFounderException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this Group!", e);
        }
    }

    @PostMapping("/groups/{uuid}/members")
    public GroupDTO join(@PathVariable String uuid, Principal principal) {
        GroupDTO groupDTO = groupService.join(uuid, principal);
        return groupDTO;
    }

    @DeleteMapping("/groups/{uuid}/members/{username}")
    public GroupDTO leave(@PathVariable String uuid, @PathVariable String username, Principal principal) {
        try {
            GroupDTO groupDTO = groupService.leave(uuid, principal);
            return groupDTO;
        } catch (FounderAttemptingToLeaveException e) {
            // FORBIDDEN or BAD_REQUEST?
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot leave Groups you founded!", e);
        }
    }
}
