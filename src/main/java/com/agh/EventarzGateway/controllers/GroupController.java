package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import com.agh.EventarzGateway.services.GroupService;
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
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping(value = "/groups", params = {"username"})
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public List<GroupSearchedDTO> getMyGroups(@RequestParam String username, Principal principal) {
        List<GroupSearchedDTO> groupSearchedDTOs = groupService.getMyGroups(username);
        return groupSearchedDTOs;
    }

    @GetMapping(value = "/groups", params = {"name"})
    public List<GroupSearchedDTO> getGroupsByName(@RequestParam String name) {
        List<GroupSearchedDTO> groupSearchedDTOs = groupService.getGroupsByName(name);
        return groupSearchedDTOs;
    }

    @PostMapping("/groups")
    @PreAuthorize("hasRole('ROLE_USER') AND #groupForm.founderUsername == authentication.principal.username")
    public GroupDTO createGroup(@Valid @RequestBody GroupForm groupForm) {
        GroupDTO groupDTO = groupService.createGroup(groupForm, groupForm.getFounderUsername());
        return groupDTO;
    }

    @GetMapping("/groups/{uuid}")
    public GroupDTO getGroup(@PathVariable String uuid) {
        GroupDTO groupDTO = groupService.getGroup(uuid);
        return groupDTO;
    }

    @PutMapping("/groups/{uuid}")
    @PreAuthorize("hasRole('ROLE_USER') AND #groupForm.founderUsername == authentication.principal.username")
    public GroupDTO editGroup(@Valid @RequestBody GroupForm groupForm, @PathVariable String uuid) {
        GroupDTO groupDTO = groupService.editGroup(groupForm, uuid, groupForm.getFounderUsername());
        return groupDTO;
    }

    @DeleteMapping("/groups/{uuid}")
    public void deleteGroup(@PathVariable String uuid, Principal principal) {
        groupService.deleteGroup(uuid, principal.getName());
    }

    @PostMapping("/groups/{uuid}/members")
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public GroupDTO join(@PathVariable String uuid, @RequestBody String username) {
        GroupDTO groupDTO = groupService.join(uuid, username);
        return groupDTO;
    }

    @DeleteMapping("/groups/{uuid}/members/{username}")
    @PreAuthorize("hasRole('ROLE_USER') AND #username == authentication.principal.username")
    public GroupDTO leave(@PathVariable String uuid, @PathVariable String username) {
        GroupDTO groupDTO = groupService.leave(uuid, username);
        return groupDTO;
    }
}
