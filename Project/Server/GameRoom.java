package Project.Server;

import Project.Common.Constants;
import Project.Common.Grid;
import Project.Common.LoggerUtil;
import Project.Common.Payload;
import Project.Common.PayloadType;
import Project.Common.Phase;
import Project.Common.TextFX;
import Project.Common.TextFX.Color;
import Project.Common.TimedEvent;
import Project.Exceptions.MissingCurrentPlayerException;
import Project.Exceptions.NotPlayersTurnException;
import Project.Exceptions.NotReadyException;
import Project.Exceptions.PhaseMismatchException;
import Project.Exceptions.PlayerNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameRoom extends BaseGameRoom {

    // used for general rounds (usually phase-based turns)
    private TimedEvent roundTimer = null;

    // used for granular turn handling (usually turn-order turns)
    private TimedEvent turnTimer = null;
    private List<ServerThread> turnOrder = new ArrayList<>();
    private long currentTurnClientId = Constants.DEFAULT_CLIENT_ID;
    private int round = 0;

    // Project specifics
    private Grid board = new Grid(); // generated in onSessionStart()
    private ServerThread currentDrawer; // selected drawer of the round
    private List<String> wordList = new ArrayList<>(); // word list
    private String chosenWord = "";



    public GameRoom(String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClientAdded(ServerThread sp) {
        // sync GameRoom state to new client
        syncCurrentPhase(sp);
        syncReadyStatus(sp);
        syncTurnStatus(sp);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClientRemoved(ServerThread sp) {
        // added after Summer 2024 Demo
        // Stops the timers so room can clean up
        LoggerUtil.INSTANCE.info("Player Removed, remaining: " + clientsInRoom.size());
        long removedClient = sp.getClientId();
        turnOrder.removeIf(player -> player.getClientId() == sp.getClientId());
        if (clientsInRoom.isEmpty()) {
            resetReadyTimer();
            resetTurnTimer();
            resetRoundTimer();
            onSessionEnd();
        } else if (removedClient == currentTurnClientId) {
            onTurnStart();
        }
    }

    // timer handlers
    private void startRoundTimer() {
        roundTimer = new TimedEvent(60, () -> onRoundEnd());
        roundTimer.setTickCallback((time) -> System.out.println("Round Time: " + time));
    }

    private void resetRoundTimer() {
        if (roundTimer != null) {
            roundTimer.cancel();
            roundTimer = null;
        }
    }

    private void startTurnTimer() {
        turnTimer = new TimedEvent(30, () -> onTurnEnd());
        turnTimer.setTickCallback((time) -> System.out.println("Turn Time: " + time));
    }

    private void resetTurnTimer() {
        if (turnTimer != null) {
            turnTimer.cancel();
            turnTimer = null;
        }
    }

    // HELPER METHODS
    // load word list
    private void loadWordList(){
        try (BufferedReader reader = new BufferedReader(new FileReader("Project/Server/words.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                wordList.add(line);
            }
        }
        LoggerUtil.INSTANCE.info("Loaded " + wordList.size() + " words.");
    } catch (IOException e) {
        LoggerUtil.INSTANCE.warning("Failed to load words: " + e.getMessage());
    }
    }
    // mask selected word to blanks for guessers
    private String maskWord(String word) {
        return word.replaceAll("[A-Za-z]", "_");
    }

    // end timer handlers

    // lifecycle methods

    /** {@inheritDoc} */
    @Override
    protected void onSessionStart() {
        LoggerUtil.INSTANCE.info("onSessionStart() start");
        changePhase(Phase.IN_PROGRESS);
        currentTurnClientId = Constants.DEFAULT_CLIENT_ID;
        setTurnOrder();
        round = 0;
        // sync board dimensions with clients
        board.generate(8, 8, true);
        // TODO: sync the board to clients
        
        // load in word list
        loadWordList();
        LoggerUtil.INSTANCE.info(TextFX.colorize("Drawing Board generated: " + board, Color.PURPLE));
        LoggerUtil.INSTANCE.info("onSessionStart() end");
        onRoundStart();
    }

    /** {@inheritDoc} */
    @Override
    protected void onRoundStart() {
        LoggerUtil.INSTANCE.info("onRoundStart() start");
        resetRoundTimer();
        resetTurnStatus();
        round++;
        relay(null, String.format("Round %d has started", round));

        chosenWord = wordList.get(new Random().nextInt(wordList.size())); // select random word from word list
        wordList.remove(chosenWord); // remove selected word from list to avoid duplicates in later rounds

        // relay selected word to drawer or blank word to guessers
        try {
            currentDrawer = getNextPlayer();
            currentDrawer.setDrawer(true);
            Payload message = new Payload();
            message.setPayloadType(PayloadType.MESSAGE);
            clientsInRoom.values().forEach(spInRoom -> {
                if (spInRoom.getClientId()!=currentDrawer.getClientId()){
                    message.setMessage(String.format("It's %s's turn. Guess the word: %s (You have 60 seconds).", currentDrawer.getDisplayName(), TextFX.colorize(maskWord(chosenWord), Color.GREEN)));
                    spInRoom.sendToClient(message);
                    // sends blank word to guessers
                }
                else{
                    message.setMessage(String.format("It is your turn. Draw: %s \n You have 60 seconds.", TextFX.colorize(chosenWord, Color.GREEN)));
                    currentDrawer.sendToClient(message);
                }
            });
        } catch (MissingCurrentPlayerException | PlayerNotFoundException e) {
            e.printStackTrace();
        }
        relay(null, board.toString());
        startRoundTimer(); // 60 seconds per round, unless all guessers win before timer ends
        LoggerUtil.INSTANCE.info("onRoundStart() end");
    }

    // turns are generally unused for this drawing game
    /** {@inheritDoc} */
    @Override
    protected void onTurnStart() {
        LoggerUtil.INSTANCE.info("onTurnStart() start");
        resetTurnTimer();
        try {
            ServerThread currentPlayer = getNextPlayer();
            relay(null, String.format("It's %s's turn", currentPlayer.getDisplayName()));
        } catch (MissingCurrentPlayerException | PlayerNotFoundException e) {

            e.printStackTrace();
        }
        startTurnTimer();
        LoggerUtil.INSTANCE.info("onTurnStart() end");
    }

    // Note: logic between Turn Start and Turn End is typically handled via timers
    // and user interaction
    /** {@inheritDoc} */
    @Override
    protected void onTurnEnd() {
        LoggerUtil.INSTANCE.info("onTurnEnd() start");
        resetTurnTimer(); // reset timer if turn ended without the time expiring
        try {
            // optionally can use checkAllTookTurn();
            if (isLastPlayer()) {
                // if the current player is the last player in the turn order, end the round
                onRoundEnd();
            } else {
                onTurnStart();
            }
        } catch (MissingCurrentPlayerException | PlayerNotFoundException e) {

            e.printStackTrace();
        }
        LoggerUtil.INSTANCE.info("onTurnEnd() end");
    }

    // Note: logic between Round Start and Round End is typically handled via timers
    // and user interaction
    /** {@inheritDoc} */
    @Override
    protected void onRoundEnd() {
        LoggerUtil.INSTANCE.info("onRoundEnd() start");
        resetRoundTimer(); // reset timer if round ended without the time expiring

        LoggerUtil.INSTANCE.info("onRoundEnd() end");
        if (round >= clientsInRoom.size()) {
            onSessionEnd();
        } else {
            onRoundStart();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onSessionEnd() {
        LoggerUtil.INSTANCE.info("onSessionEnd() start");
        turnOrder.clear();
        currentTurnClientId = Constants.DEFAULT_CLIENT_ID;
        resetReadyStatus();
        resetTurnStatus();
        changePhase(Phase.READY);
        LoggerUtil.INSTANCE.info("onSessionEnd() end");
    }
    // end lifecycle methods

    // send/sync data to ServerThread(s)

    // sync canvas/board to clients
    private void sendCanvasUpdate(int x, int y, String color){
        clientsInRoom.values().forEach(spInRoom -> {
            boolean failedToSend = !spInRoom.sendCanvasUpdate(x,y,color);
            if (failedToSend) {
                removeClient(spInRoom);
            }
        });
    }

    private void sendResetTurnStatus() {
        clientsInRoom.values().forEach(spInRoom -> {
            boolean failedToSend = !spInRoom.sendResetTurnStatus();
            if (failedToSend) {
                removeClient(spInRoom);
            }
        });
    }

    private void sendTurnStatus(ServerThread client, boolean tookTurn) {
        clientsInRoom.values().removeIf(spInRoom -> {
            boolean failedToSend = !spInRoom.sendTurnStatus(client.getClientId(), client.didTakeTurn());
            if (failedToSend) {
                removeClient(spInRoom);
            }
            return failedToSend;
        });
    }

    private void syncTurnStatus(ServerThread incomingClient) {
        clientsInRoom.values().forEach(serverUser -> {
            if (serverUser.getClientId() != incomingClient.getClientId()) {
                boolean failedToSync = !incomingClient.sendTurnStatus(serverUser.getClientId(),
                        serverUser.didTakeTurn(), true);
                if (failedToSync) {
                    LoggerUtil.INSTANCE.warning(
                            String.format("Removing disconnected %s from list", serverUser.getDisplayName()));
                    disconnect(serverUser);
                }
            }
        });
    }

    // end send data to ServerThread(s)

    // misc methods
    private void resetTurnStatus() {
        clientsInRoom.values().forEach(sp -> {
            sp.setTookTurn(false);
        });
        sendResetTurnStatus();
    }

    /**
     * Sets `turnOrder` to a shuffled list of players who are ready.
     */
    private void setTurnOrder() {
        turnOrder.clear();
        turnOrder = clientsInRoom.values().stream().filter(ServerThread::isReady).collect(Collectors.toList());
        Collections.shuffle(turnOrder);
    }

    /**
     * Gets the current player based on the `currentTurnClientId`.
     * 
     * @return
     * @throws MissingCurrentPlayerException
     * @throws PlayerNotFoundException
     */
    private ServerThread getCurrentPlayer() throws MissingCurrentPlayerException, PlayerNotFoundException {
        // quick early exit
        if (currentTurnClientId == Constants.DEFAULT_CLIENT_ID) {
            throw new MissingCurrentPlayerException("Current Player not set");
        }
        return turnOrder.stream()
                .filter(sp -> sp.getClientId() == currentTurnClientId)
                .findFirst()
                // this shouldn't occur but is included as a "just in case"
                .orElseThrow(() -> new PlayerNotFoundException("Current player not found in turn order"));
    }

    /**
     * Gets the next player in the turn order.
     * If the current player is the last in the turn order, it wraps around
     * (round-robin).
     * 
     * @return
     * @throws MissingCurrentPlayerException
     * @throws PlayerNotFoundException
     */
    private ServerThread getNextPlayer() throws MissingCurrentPlayerException, PlayerNotFoundException {
        int index = 0;
        if (currentTurnClientId != Constants.DEFAULT_CLIENT_ID) {
            index = turnOrder.indexOf(getCurrentPlayer()) + 1;
            if (index >= turnOrder.size()) {
                index = 0;
            }
        }
        ServerThread nextPlayer = turnOrder.get(index);
        currentTurnClientId = nextPlayer.getClientId();
        return nextPlayer;
    }

    /**
     * Checks if the current player is the last player in the turn order.
     * 
     * @return
     * @throws MissingCurrentPlayerException
     * @throws PlayerNotFoundException
     */
    private boolean isLastPlayer() throws MissingCurrentPlayerException, PlayerNotFoundException {
        // check if the current player is the last player in the turn order
        return turnOrder.indexOf(getCurrentPlayer()) == (turnOrder.size() - 1);
    }

    private void checkAllTookTurn() {
        int numReady = clientsInRoom.values().stream()
                .filter(sp -> sp.isReady())
                .toList().size();
        int numTookTurn = clientsInRoom.values().stream()
                // ensure to verify the isReady part since it's against the original list
                .filter(sp -> sp.isReady() && sp.didTakeTurn())
                .toList().size();
        if (numReady == numTookTurn) {
            relay(null,
                    String.format("All players have taken their turn (%d/%d) ending the round", numTookTurn, numReady));
            onRoundEnd();
        }
    }

    // start check methods
    private void checkCurrentPlayer(long clientId) throws NotPlayersTurnException {
        if (currentTurnClientId != clientId) {
            throw new NotPlayersTurnException("You are not the current player");
        }
    }

    // end check methods

    // receive data from ServerThread (GameRoom specific)

    /**
     * Handles the turn action from the client.
     * 
     * @param currentUser
     * @param exampleText (arbitrary text from the client, can be used for
     *                    additional actions or information)
     */
    protected void handleTurnAction(ServerThread currentUser, String exampleText) {
        // check if the client is in the room
        try {
            checkPlayerInRoom(currentUser);
            checkCurrentPhase(currentUser, Phase.IN_PROGRESS);
            checkCurrentPlayer(currentUser.getClientId());
            checkIsReady(currentUser);
            if (currentUser.didTakeTurn()) {
                currentUser.sendMessage(Constants.DEFAULT_CLIENT_ID, "You have already taken your turn this round");
                return;
            }
            currentUser.setTookTurn(true);
            // TODO handle example text possibly or other turn related intention from client
            sendTurnStatus(currentUser, currentUser.didTakeTurn());
            // finished processing the turn
            onTurnEnd();
        } catch (NotPlayersTurnException e) {
            currentUser.sendMessage(Constants.DEFAULT_CLIENT_ID, "It's not your turn");
            LoggerUtil.INSTANCE.severe("handleTurnAction exception", e);
        } catch (NotReadyException e) {
            // The check method already informs the currentUser
            LoggerUtil.INSTANCE.severe("handleTurnAction exception", e);
        } catch (PlayerNotFoundException e) {
            currentUser.sendMessage(Constants.DEFAULT_CLIENT_ID, "You must be in a GameRoom to do the ready check");
            LoggerUtil.INSTANCE.severe("handleTurnAction exception", e);
        } catch (PhaseMismatchException e) {
            currentUser.sendMessage(Constants.DEFAULT_CLIENT_ID,
                    "You can only take a turn during the IN_PROGRESS phase");
            LoggerUtil.INSTANCE.severe("handleTurnAction exception", e);
        } catch (Exception e) {
            LoggerUtil.INSTANCE.severe("handleTurnAction exception", e);
        }
    }

    // handle DRAW action (sent from ServerThread)
    public void handleDraw(ServerThread drawer, int x, int y, TextFX.Color color){
        // TODO: gameroom handles draw action - change pixel in board to drawn pixel, then send result back to client and sync board
        try{
            //checks
            checkPlayerInRoom(drawer);
            checkCurrentPhase(drawer, Phase.IN_PROGRESS);
            checkCurrentPlayer(drawer.getClientId());
            checkIsReady(drawer);
            if (!drawer.isDrawer()){
                drawer.sendMessage(drawer.getClientId(), "You are not this round's drawer");
            }
            else if (!board.isValidCoordinate(x,y)){
                drawer.sendMessage(drawer.getClientId(), "Coordinates out of canvas bounds");

            }
            else{
                board.tryDraw(x, y, color); // try to draw drawer's request
                relay(null, TextFX.colorize(String.format("%s is drawing on (%d,%d)", drawer.getDisplayName(), x,y), Color.BLUE));
                sendCanvasUpdate(x,y,"black");
            }
            LoggerUtil.INSTANCE.info(TextFX.colorize("Canvas: " + board, Color.PURPLE));
        } catch (NotReadyException e){
            // The check method already informs the currentUser
            LoggerUtil.INSTANCE.severe("handleDraw exception", e);
        } catch (PlayerNotFoundException e) {
            drawer.sendMessage(Constants.DEFAULT_CLIENT_ID, "You must be in a GameRoom to play the game");
            LoggerUtil.INSTANCE.severe("handleDraw exception", e);
        } catch (PhaseMismatchException e) {
            drawer.sendMessage(Constants.DEFAULT_CLIENT_ID,
                    "You can only play during the IN_PROGRESS phase");
            LoggerUtil.INSTANCE.severe("handleDraw exception", e);
        } catch (Exception e) {
            LoggerUtil.INSTANCE.severe("handleDraw exception", e);
        }


    }
    // handle GUESS action (sent from ServerThread)
    public void handleGuess(ServerThread player, String guess){
        //TODO: gameroom handles guess action - compare guess to chosen word, evaluate points, send back to client and sync points
    }
    // end receive data from ServerThread (GameRoom specific)
}