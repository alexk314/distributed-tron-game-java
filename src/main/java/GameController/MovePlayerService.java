package GameController;

import userInterface.enums.MovingDirection;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import org.apache.log4j.Logger;
import support.MyLog;

import java.util.List;

public class MovePlayerService {
    private static final Logger LOGGER = MyLog.createLogger("Log MovePlayerService");

    public MovePlayerService() {
    }

    public List<Coordinate> movePlayer(MovingDirection direction, List<Coordinate> playerCoordinates) {
        Coordinate currentHead = getHead(playerCoordinates);

        // Fehlerfall: Bei der Übertragung ist nicht rechtzeitig ein neuer Wert eingetroffen. Der Default Wert Straight bzw. No Direction muss behandelt werden:
        if (direction == MovingDirection.NODIRECTION || direction == MovingDirection.STRAIGHT) {
            // Sonderfall: Die Schlange ist zu kurz, um geradeaus zu gehen:
            if (playerCoordinates.size() < 2) {
                // Es kann sich nur um den Mitspieler handeln, der noch nicht gegangen ist. Es kann in diesem Fall die Richtung links angenommen werden:
                direction = MovingDirection.LEFT;
                LOGGER.info("Rare error case: Player didn't send new Moving Direction and snake was size < 2. Snake was moved left.");
            } else {
                LOGGER.info("Player didn't send new Moving Direction and was moved straight.");
                // Zur Korrektur wird angenommen, dass der Spieler in die gleiche Richtung weitergeht:
                Coordinate oldHead = getHead(playerCoordinates); // Head of nake
                Coordinate oldNeck = playerCoordinates.get(playerCoordinates.size() - 2); // 2nd last Coordinate after Head of snake
                int calculatedXMovement = oldHead.x - oldNeck.x;
                int calculatedYMovement = oldHead.y - oldNeck.y;
                Coordinate newHead = new Coordinate((oldHead.x + calculatedXMovement), (oldHead.y + calculatedYMovement));
                LOGGER.info("New Head after moving straight: " + newHead);

                // Add to Coordinate List and return:
                List<Coordinate> newCoordinateList = playerCoordinates;
                newCoordinateList.add(newHead);
                LOGGER.info("New Coordinate List after moving Straight: " + newCoordinateList);
                return newCoordinateList;
            }
        }

        // Normalfall: Snake moves in MovingDirection
        Coordinate newHead = currentHead.add(direction.getCoordinateDifference());
        // 180Grad Drehungen sind verboten. Der Spieler kann nicht direkt in seinen eigenen Körper fahren. Entsprechende Versuche werden abgefangen und es geht weiter in der bisherigen Richtung:
        // Fall kann auftreten, wenn mehr als 2 Elemente in der Liste sind (mindestens head und ein weiteres Element):
        if (playerCoordinates.size() >= 2) {
            Coordinate headOfTailPlayer = playerCoordinates.get(playerCoordinates.size() - 2);
            if (newHead.equals(headOfTailPlayer)) {
                LOGGER.info("Player tried forbidden move. He wanted to turn 180degrees into his own tail. Details:");
                LOGGER.info("Current head Coords: " + currentHead);
                LOGGER.info("new forbidden head Coords: " + newHead);
                LOGGER.info("Head of Tail: " + headOfTailPlayer);
                // Zur Korrektur wird die Bewegungsdifferenz in x- und y-Richtung negiert, um in die Richtung der Schlange zu gehen:
                int correctedXValue = -1 * (direction.getCoordinateDifference().x);
                int correctedYValue = -1 * (direction.getCoordinateDifference().y);
                newHead = currentHead.add(new Coordinate(correctedXValue, correctedYValue));
                LOGGER.info("Correct new head to Coords: " + newHead);
            }
        }
        List<Coordinate> newCoordinateList = playerCoordinates;
        newCoordinateList.add(newHead);

        return newCoordinateList;
    }

    private Coordinate getHead(List<Coordinate> playerCoordinates) {
        return playerCoordinates.get(playerCoordinates.size() - 1); // last element is head of snake
    }
}

