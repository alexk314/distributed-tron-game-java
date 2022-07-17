package GameController;

import edu.cads.bai5.vsp.tron.view.Coordinate;
import org.apache.log4j.Logger;
import support.MyLog;

import java.util.List;

public class PlayerCollisionService {

    private static final Logger LOGGER = MyLog.createLogger("Log PlayerCollisionService");

    public PlayerCollisionService() {

    }

    public boolean checkCollisionWithPlayer(List<Coordinate> player1Coords, List<Coordinate> player2Coords) {
        boolean player1Lost = false;
        Coordinate headPlayer1 = player1Coords.get(player1Coords.size()-1);
        List<Coordinate> tailPlayer1 = player1Coords.subList(1, player1Coords.size()-1);
        // First check if player 1 hit himself. Extract Head of Snake:
        if(tailPlayer1.contains(headPlayer1)){
            player1Lost = true;
            LOGGER.info("Player crashed into himself and lost. Players Head Coords: " + headPlayer1 + " Players Tail Coords: " + tailPlayer1);
        }
        // Second check if player 1's head hit the player 2's body:
        if(player2Coords.contains(headPlayer1)){
            player1Lost = true;
            LOGGER.info("Player1 crashed into other Player. Player 1s Head Coords: " + headPlayer1 + " Player 2s Coords: "+ player2Coords);
        }
        return player1Lost;
    }
}
