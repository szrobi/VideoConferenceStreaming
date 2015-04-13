package eu.marbledigital.VideoConference.Model;

import org.springframework.web.client.RestTemplate;

public class Red5Client {

	public User getUser(Integer userId) {

		final String uri = "http://localhost:8080/api/v1/users/:" + userId + ".json";

		RestTemplate restTemplate = new RestTemplate();
		User user = restTemplate.getForObject(uri, User.class);
		
		
		return user;

	}

}
