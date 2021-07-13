package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.exceptions.MicroserviceConnectionException;
import com.agh.EventarzGateway.model.users.User;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.List;

// A wrapper class to fire UsersClient methods but also translate thrown exceptions to adjust Resilience4j behaviour.
// Using Resilience4j annotations directly on the feign clients doesn't work.
// Using them for service methods has unwanted behaviour if a methods communicates with two services but the second one is unavailable.

@Component
@Retry(name = "UsersClientRetry")
@CircuitBreaker(name = "UsersClientCircuitBreaker")
public class UsersClientWrapper {

    private final UsersClient usersClient;

    public UsersClientWrapper(UsersClient usersClient) {
        this.usersClient = usersClient;
    }

    public List<User> findUsersByUsername(String username) {
        try {
            return usersClient.findUsersByUsername(username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public void createUser(User user) {
        try {
            usersClient.createUser(user);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public User getUser(String username) {
        try {
            return usersClient.getUser(username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public String deleteUser(String username) {
        try {
            return usersClient.deleteUser(username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public void checkIfUserExists(String username) {
        try {
            usersClient.checkIfUserExists(username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public User changeBanStatus(String username, boolean banned) {
        try {
            return usersClient.changeBanStatus(username, banned);
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
