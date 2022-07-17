package middleware.serverstubs.receive;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import support.MyLog;
import userInterface.ApplicationStubs;

/**
 * Eingehende Nachrichten werden entgegengenommen und an die Klasse
 * ReceivingPackage übergeben und durch ein separaten Thread ausgeführt.
 */
public class NetworkService implements Runnable {
    
    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log NetworkService");
    private final ServerSocket SERVER_SOCKET;
    private final ExecutorService POOL;
    private ApplicationStubs APPSTUBS;

    /**
     * Konstruktor
     * 
     * @param serverSocket als Socket
     * @param pool         als ExecutorService
     * @param port         als int
     */
    public NetworkService(ServerSocket serverSocket, ExecutorService pool, ApplicationStubs appstubs) {
        SERVER_SOCKET = serverSocket;
        POOL = pool;
        APPSTUBS = appstubs;
    }

    @Override
    public void run() {
        try {

            // Dauerschleife.
            while (true) {

                /*
                 * Blockierend: Wartet auf das empfangen einer Nachricht. Wird eine Nachricht
                 * empfangen, wird ein Socket erzeugt und durch ein Thread aus dem ThreadPool
                 * bearbeitet.
                 */
                LOGGER.info("Warte auf Nachricht...");
                Socket socket = SERVER_SOCKET.accept();
                LOGGER.info("Nachricht empfangen!");

                // ReceivingPackage erzeugen, um die Nachricht zu verarbeiten.
                ReceivingPackage rp = new ReceivingPackage(socket, APPSTUBS);

                // ReceivingPackage einem Thread übergeben.
                POOL.execute(rp);

            }
        } catch (IOException e) {
            LOGGER.error("Nachricht konte nicht verarbeitet werden! : " + e.getMessage(), e);
        } finally {

            // ThreadPool schließen
            LOGGER.info("ThreadPool schließen");
            POOL.shutdown();
            try {

                // ThreadPool schließen, wenn nach 4 Sekunden noch Threads leben.
                LOGGER.info("ThreadPool schließen, wenn nach 4 Sekunden noch Threads leben.");
                POOL.awaitTermination(4L, TimeUnit.SECONDS);

                // ServerSocket beenden, wenn noch aktiv.
                if (!SERVER_SOCKET.isClosed()) {
                    LOGGER.info("ServerSocket beenden.");
                    SERVER_SOCKET.close();
                }
            } catch (InterruptedException | IOException e) {
                LOGGER.warn("ThreadPool oder Server konnte nicht beendet werden! : " + e.getMessage(), e);
            }
        }
    }
}
