package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.exceptions.MicroserviceConnectionException;
import com.agh.EventarzGateway.model.events.Event;
import com.agh.EventarzGateway.model.inputs.EventForm;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// A wrapper class to fire EventsClient methods but also translate thrown exceptions to adjust Resilience4j behaviour.
// Using Resilience4j annotations directly on the feign clients doesn't work.
// Using them for service methods has unwanted behaviour if a methods communicates with two services but the second one is unavailable.

@Component
@Retry(name = "EventsClientRetry")
@CircuitBreaker(name = "EventsClientCircuitBreaker")
public class EventsClientWrapper {

    private final EventsClient eventsClient;

    public EventsClientWrapper(EventsClient eventsClient) {
        this.eventsClient = eventsClient;
    }

    public List<Event> getOrganizedEvents(String organizerUsername) {
        try {
            return eventsClient.getOrganizedEvents(organizerUsername);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Event> getJoinedEvents(String memberUsername) {
        try {
            return eventsClient.getJoinedEvents(memberUsername);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Event> getMyEvents(String username) {
        try {
            return eventsClient.getMyEvents(username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Event> getHomeEvents(String username, boolean home) {
        try {
            return eventsClient.getHomeEvents(username, home);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Event> getEventsByName(String name) {
        try {
            return eventsClient.getEventsByName(name);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public List<Event> getEventsByGroupUuid(String groupUuid) {
        try {
            return eventsClient.getEventsByGroupUuid(groupUuid);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Map<String, Integer> getEventCountsByGroupUuids(String[] groupUuids, boolean counts) {
        try {
            return eventsClient.getEventCountsByGroupUuids(groupUuids, counts);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Event createEvent(EventForm eventForm) {
        try {
            return eventsClient.createEvent(eventForm);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public void deleteEventsByGroupUuid(String groupUuid) {
        try {
            eventsClient.deleteEventsByGroupUuid(groupUuid);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public void removeUserFromEventsByGroupUuid(String groupUuid, String username) {
        try {
            eventsClient.removeUserFromEventsByGroupUuid(groupUuid, username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Event getEvent(String uuid) {
        try {
            return eventsClient.getEvent(uuid);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Event updateEvent(String uuid, EventForm eventForm) {
        try {
            return eventsClient.updateEvent(uuid, eventForm);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public void deleteEvents(String[] uuids) {
        eventsClient.deleteEvents(uuids);
    }

    public String getGroupUuid(String uuid) {
        try {
            return eventsClient.getGroupUuid(uuid);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Event joinEvent(String uuid, String username) {
        try {
            return eventsClient.joinEvent(uuid, username);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    public Event leaveEvent(String uuid, String username) {
        try {
            return eventsClient.leaveEvent(uuid, username);
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
