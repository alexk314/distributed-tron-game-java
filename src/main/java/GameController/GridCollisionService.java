package GameController;

import edu.cads.bai5.vsp.tron.view.Coordinate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GridCollisionService {

    public final static String VIEW_CONFIG_FILE = "game.properties";
    final int ROWS;
    final int COLUMNS;

    public GridCollisionService() {
        // Get border properties:
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(VIEW_CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("Config File not found, Grid Collision Service not started");
            e.printStackTrace();
        }
        this.ROWS = Integer.parseInt(prop.getProperty("rows"));
        this.COLUMNS = Integer.parseInt(prop.getProperty("columns"));
    }

    public boolean checkCollisionWithGrid(Coordinate headCoordinate) {
        boolean playerCollidedWithGrid = false;
        if (headCoordinate.x < 0 || headCoordinate.x >= COLUMNS) {
            playerCollidedWithGrid = true;
        }
        if (headCoordinate.y < 0 || headCoordinate.y >= ROWS) {
            playerCollidedWithGrid = true;
        }
        return playerCollidedWithGrid;
    }
}
