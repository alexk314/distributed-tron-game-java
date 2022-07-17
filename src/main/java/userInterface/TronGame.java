package userInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import userInterface.enums.MovingDirection;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import edu.cads.bai5.vsp.tron.view.ITronView;
import edu.cads.bai5.vsp.tron.view.TronView;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import support.MyLog;
import userInterface.enums.PlayerPositionOnGrid;

public class TronGame extends Application {

    private ApplicationStubs applicationStubs;
    public final static String VIEW_CONFIG_FILE = "game.properties";

    private Stage stage;
    private ITronView gameGridView;
    private MovingDirection currentMovingDirectionPlayerLeft = MovingDirection.RIGHT;
    private MovingDirection currentMovingDirectionPlayerRight = MovingDirection.LEFT;
    private static final Logger LOGGER = MyLog.createLogger("Log GUI TronGame");
    public final static String GAME_CONFIG_FILE = "game.properties";
    private static boolean PERFORMANCESTRESSTEST;

    public TronGame() throws IOException {
        this.applicationStubs = new ApplicationStubs(this);

        // Get game properties to check if Performance test shall be executed:
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GAME_CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("Config File not found, TronGame not started");
            e.printStackTrace();
        }
        PERFORMANCESTRESSTEST = Boolean.parseBoolean(prop.getProperty("performanceStressTest"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initGameGridView() {
        try {
            gameGridView = new TronView(VIEW_CONFIG_FILE);
        } catch (IOException e) {
            LOGGER.error("Couldn't init gameGridView in TronGame.java");
            e.printStackTrace();
        }
        gameGridView.init(); // inits Tron Game View
        // configure and show stage
        stage.setTitle("TRON GAME");
        stage.setScene(gameGridView.getScene());
        this.stage.show();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        initGameGridView();

        if (PERFORMANCESTRESSTEST) { // Test can be activated in game.properties
            LOGGER.info("Performance stress test set, torture that node!");
            this.applicationStubs.hostGame("PerformanceTest", true);
        } else { // Only start gameMenu if Performance Test is not selected
            new TronGameMenu(applicationStubs, this);
        }

        // Keyboard Inputs verarbeiten:
        gameGridView.getScene().setOnKeyPressed(keyEvent -> {
            KeyCode key = keyEvent.getCode();
            MovingDirection movingDirectionHost = MovingDirection.convertKeyToMovingDirectionHost(key);
            MovingDirection movingDirectionClient = MovingDirection.convertKeyToMovingDirectionClient(key);

            // Set new MovingDirection upon keyInput of Player:
            if (movingDirectionHost != null) {
                this.currentMovingDirectionPlayerLeft = movingDirectionHost;
                LOGGER.info("Left player moved " + movingDirectionHost);
            }
            if (movingDirectionClient != null) {
                this.currentMovingDirectionPlayerRight = movingDirectionClient;
                LOGGER.info("Right player moved " + movingDirectionClient);
            }
        });
    }

    public void drawPlayerCoordinates(Coordinate playerLeftCoordinate, Coordinate playerRightCoordinate) {
        // Draw Library is just able to draw List<Coordinates>:
        List<Coordinate> playerLeftCoordinateAsList = new ArrayList<>();
        playerLeftCoordinateAsList.add(playerLeftCoordinate);
        List<Coordinate> playerRightCoordinateAsList = new ArrayList<>();
        playerRightCoordinateAsList.add(playerRightCoordinate);

        gameGridView.draw(playerLeftCoordinateAsList, PlayerPositionOnGrid.LEFT.getColorOfPlayer());
        gameGridView.draw(playerRightCoordinateAsList, PlayerPositionOnGrid.RIGHT.getColorOfPlayer());
    }

    public MovingDirection getCurrentDirection(PlayerPositionOnGrid player) {
        MovingDirection currentMovingDirection;
        if (player.equals(PlayerPositionOnGrid.LEFT)) {
            currentMovingDirection = this.currentMovingDirectionPlayerLeft;
        } else {
            currentMovingDirection = this.currentMovingDirectionPlayerRight;
        }
        LOGGER.info("Middleware requested current Direction. Returned " + currentMovingDirection.toString()
                + " to Middleware");
        return currentMovingDirection;
    }

    public void receivePlayerLost(boolean playerLeftLost, boolean playerRightLost) {
        LOGGER.info("Game Over. Player Left Lost: " + playerLeftLost + " and Player Right Lost: " + playerRightLost);
        if(!PERFORMANCESTRESSTEST) { // Winner menu will only be shown if performance stress test is not applied
            WinnerMenu wm = new WinnerMenu();
            wm.show(playerLeftLost, playerRightLost);
        } else {
            System.exit(0); // If Performance Stress test is applied application will close directly after game is over
        }
    }
}
