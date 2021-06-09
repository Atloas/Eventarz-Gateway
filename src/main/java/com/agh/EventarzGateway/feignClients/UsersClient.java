package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.model.inputs.BanForm;
import com.agh.EventarzGateway.model.users.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("eventarz-users")
public interface UsersClient {

    @GetMapping("/users")
    List<User> getUsersByName(@RequestParam String name);

    @PostMapping("/users")
    User createUser(@RequestBody User user);

    @GetMapping("/users/{username}")
    User getUser(@PathVariable String username);

    @DeleteMapping("/users/{username}")
    String deleteUser(@PathVariable String username);

    @RequestMapping(method = RequestMethod.HEAD, value = "/users/{username}")
    void checkIfUserExists(@PathVariable String username);

    @PutMapping("users/{username}/securityDetails/banned")
    User changeBanStatus(@PathVariable String username, @RequestBody BanForm banForm);
}
