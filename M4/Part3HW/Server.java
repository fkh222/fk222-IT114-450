package M4.Part3HW;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;


public class Server {
    private int port = 3000;
    // connected clients
    // Use ConcurrentHashMap for thread-safe client management
    // the Long will be a unique client identifier, and ServerThread is the instance
    private final ConcurrentHashMap<Long, ServerThread> connectedClients = new ConcurrentHashMap<>();
    private boolean isRunning = true;

    private void start(int port) {
        this.port = port;
        // server listening
        System.out.println("Listening on port " + this.port);
        // Simplified client connection loop
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                System.out.println("Waiting for next client");
                Socket incomingClient = serverSocket.accept(); // blocking action, waits for a client connection
                System.out.println("Client connected");
                // wrap socket in a ServerThread, pass a callback to notify the Server when
                // they're initialized
                ServerThread serverThread = new ServerThread(incomingClient, this, this::onServerThreadInitialized);
                // start the thread (typically an external entity manages the lifecycle and we
                // don't have the thread start itself)
                serverThread.start();
                // Note: We don't yet add the ServerThread reference to our connectedClients map
            }
        } catch (IOException e) {
            System.err.println("Error accepting connection");
            e.printStackTrace();
        } finally {
            System.out.println("Closing server socket");
        }
    }

    /**
     * Callback passed to ServerThread to inform Server they're ready to receive
     * data
     * 
     * @param serverThread
     */
    private void onServerThreadInitialized(ServerThread serverThread) {
        // add to connected clients list (unique id and actual reference)
        connectedClients.put(serverThread.getClientId(), serverThread);
        relay(null, String.format("*User[%s] connected*", serverThread.getClientId()));
    }

    /**
     * Takes a ServerThread and removes them from the Server
     * Adding the synchronized keyword ensures that only one thread can execute
     * these methods at a time,
     * preventing concurrent modification issues and ensuring thread safety
     * 
     * @param serverThread
     */
    private synchronized void disconnect(ServerThread serverThread) {
        serverThread.disconnect();
        // remove disconnecting ServerThread from map
        ServerThread disconnectingServerThread = connectedClients.remove(serverThread.getClientId());
        if (disconnectingServerThread != null) {
            // Improved logging with user ID
            relay(null, "User[" + disconnectingServerThread.getClientId() + "] disconnected");
        }
    }

    /**
     * Relays the message from the sender to all connectedClients
     * Internally calls processCommand and evaluates as necessary.
     * Note: Clients that fail to receive a message get removed from
     * connectedClients.
     * Adding the synchronized keyword ensures that only one thread can execute
     * these methods at a time,
     * preventing concurrent modification issues and ensuring thread safety
     * 
     * @param message
     * @param sender  ServerThread (client) sending the message or null if it's a
     *                server-generated message
     */
    private synchronized void relay(ServerThread sender, String message) {
        // we'll temporarily use the thread id as the client identifier to
        // show in all client's chat. This isn't good practice since it's subject to
        // change as clients connect/disconnect (i.e., a reconnecting client likely
        // won't get the same id)
        // Note: any desired changes to the message must be done before this line
        String senderString = sender == null ? "Server" : String.format("User[%s]", sender.getClientId());
        // Note: formattedMessage must be final (or effectively final) since outside
        // scope can't changed inside a callback function (see removeIf() below)
        final String formattedMessage = String.format("%s: %s", senderString, message);
        // end temp identifier

        // loop over clients and send out the message; remove client if message failed
        // to be sent
        // Note: this uses a lambda expression for each item in the values() collection,
        // it's one way we can safely remove items during iteration

        connectedClients.values().removeIf(serverThread -> {
            boolean failedToSend = !serverThread.sendToClient(formattedMessage);
            if (failedToSend) {
                System.out.println(
                        String.format("Removing disconnected client[%s] from list", serverThread.getClientId()));
                disconnect(serverThread);
            }
            return failedToSend;
        });
    }

    // start handle actions
    /**
     * Expose access to the disconnect action
     * 
     * @param serverThread
     */
    protected synchronized void handleDisconnect(ServerThread sender) {
        disconnect(sender);
    }

    protected synchronized void handleReverseText(ServerThread sender, String text) {
        StringBuilder sb = new StringBuilder(text);
        sb.reverse();
        String rev = sb.toString();
        relay(sender, rev);
    }

    // fk222 6/23/25
    // flip command
    protected synchronized void handleFlip(ServerThread sender){
        double flipValue = Math.random();
        if (flipValue < 0.5) {
            relay(sender, String.format("User[%s] flipped a coin and got HEADS!", sender.getClientId()) );
        } else {
            relay(sender, String.format("User[%s] flipped a coin and got TAILS!", sender.getClientId()) );
        }
    }

    // fk222 6/23/25
    // private message command
    protected synchronized void handlePM(ServerThread sender, String text){
        // finish code here
        String[] splitInput = text.split(" ", 2); // splits targetID and message
        if (splitInput.length < 2){
            sender.sendToClient("From Server: Invalid format, use /pm <target ID> <message>");
            return;
        }

        String targetIdString = splitInput[0];
        String message = splitInput[1];

        try {
            long targetId = Long.parseLong(targetIdString);
            ServerThread targetUser = connectedClients.get(targetId);

            if (targetUser == null || !targetUser.isRunning()){ // checks is user doesn't exist or have disconnected
                sender.sendToClient("From Server: User is not found or may have disconnected");
                return;
            }

            String privateMessage = String.format("(Private) User[%s]: %s", sender.getClientId(), message);

            targetUser.sendToClient(privateMessage);
            sender.sendToClient(privateMessage);
        } catch (Exception e){ // in case something in user's input goes wrong
            sender.sendToClient("From Server: Error occured, please check format. Use /pm <target ID> <message>");
            e.printStackTrace();
        }
    }

    // fk222 6/23/25
    // shuffle message command
    protected synchronized void handleShuffle(ServerThread sender, String text){
        String[] words = text.split(" ");
        List<String> wordList = Arrays.asList(words);
        Collections.shuffle(wordList);
        String shuffledText = String.join(" ", wordList);
        relay(null, String.format("Shuffled from User[%s]: %s", sender.getClientId(), shuffledText));
    }

    protected synchronized void handleMessage(ServerThread sender, String text) {
        relay(sender, text);
    }
    // end handle actions

    public static void main(String[] args) {
        System.out.println("Server Starting");
        Server server = new Server();
        int port = 3000;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            // can ignore, will either be index out of bounds or type mismatch
            // will default to the defined value prior to the try/catch
        }
        server.start(port);
        System.out.println("Server Stopped");
    }

}