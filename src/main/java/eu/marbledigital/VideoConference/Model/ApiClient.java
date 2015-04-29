package eu.marbledigital.VideoConference.Model;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Robert Szabados
 *
 */
public class ApiClient {
	
	/** 
	 * Gets all users from JSON
	 * 
	 * @return list of users
	 */
	public List<User> getAllUsers(){
		final String usersUri = "http://localhost:8080/api/v1/users/.json";
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

		final String userUri = "http://localhost:8080/api/v1/users/" + userId
				+ ".json";

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

		final String roomUri = "http://localhost:8080/api/v1/rooms/" + roomId
				+ ".json";

		RestTemplate restTemplate = new RestTemplate();
		Room room = restTemplate.getForObject(roomUri, Room.class);

		return room;

	}

}
