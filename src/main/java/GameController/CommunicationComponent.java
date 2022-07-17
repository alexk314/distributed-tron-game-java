package GameController;

import userInterface.enums.MovingDirection;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import org.apache.log4j.Logger;
import support.MyLog;
import userInterface.ApplicationStubs;
import userInterface.enums.PlayerPositionOnGrid;

public class CommunicationComponent {
    ApplicationStubs applicationStubs;
    MovingDirection lastDirectionPlayerRight;
    MovingDirection lastDirectionPlayerLeft;
    private static final Logger LOGGER = MyLog.createLogger("Log CommunicationComponent");

    public CommunicationComponent(ApplicationStubs tronGame) {
        applicationStubs = tronGame;
        applicationStubs.setCommunicationComponent(this);
    }

    public void requestNewDirections() {
        // Reset Direction of players to default value. Necessary, if response of new direction is too slow and required to keep track of connection problems.
        lastDirectionPlayerRight = MovingDirection.STRAIGHT;
        lastDirectionPlayerLeft = MovingDirection.STRAIGHT;
        // Request new Directions from Middleware:
        applicationStubs.requestDirections();
        LOGGER.info("Moving Directions reseted and new Directions requested");
    }

    public void setCurrentDirection(PlayerPositionOnGrid playerPosition, MovingDirection currentDirection) {
        LOGGER.info(playerPosition + "'s Direction has been set with: " + currentDirection);
        if (playerPosition == PlayerPositionOnGrid.LEFT) {
            lastDirectionPlayerLeft = currentDirection;
        } else {
            lastDirectionPlayerRight = currentDirection;
        }
    }

    public MovingDirection getCurrentDirection(PlayerPositionOnGrid playerPosition, int tick) {
        // Returns current Direction of players to GameLoopService. These values are buffered here in this Object.
        if (playerPosition == PlayerPositionOnGrid.LEFT) {
            if (lastDirectionPlayerLeft == MovingDirection.STRAIGHT) {
                LOGGER.warn("!!!WARNING: Straight has been used for GameLoop at tick: " + tick + " for player: " + playerPosition);
            }
            return lastDirectionPlayerLeft;
        } else {
            if (lastDirectionPlayerRight == MovingDirection.STRAIGHT) {
                LOGGER.warn("!!!WARNING: Straight has been used for GameLoop at tick: " + tick + " for player: " + playerPosition);
            }
            return lastDirectionPlayerRight;
        }
    }

    public void sendNewCoordinates(Coordinate headPlayerLeft, Coordinate headPlayerRight) {
        applicationStubs.sendCurrentCoordinates(headPlayerLeft, headPlayerRight);
    }

    public void sendPlayerLost(boolean playerLeftLost, boolean playerRightLost) {
        applicationStubs.sendPlayerLost(playerLeftLost, playerRightLost);
    }
}
