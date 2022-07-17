package support;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class MyLog {

    /**
     * Erstellt ein Logger. Der als Parameter übergebner String wird als Name für
     * den Logger sowie als Name der Log-Datei verwendet.
     * 
     * @param name als String
     * @return Logger mit FileAppender
     */
    public static Logger createLogger(String name) {

        // Logger erzeugen
        Logger logger = Logger.getLogger(name);

        // FileAppender hinzufügen
        logger.addAppender(createFileAppender(name));

        return logger;
    }

    /**
     * Logger konfigurieren.
     * 
     * @param name als String
     * @return ein konfigurierten FileAppender
     */
    private static FileAppender createFileAppender(String name) {

        // FileAppender erzeugen.
        FileAppender fileAppender = new FileAppender();

        // Schwelle auf Level INFO setzen.
        fileAppender.setThreshold(Level.INFO);

        // Muster für die Logausgabe erzeugen und setzen.
        // fileAppender.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        fileAppender.setLayout(new PatternLayout("%d [%p|%C{1}] %m%n"));

        // Log-Datei erzeugen:
        fileAppender.setFile(name + ".txt");

        // Appenderkonfiguration aktivieren.
        fileAppender.activateOptions();

        return fileAppender;
    };

}
