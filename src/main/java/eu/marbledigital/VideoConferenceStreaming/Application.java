package eu.marbledigital.VideoConferenceStreaming;

import java.util.List;

import org.red5.io.utils.ObjectMap;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IServerStream;

import eu.marbledigital.VideoConcerenceStreaming.Service.IApiClient;
import eu.marbledigital.VideoConferenceStreaming.Model.Room;
import eu.marbledigital.VideoConferenceStreaming.Model.User;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.red5.server.api.service.ServiceUtils;

/**
 * Red5 Application for the Video Conference Streaming Service
 *
 * @author Robert Szabados <petrik.zsolt@marbledigital.eu>
 */
public class Application extends ApplicationAdapter {

    private IScope appScope;
    private IServerStream serverStream;
    private IApiClient apiClient;

    private ConcurrentHashMap<IScope, List<User>> usersInRooms = new ConcurrentHashMap<>();

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
        log.info("Video Conference Streaming roomConnect");

        IScope roomScope = conn.getScope();

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

            User user = apiClient.getUser(Integer
                    .parseInt(connectionParameters.get("user_Id").toString()));
            Integer roomId = Integer.parseInt(roomScope.getName());
            Room room = apiClient.getRoom(roomId);

            List<User> joinedUsers = room.getJoinedUsers();

            if (joinedUsers.contains(user)) {
                log.debug("Joined users list contains current user ({})", user.getUsername());
            } else {
                log.info("Connection rejected!");
                rejectClient("User not in room.");
                return false;
            }

            ServiceUtils.invokeOnAllScopeConnections(roomScope, "userConnected", new Object[]{user.getId(), user.getUsername()}, null);

            conn.getClient().setAttribute("user", user);
        } catch (Exception e) {
            log.info("Client rejected: ", e);
            return false;
        }

        return super.roomConnect(conn, params);
    }

    @Override
    public boolean roomJoin(IClient client, IScope room) {
        log.info("Client ({}) joining room {}", client.getId(), room.getName());
        boolean ret = super.roomJoin(client, room);

        if (ret) {

            if (usersInRooms.get(room) == null) {
                List<User> existingUsers = usersInRooms.put(room, new ArrayList<>());

                if (existingUsers != null) {
                    existingUsers.add((User) client.getAttribute("user"));
                }
            } else {
                usersInRooms.get(room).add((User) client.getAttribute("user"));
            }

            if (client.getConnections().iterator().hasNext()) {
                ServiceUtils.invokeOnConnection(client.getConnections().iterator().next(), "usersInRoom", new Object[]{usersInRooms.get(room)}, null);
            } else {
                log.info("No connections for the client");
            }
        }

        return ret;
    }

    @Override
    public void roomLeave(IClient client, IScope room) {
        User user = (User) client.getAttribute("user");

        usersInRooms.get(room).remove(user);

        ServiceUtils.invokeOnAllScopeConnections(room, "userDisconnected", new Object[]{user.getId(), user.getUsername()}, null);

        super.roomLeave(client, room);
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

    public void setApiClient(IApiClient apiClient) {
        this.apiClient = apiClient;
    }

}
