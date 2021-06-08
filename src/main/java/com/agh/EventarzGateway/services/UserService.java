package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.feignClients.DataClient;
import com.agh.EventarzGateway.model.BanForm;
import com.agh.EventarzGateway.model.User;
import com.agh.EventarzGateway.model.dtos.UserDTO;
import com.agh.EventarzGateway.model.dtos.UserShortDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private DataClient dataClient;

    public List<UserShortDTO> getUsersByRegex(String username) {
        List<User> users = dataClient.getUsersByRegex("(?i).*" + username + ".*");
        List<UserShortDTO> userShortDTOs = new ArrayList<>();
        for (User user : users) {
            userShortDTOs.add(new UserShortDTO(user));
        }
        return userShortDTOs;
    }

    public UserDTO getUser(String username) {
        User user = dataClient.getUser(username);
        UserDTO userDTO = new UserDTO(user);
        return userDTO;
    }

    public UserDTO changeBannedStatus(String username, BanForm banForm) {
        User user = dataClient.changeBanStatus(username, banForm);
        UserDTO userDTO = new UserDTO(user);
        return userDTO;
    }
}
