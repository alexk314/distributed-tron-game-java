package userInterface.enums;

import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.input.KeyCode;

public enum MovingDirection {
    UP, DOWN, LEFT, RIGHT, STRAIGHT, NODIRECTION;

    public static MovingDirection convertKeyToMovingDirectionClient(KeyCode key) {
        switch (key) {
            case UP:
                return MovingDirection.UP;
            case DOWN:
                return MovingDirection.DOWN;
            case LEFT:
                return MovingDirection.LEFT;
            case RIGHT:
                return MovingDirection.RIGHT;
            default:
                return null;
        }
    }

    public static MovingDirection convertKeyToMovingDirectionHost(KeyCode key) {
        switch (key) {
            case W:
                return MovingDirection.UP;
            case S:
                return MovingDirection.DOWN;
            case A:
                return MovingDirection.LEFT;
            case D:
                return MovingDirection.RIGHT;
            default:
                return null;

        }
    }

    public Coordinate getCoordinateDifference(){
        switch(this) {
            case UP:
                return new Coordinate(0,-1);
            case DOWN:
                return new Coordinate(0,1);
            case LEFT:
                return new Coordinate(-1,0);
            case RIGHT:
                return new Coordinate(1,0);
            default:
                return null;
        }
    }
}
