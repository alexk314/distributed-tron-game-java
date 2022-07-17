package userInterface.enums;

import javafx.scene.paint.Color;

public enum PlayerPositionOnGrid {
    LEFT, RIGHT;

    public Color getColorOfPlayer(){
        switch (this) {
            case LEFT:
                return Color.DARKORCHID;
            case RIGHT:
                return Color.GREEN;
            default:
                return null;
        }
    }
}
