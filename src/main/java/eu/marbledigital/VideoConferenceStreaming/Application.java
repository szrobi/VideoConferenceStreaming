package eu.marbledigital.VideoConferenceStreaming;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.red5.io.utils.ObjectMap;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IServerStream;

import eu.marbledigital.VideoConference.Model.Red5Client;
import eu.marbledigital.VideoConference.Model.Room;
import eu.marbledigital.VideoConference.Model.User;

/**
 * Red5 Application for the Video Conference Streaming Service
 *
 * @author Robert Szabados <petrik.zsolt@marbledigital.eu>
 */
public class Application extends ApplicationAdapter {

	private IScope appScope;
	private IServerStream serverStream;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean appStart(IScope app) {
		super.appStart(app);

		log.info("Video Conference Streaming is starting up");

		appScope = app;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean appConnect(IConnection conn, Object[] params) {

		log.info("Video Conference Streaming appConnect");
		log.info(conn.toString());

		IScope scope = conn.getScope();

		log.info("App connect called for scope: {}", scope.getName());

		try {
			if (params.length != 1 || !(params[0] instanceof ObjectMap)) {
				log.info("Client rejected: invalid connection parameters");
				return false;
			}

			ObjectMap<String, Object> connectionParameters = (ObjectMap) params[0];

			if (!connectionParameters.containsKey("user_Id")
					|| !connectionParameters.containsKey("room_Token")) {
				log.info(
						"Client rejected: no userId or roomId or roomToken given ({})",
						conn.getRemoteAddress());
				return false;
			}

			log.info("Connecting to scope: " + scope.getPath() + "/"
					+ scope.getName());

		} catch (Exception e) {
			log.info("Client rejected: exception ({})", e);
			return false;
		}
		return super.appConnect(conn, params);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {

		IScope scope = conn.getScope();

		try {
			if (params.length != 1 || !(params[0] instanceof ObjectMap)) {
				log.info("Client rejected: invalid connection parameters");
				return false;
			}

			ObjectMap<String, Object> connectionParameters = (ObjectMap) params[0];

			if (!connectionParameters.containsKey("user_Id")) {
				log.info("Client rejected: no userId or roomId given ({})",
						conn.getRemoteAddress());
				return false;
			}

			Red5Client red5Client = new Red5Client();
			User user = red5Client.getUser(Integer
					.parseInt(connectionParameters.get("user_Id").toString()));
			Integer roomId = Integer.parseInt(scope.getName());
			Room room = red5Client.getRoom(roomId);

			List<User> joinedUsers = room.getJoinedUsers();
			log.info("Current user: " + user.toString());
			log.info("Joined users: " + joinedUsers.toString());

			for (IClient client : this.getClients()) {

					for (IConnection connection : client.getConnections()) {
						((IServiceCapableConnection) connection).invoke(
								"userConnected", new Object[] { user.getId(),
										user.getUsername() });
						log.info("userConnected method invoked");
					
				}
			}

			if (joinedUsers.contains(user)) {
				log.info("Joined users list contains current user",
						user.getUsername());

			}

			else {
				log.info("Connection rejected!");
				this.rejectClient();
				return false;
			}
		} catch (Exception e) {
			log.info("Client rejected: ", e);
			return false;
		}
		return super.roomConnect(conn, params);
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void appDisconnect(IConnection conn) {
		log.info("VideoConference app disconnected");
		if (appScope == conn.getScope() && serverStream != null) {
			serverStream.close();
		}
		super.appDisconnect(conn);
	}

}
