package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("eventarz-groups")
public interface GroupsClient {

    @GetMapping(value = "/groups", params = {"founderUsername"})
    List<Group> getFoundedGroups(@RequestParam String founderUsername);

    @GetMapping(value = "/groups", params = {"memberUsername"})
    List<Group> getJoinedGroups(@RequestParam String memberUsername);

    @GetMapping(value = "/groups", params = {"username"})
    List<Group> getMyGroups(@RequestParam String username);

    @GetMapping("/groups")
    List<Group> getGroupsByName(@RequestParam String name);

    @GetMapping("/groups")
    List<Group> getGroupsByUuids(@RequestParam String[] uuids);

    @PostMapping("/groups")
    Group createGroup(@RequestBody GroupForm groupForm);

    @GetMapping("/groups/{uuid}")
    Group getGroup(@PathVariable String uuid);

    @PutMapping("/groups/{uuid}")
    Group updateGroup(@PathVariable String uuid, @RequestBody GroupForm groupForm);

    @DeleteMapping("/groups/{uuid}")
    void deleteGroup(@PathVariable String uuid);

    @PostMapping("/groups/{uuid}/members")
    Group joinGroup(@PathVariable String uuid, @RequestBody String username);

    @DeleteMapping("/groups/{uuid}/members/{username}")
    Group leaveGroup(@PathVariable String uuid, @PathVariable String username);
}
