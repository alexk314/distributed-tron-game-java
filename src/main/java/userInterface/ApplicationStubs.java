package userInterface;

import GameController.CommunicationComponent;
import GameController.GameLoopService;
import userInterface.enums.MovingDirection;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import middleware.clientstubs.marshalling.RPCMarshaller;
import middleware.serverstubs.receive.Receiver;

import org.apache.log4j.Logger;

import support.MyLog;
import userInterface.enums.PlayerPositionOnGrid;
import userInterface.enums.RPCMethodName;

import java.io.IOException;
import java.util.ArrayList;

public class ApplicationStubs {
    // Instance variables for hosting a game:
    private boolean server = false; // default = false : Nur für Test = true.
    private String gameName; // Game name can be set by Host in GUI
    private boolean clientConnected = false; // besteht Verbindung zum Client? default = false.
    private boolean localGame;
    private GameLoopService gameLoopService;
    private CommunicationComponent communicationComponent; // Hinweis: Nur gesetzt, falls es sich um den Host handelt.
    // Connections:
    private Connection hostConnection;
    private Connection sourceConnection;
    private Connection clientConnection;
    // Game:
    private TronGameMenu tronGameMenu;
    private TronGame tronGameGui;
    private static final Logger LOGGER = MyLog.createLogger("Log Application Stubs");

    public void setCommunicationComponent(CommunicationComponent communicationComponent) {
        this.communicationComponent = communicationComponent;
    }

    public ApplicationStubs(TronGame guiReference) throws IOException {
        tronGameGui = guiReference;
    }

    /**
     * Hinweis zu den Methoden: Die Reihenfolge der Methoden entspricht dem logischen Ablauf eines Spielelebenszykluses vom hosten bis zum Verlieren eines Spieles.
     */

    // Server starten:
    public void startServer(Connection connectionFromUI) throws IOException {

        // Logger
        LOGGER.info(String.format("Server starten mit Connection: %s %d", connectionFromUI.getIp(),
                connectionFromUI.getPort()));

        // Server starten
        Receiver.start(connectionFromUI.getPort(), this);

        sourceConnection = connectionFromUI;
        LOGGER.info("Source Connection gespeichert!");
    }

    public void hostGame(String gameName, boolean localGame) {
        this.localGame = localGame;
        this.gameName = gameName;
        this.gameLoopService = new GameLoopService(this);
        if (localGame) {
            gameLoopService.start();
            LOGGER.info("LOCAL GameLoopService created and started");
        } else {
            this.server = true;
            LOGGER.info("Remote GameLoopService created.");
        }
    }

    /**
     *
     */
    public void requestGameList() {
        RPCMethodName methodName = RPCMethodName.REQUESTGAMELIST;
        Object param = null;
        boolean disconnect = true;
        Connection destinationConnection = new Connection("255.255.255.255", sourceConnection.getPort()); // Hinweis: destinationPort nicht benutzt
        RPCMarshaller.invoke(sourceConnection, methodName, param, disconnect, destinationConnection);
    }

    /**
     * Sendet Host-Game Information an den als Parameter übergebene
     * Client-Connection
     *
     * @param destinationConnection
     */
    private void sendGameInfoToClient(Connection destinationConnection) {
        RPCMethodName methodName = RPCMethodName.ADDGAMETOGAMELIST;
        String param = this.gameName; // Right player is being
        RPCMarshaller.invoke(sourceConnection, methodName, (Object) param, true, destinationConnection);
    }


    /**
     * @param hostIP
     * @param hostPort
     */
    public void joinGame(String hostIP, String hostPort) {
        Connection destinationConnection = new Connection(hostIP, hostPort);
        RPCMethodName methodName = RPCMethodName.JOINGAME;
        Object param = null;
        boolean disconnect = true;
        RPCMarshaller.invoke(sourceConnection, methodName, param, disconnect, destinationConnection);
    }

