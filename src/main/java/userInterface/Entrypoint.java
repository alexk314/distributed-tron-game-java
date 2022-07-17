package userInterface;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Annoying workaround! See
 * https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
 */
public class Entrypoint {

    private Entrypoint() {
    }

    public static void main(String[] args) {

        // Menu design anpassen (Wichtig: nicht verändern!)
        FlatDarkLaf.setup();

        TronGame.main(args);
    }
}