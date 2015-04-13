package eu.marbledigital.VideoConferenceStreaming;

import org.red5.io.utils.ObjectMap;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

import eu.marbledigital.VideoConference.Model.Red5Client;
import eu.marbledigital.VideoConference.Model.User;

/**
 * Red5 Application for the Video Conference Streaming Service
 *
 * @author Zsolt Petrik <petrik.zsolt@marbledigital.eu>
 */
public class Application extends ApplicationAdapter {

	private IScope appScope;

	// private ArrayList<Room> rooms = new ArrayList<>();

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

		IScope scope = conn.getScope();

		log.info("App connect called for scope: {}", scope.getName());

		try {
			if (params.length != 1 || !(params[0] instanceof ObjectMap)) {
				log.info("Client rejected: invalid connection parameters");
				return false;
			}

			ObjectMap<String, Object> connectionParameters = (ObjectMap) params[0];

			if (!connectionParameters.containsKey("user_Id")
					|| !connectionParameters.containsKey("room_Id")
					|| !connectionParameters.containsKey("room_Token")) {
				log.info(
						"Client rejected: no userId or roomId or roomToken given ({})",
						conn.getRemoteAddress());
				return false;
			}
			
			log.info(
					"Client connecting to roomId: {},roomToken: {}, userId: {}",
					connectionParameters.get("room_Id").toString(),
					connectionParameters.get("room_Token"),
					connectionParameters.get("user_Id").toString());
			log.info("SCOPE!!!!!!!!!!!!!!!!!!!!!!: " + scope.getPath() + "/"
					+ scope.getName());
		} catch (Exception e) {
			log.info("Client rejected: exception ({})", e);
			return false;
		}
		return super.appConnect(conn, params);
	}

	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		try {
			/*if (params.length != 1 || !(params[0] instanceof ObjectMap)) {
				log.info("Client rejected: invalid connection parameters");
				return false;
			}*/

			ObjectMap<String, Object> connectionParameters = (ObjectMap) params[0];

			/*if (!connectionParameters.containsKey("user_Id")) {
				log.info(
						"Client rejected: no userId or roomId or roomToken given ({})",
						conn.getRemoteAddress());
				return false;
			}*/
			Red5Client red5Client = new Red5Client();
			User user=red5Client.getUser(8);
			
			log.info("CURRENT USER: "+user.toString());
		}
		
		catch (Exception e) {
			log.info("Client rejected: exception ({})", e);
			return false;
		}
		
		
		return super.roomConnect(conn, params);
	}
}
