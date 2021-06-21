package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import com.agh.EventarzGateway.services.GroupService;
import org.springframework.security.access.annotation.Secured;
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
@Secured("USER")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

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
        GroupDTO groupDTO = groupService.editGroup(groupForm, uuid, principal);
        return groupDTO;
    }

    @DeleteMapping("/groups/{uuid}")
    public void deleteGroup(@PathVariable String uuid, Principal principal) {
        groupService.deleteGroup(uuid, principal);
    }

    @PostMapping("/groups/{uuid}/members")
    public GroupDTO join(@PathVariable String uuid, Principal principal) {
        GroupDTO groupDTO = groupService.join(uuid, principal);
        return groupDTO;
    }

    @DeleteMapping("/groups/{uuid}/members/{username}")
    public GroupDTO leave(@PathVariable String uuid, @PathVariable String username, Principal principal) {
        GroupDTO groupDTO = groupService.leave(uuid, principal);
        return groupDTO;
    }
}
