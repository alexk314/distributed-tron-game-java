package GameController;

import userInterface.enums.MovingDirection;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import org.apache.log4j.Logger;
import support.MyLog;
import userInterface.ApplicationStubs;
import userInterface.enums.PlayerPositionOnGrid;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameLoopService extends Thread {
    public final static String GAME_CONFIG_FILE = "game.properties";

    private int DELTATIMEPERTICK; // in ms
    private int tick = 0;
    private boolean playerLeftLost;
    private boolean playerRightLost;
    private CommunicationComponent communicationComponent;
    private GridCollisionService gridCollisionService;
    private PlayerCollisionService playerCollisionService;
    private MovePlayerService movePlayerService;

    private static final Logger LOGGER_PLAYER_LEFT = MyLog.createLogger("Log PlayerLeft");
    private static final Logger LOGGER_PLAYER_RIGHT = MyLog.createLogger("Log PlayerRight");

    // Player State:
    private static final PlayerPositionOnGrid playerLeft = PlayerPositionOnGrid.LEFT;
    private static final PlayerPositionOnGrid playerRight = PlayerPositionOnGrid.RIGHT;
    private List<Coordinate> playerLeftCoordinates = new ArrayList<>();
    private List<Coordinate> playerRightCoordinates = new ArrayList<>();

    public GameLoopService(ApplicationStubs appStub) {
        // Get game properties:
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GAME_CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("Config File not found, Grid Collision Service not started");
            e.printStackTrace();
        }
        this.DELTATIMEPERTICK = Integer.parseInt(prop.getProperty("gameSpeedInMs"));

        gridCollisionService = new GridCollisionService();
        playerCollisionService = new PlayerCollisionService();
        movePlayerService = new MovePlayerService();
        communicationComponent = new CommunicationComponent(appStub);
        // Initial Player State
        playerLeftCoordinates.add(new Coordinate(9, 19));
        LOGGER_PLAYER_LEFT.info("Initialized Player with coordinates: " + playerLeftCoordinates);
        playerRightCoordinates.add(new Coordinate(40, 19));
        LOGGER_PLAYER_RIGHT.info("Initialized Player with coordinates: " + playerRightCoordinates);
        playerLeftLost = false;
        playerRightLost = false;
    }


    public void run() {
        // Open starting: Send start positions of both players:
        communicationComponent.sendNewCoordinates(getHead(playerLeftCoordinates), getHead(playerRightCoordinates));

        while (!playerLeftLost && !playerRightLost) {
            communicationComponent.requestNewDirections(); //CommunicationComponent will have DELTATIMEPERTICK to request new MovingDirection from Players
            try { //Pause Game to make it fun
                Thread.sleep(DELTATIMEPERTICK);
            } catch (InterruptedException e) {
                System.err.println("Couldn't get GameLoop back from sleeping");
                e.printStackTrace();
            }
            // Get new Directions from Communication Component from both Players:
            MovingDirection currentDirectionPlayerLeft = communicationComponent.getCurrentDirection(playerLeft, tick);

            MovingDirection currentDirectionPlayerRight = communicationComponent.getCurrentDirection(playerRight, tick);
            // Move Players:
            playerLeftCoordinates = movePlayerService.movePlayer(currentDirectionPlayerLeft, playerLeftCoordinates);
            LOGGER_PLAYER_LEFT.info("Moved Player, new Coordinates : " + playerLeftCoordinates);
            playerRightCoordinates = movePlayerService.movePlayer(currentDirectionPlayerRight, playerRightCoordinates);
            LOGGER_PLAYER_RIGHT.info("Moved Player, new Coordinates : " + playerRightCoordinates);
            // Check collision with Grid from both players:
            Coordinate headPlayerLeft = getHead(playerLeftCoordinates);
            playerLeftLost = gridCollisionService.checkCollisionWithGrid(headPlayerLeft);
            Coordinate headPlayerRight = getHead(playerRightCoordinates);
            playerRightLost = gridCollisionService.checkCollisionWithGrid(headPlayerRight);
            //Return if somebody lost and send notice to GameView to show Winner Menu:
            if (playerRightLost || playerLeftLost) {
                gameOver();
                return;
            }
            // Check collision between Players with PlayerCollisionService:
            playerLeftLost = playerCollisionService.checkCollisionWithPlayer(playerLeftCoordinates, playerRightCoordinates);
            playerRightLost = playerCollisionService.checkCollisionWithPlayer(playerRightCoordinates, playerLeftCoordinates);
            //Return if one player crashed another:
            if (playerRightLost || playerLeftLost) {
                gameOver();
                return;
            }
            // Send Communication Component new Coordinates of both players:
            communicationComponent.sendNewCoordinates(headPlayerLeft, headPlayerRight);
            // Increment tick by DeltaTimePerTick:
            tick++;
        }
    }

    private void gameOver() {
        //Log Reason for breaking Game Loop:
        int gameDuration = (tick * DELTATIMEPERTICK) / 1000; // in seconds
        LOGGER_PLAYER_LEFT.info("Game ended after " + gameDuration + " seconds at tick: " + tick);
        LOGGER_PLAYER_RIGHT.info("Game ended after " + gameDuration + " seconds at tick: " + tick);
        LOGGER_PLAYER_LEFT.info("Player lost: " + playerLeftLost);
        LOGGER_PLAYER_RIGHT.info("Player lost: " + playerRightLost);

        // Inform CommunicationComponent about the losers of the game:
        communicationComponent.sendPlayerLost(playerLeftLost, playerRightLost);
    }

    private Coordinate getHead(List<Coordinate> playerCoordinates) {
        return playerCoordinates.get(playerCoordinates.size() - 1); // last element is head of snake
    }
}
