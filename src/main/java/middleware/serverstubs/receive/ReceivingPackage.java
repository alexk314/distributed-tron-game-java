package middleware.serverstubs.receive;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import middleware.serverstubs.unmarshalling.RPCUnMarshaller;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import support.MyLog;
import userInterface.ApplicationStubs;

public class ReceivingPackage implements Runnable {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log ReceivingPackage");
    private final Socket SOCKET;
    private int payloadLength = 0;
    private static ReentrantLock MUTEX = new ReentrantLock();
    private static ApplicationStubs APPSTUBS;

    /**
     * Verarbeitet die erhaltenen Nachrichten aus dem als Parameter übergebenen
     * Socket.
     * 
     * @param socket
     */
    public ReceivingPackage(Socket socket, ApplicationStubs appStubs) {
        SOCKET = socket;
        APPSTUBS = appStubs;
    }

    @Override
    public void run() {

        // Nachricht empfangen und verarbeiten.
        byte[] payload = processMessage();
    }

    protected byte[] processMessage() {
        byte[] payload = null;
        try {

            // DataInputSream erzeugen.
            DataInputStream in = new DataInputStream(SOCKET.getInputStream());

            /**
             * solange der Socket nicht geschlossen ist und die Verbindung gehalten werden
             * soll.
             */
            RPCUnMarshaller rpcUnMarshaller = new RPCUnMarshaller();
            while (!SOCKET.isClosed() && !rpcUnMarshaller.getDisconnect()) {

                // Logging
                LOGGER.info("Nachricht wird verarbeitet...");

                // int lesen.
                payloadLength = in.readInt();
                LOGGER.info("Int: " + payloadLength);

                // Payload lesen.
                payload = new byte[payloadLength];
                in.readFully(payload, 0, payloadLength);

                // Logging
                LOGGER.info("payload: " + Arrays.toString(payload));

                // Nachricht verarbeiten: byte-Array -> Json
                JSONObject json = JsonDecapsulation.decapsulation(payload);

                // RPC
                rpcUnMarshaller.receive(json, APPSTUBS);

            }

            // DataInputStream schließen.
            in.close();

            // Logging
            LOGGER.info("InputStream beendet!");

        } catch (Exception e) {

            // Logging
            LOGGER.info("Server muss wissen, wann der Socket beedet werden soll!!!!");
            LOGGER.info("Stream von Gegenseite ohne Benachrichtung beendet!!!");
            LOGGER.warn("Nachticht konnte nicht empfangen werden! : " + e.getMessage(), e);
        } finally {
            try {

                // Ist der ServerSocket aktiv und Verbindung soll nicht halten werden, ...
                if (!SOCKET.isClosed()) {

                    // Logging
                    LOGGER.info("finally: Socket beenden.");

                    // Socket schliessen.
                    SOCKET.close();
                }
            } catch (Exception e) {

                // Logging
                LOGGER.warn("Socket konnte nicht beendet werden! : " + e.getMessage(), e);
            }
        }
        return payload;
    }

}