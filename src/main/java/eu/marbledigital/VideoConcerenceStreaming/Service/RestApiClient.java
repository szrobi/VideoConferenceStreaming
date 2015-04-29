package eu.marbledigital.VideoConcerenceStreaming.Service;

import eu.marbledigital.VideoConferenceStreaming.Model.Room;
import eu.marbledigital.VideoConferenceStreaming.Model.User;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author pezia
 */
public class RestApiClient implements IApiClient {

    private String apiServerBaseUri;

    /**
     * Gets all users from JSON
     *
     * @return list of users
     */
    public List<User> getAllUsers() {
        String usersUri = apiServerBaseUri + "/api/v1/users";

        RestTemplate restTemplate = new RestTemplate();
        User[] usersArray = restTemplate.getForObject(usersUri, User[].class);
        List<User> usersList = Arrays.asList(usersArray);

        return usersList;
    }

    /**
     * Gets a single user from JSON by its id
     *
     * @param userId
     * @return a User object
     */
    public User getUser(Integer userId) {
        String userUri = apiServerBaseUri + "/api/v1/users/" + userId;

        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(userUri, User.class);

        return user;

    }

    /**
     * Gets a single room from JSON by its id
     *
     * @param roomId
     * @return a Room object
     */
    public Room getRoom(Integer roomId) {
        String roomUri = apiServerBaseUri + "/api/v1/rooms/" + roomId;

        RestTemplate restTemplate = new RestTemplate();
        Room room = restTemplate.getForObject(roomUri, Room.class);

        return room;
    }

    public void setApiServerBaseUri(String apiServerBaseUri) {
        this.apiServerBaseUri = apiServerBaseUri;
    }

}