    public synchronized void call(Connection incomingConnection, RPCMethodName methodeName, Object param) {

        switch (methodeName) {
            // Methods for hosting/joining Games in order of occurrence:
            case REQUESTGAMELIST:
                LOGGER.info("Remote method call executed: " + RPCMethodName.REQUESTGAMELIST);
                // Ist Server und Client nicht Verbunden.
                if (this.server && !this.clientConnected) {
                    this.sendGameInfoToClient(incomingConnection);
                }
                break;
            case ADDGAMETOGAMELIST:
                LOGGER.info("Remote method call executed: " + RPCMethodName.ADDGAMETOGAMELIST);
                String receivedGameName = (String) param;
                tronGameMenu.addGameToGamelist(incomingConnection, receivedGameName);
                break;
            case JOINGAME:
                LOGGER.info("Remote method call executed: " + RPCMethodName.JOINGAME);
                // Ist Server und Client nicht verbunden.
                if (this.server && !this.clientConnected) {
                    // setzte "Server ist mit Client verbunden".
                    this.clientConnected = true;
                    // setzte "Client Verbindung".
                    this.clientConnection = incomingConnection;
                    LOGGER.info("Client Connection gespeichert!");
                    // HOST Nachricht an Client Senden, um Host zu Speichern!
                    RPCMarshaller.invoke(this.sourceConnection, RPCMethodName.SETCONNECTIONTOHOST, null, true,
                            incomingConnection);
                }
                break;
            case SETCONNECTIONTOHOST:
                LOGGER.info("Remote method call executed: " + RPCMethodName.SETCONNECTIONTOHOST);
                this.hostConnection = incomingConnection;
                LOGGER.info("Host Connection gespeichert, jetzt kenne ich den Host!");
                // Start Game:
                RPCMarshaller.invoke(this.sourceConnection, RPCMethodName.STARTGAME, null, false, incomingConnection);
                break;
            case STARTGAME:
                this.gameLoopService.start(); //Start a Game
                break;
            // Methods for Game Logic:
            case REQUESTCURRENTDIRECTION:
                sendCurrentDirection();
                LOGGER.info("Remote method call executed: " + RPCMethodName.REQUESTCURRENTDIRECTION);
                break;
            case SENDCURRENTDIRECTION:
                MovingDirection newMovingDirectionPlayerRight = MovingDirection.valueOf((String) param);
                setCurrentDirection(PlayerPositionOnGrid.RIGHT, newMovingDirectionPlayerRight);
                LOGGER.info("Remote method call executed: " + RPCMethodName.SENDCURRENTDIRECTION);
                break;
            case SENDCURRENTCOORDINATES:
                ArrayList<Coordinate> paramAsCoordinates = (ArrayList<Coordinate>) param;
                Coordinate playerLeftCoordinate = paramAsCoordinates.get(0);
                Coordinate playerRightCoordinate = paramAsCoordinates.get(1);
                tronGameGui.drawPlayerCoordinates(playerLeftCoordinate, playerRightCoordinate);
                LOGGER.info("Remote method call executed: " + RPCMethodName.SENDCURRENTCOORDINATES);
                break;
            case SENDPLAYERLOST:
                ArrayList<Boolean> paramAsBool = (ArrayList<Boolean>) param;
                Boolean playerLeftLost = paramAsBool.get(0);
                Boolean playerRightLost = paramAsBool.get(1);
                tronGameGui.receivePlayerLost(playerLeftLost, playerRightLost);
                LOGGER.info("Remote method call executed: " + RPCMethodName.SENDPLAYERLOST);
                break;
            default:
                LOGGER.info("Couldn't interpret method Name: " + methodeName);
                break;
        }
    }

    public void sendCurrentCoordinates(Coordinate playerLeftHeadCoordinate, Coordinate playerRightHeadCoordinate) {
        // Allways display Game on own GUI
         tronGameGui.drawPlayerCoordinates(playerLeftHeadCoordinate, playerRightHeadCoordinate);

        RPCMethodName methodName = RPCMethodName.SENDCURRENTCOORDINATES;

        ArrayList<Coordinate> param = new ArrayList<>();
        param.add(playerLeftHeadCoordinate);
        param.add(playerRightHeadCoordinate);
        boolean disconnectRequired = false;

        // Use Middleware if online Game is selected:
        if (!localGame) {
            RPCMarshaller.invoke(sourceConnection, methodName, (Object) param, disconnectRequired,
                    this.clientConnection);
        }
    }

    public void requestDirections() {
        // Left player is always local: Can be set directly on Communication Component
        MovingDirection playerLeft = tronGameGui.getCurrentDirection(PlayerPositionOnGrid.LEFT);
        setCurrentDirection(PlayerPositionOnGrid.LEFT, playerLeft);
        // Logic for local game: Right player is also directly connected:
        if (localGame) {
            MovingDirection playerRight = tronGameGui.getCurrentDirection(PlayerPositionOnGrid.RIGHT);
            setCurrentDirection(PlayerPositionOnGrid.RIGHT, playerRight);
        } else {
            // For remote Player invoke setCurrentDirection remotely:
            RPCMethodName methodName = RPCMethodName.REQUESTCURRENTDIRECTION;
            Object param = null;
            boolean disconnect = false;
            RPCMarshaller.invoke(sourceConnection, methodName, param, disconnect, clientConnection);
        }
    }

    private void sendCurrentDirection() {
        // Method for Sending the current direction after the request has been received.
        // Only happening on Clients App of remote game.
        RPCMethodName methodName = RPCMethodName.SENDCURRENTDIRECTION;
        String param = tronGameGui.getCurrentDirection(PlayerPositionOnGrid.RIGHT).toString(); // Right player is being
        // requested, because it's the only case where it needs to be send
        boolean disconnect = false;
        RPCMarshaller.invoke(sourceConnection, methodName, (Object) param, disconnect,
                this.hostConnection);
    }

    public void setCurrentDirection(PlayerPositionOnGrid playerposition, MovingDirection movingDirection) {
        // This request comes from remote Call:
        communicationComponent.setCurrentDirection(playerposition, movingDirection);
    }

    public void sendPlayerLost(boolean playerLeft, boolean playerRight) {
        RPCMethodName methodName = RPCMethodName.SENDPLAYERLOST;

        ArrayList<Boolean> param = new ArrayList<>();
        param.add(playerLeft);
        param.add(playerRight);
        boolean disconnectRequired = true;

        // Use Middleware if online Game is selected:
        if (!localGame) {
            RPCMarshaller.invoke(sourceConnection, methodName, (Object) param, disconnectRequired,
                    this.clientConnection);
        }
        tronGameGui.receivePlayerLost(playerLeft, playerRight);
    }
    public void setTronGameMenu(TronGameMenu tronGameMenu) {
        this.tronGameMenu = tronGameMenu;
    }

}
