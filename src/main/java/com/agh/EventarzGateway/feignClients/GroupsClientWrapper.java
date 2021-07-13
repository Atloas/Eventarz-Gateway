package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.exceptions.MicroserviceConnectionException;
import com.agh.EventarzGateway.model.groups.Group;
import com.agh.EventarzGateway.model.inputs.GroupForm;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.List;

// A wrapper class to fire GroupsClient methods but also translate thrown exceptions to adjust Resilience4j behaviour.
// Using Resilience4j annotations directly on the feign clients doesn't work.
// Using them for service methods has unwanted behaviour if a methods communicates with two services but the second one is unavailable.

@Component
@Retry(name = "UsersClientRetry")
@CircuitBreaker(name = "UsersClientCircuitBreaker")
public class GroupsClientWrapper {

    private final GroupsClient groupsClient;

    public GroupsClientWrapper(GroupsClient groupsClient) {
        this.groupsClient = groupsClient;
    }

    public List<Group> getFoundedGroups(String founderUsername) {
        try {
            return groupsClient.getFoundedGroups(founderUsername);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Group> getJoinedGroups(String memberUsername) {
        try {
            return groupsClient.getJoinedGroups(memberUsername);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Group> getMyGroups(String username) {
        try {
            return groupsClient.getMyGroups(username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Group> getGroupsByName(String name) {
        try {
            return groupsClient.getGroupsByName(name);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Group> getGroupsByUuids(String[] uuids) {
        try {
            return groupsClient.getGroupsByUuids(uuids);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Group createGroup(GroupForm groupForm) {
        try {
            return groupsClient.createGroup(groupForm);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Group getGroup(String uuid) {
        try {
            return groupsClient.getGroup(uuid);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Group updateGroup(String uuid, GroupForm groupForm) {
        try {
            return groupsClient.updateGroup(uuid, groupForm);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public void deleteGroup(String uuid) {
        try {
            groupsClient.deleteGroup(uuid);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Group joinGroup(String uuid, String username) {
        try {
            return groupsClient.joinGroup(uuid, username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Group leaveGroup(String uuid, String username) {
        try {
            return groupsClient.leaveGroup(uuid, username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    private RuntimeException handleFeignException(FeignException e) {
        if (e.status() < 0 || e.status() == 503) {
            // Translate exception to a type that isn't ignored, so retries, circuit breakers, etc. happen.
            return new MicroserviceConnectionException(e.getMessage());
        } else {
            // These should be exceptions that should be ignored by Resilience4j, like 404.
            return e;
        }
    }
}
