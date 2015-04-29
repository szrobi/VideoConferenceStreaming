package eu.marbledigital.VideoConcerenceStreaming.Service;

import eu.marbledigital.VideoConferenceStreaming.Model.Room;
import eu.marbledigital.VideoConferenceStreaming.Model.User;
import java.util.List;

/**
 *
 * @author Robert Szabados
 *
 */
public interface IApiClient {

    public List<User> getAllUsers();

    public User getUser(Integer userId);

    public Room getRoom(Integer roomId);

}
