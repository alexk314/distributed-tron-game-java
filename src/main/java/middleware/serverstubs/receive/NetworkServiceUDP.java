package middleware.serverstubs.receive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;

import middleware.serverstubs.unmarshalling.RPCUnMarshaller;
import support.MyLog;
import userInterface.ApplicationStubs;

/**
 * Eingehende Nachrichten werden entgegengenommen und an die Klasse
 * ReceivingPackage übergeben und durch ein separaten Thread ausgeführt.
 */
public class NetworkServiceUDP implements Runnable {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log NetworkServiceUDP");
    private final DatagramSocket UDP_SOCKET;
    private ApplicationStubs APPSTUBS;
    private static final int READ_INT = 4;

    /**
     * Konstruktor
     * 
     * @param serverSocket als Socket
     * @param pool         als ExecutorService
     * @param port         als int
     */
    public NetworkServiceUDP(DatagramSocket udpSocket, ApplicationStubs appstubs) {
        UDP_SOCKET = udpSocket;
        APPSTUBS = appstubs;
    }

    @Override
    public void run() {
        try {

            RPCUnMarshaller rpcUnMarshaller = new RPCUnMarshaller();

            // Dauerschleife.
            while (true) {

                // Package für Integer erzeugen.
                byte[] buf_int = new byte[READ_INT];
                DatagramPacket packet_int = new DatagramPacket(buf_int, buf_int.length);

                /*
                 * Blockierend: Wartet auf das empfangen einer Nachricht. Wird eine Nachricht
                 * empfangen, wird ein die Nachricht in eine Liste eingetragen und durch ein
                 * Thread bearbeitet.
                 */
                LOGGER.info("Warte auf Nachricht...");
                UDP_SOCKET.receive(packet_int);
                LOGGER.info("Nachricht empfangen!");

                // Intager auslesen
                ByteBuffer wrapped = ByteBuffer.wrap(packet_int.getData());
                int payloadLength = wrapped.getInt();
                LOGGER.info("Package Length: " + payloadLength);

                // Package für Nachricht erzeugen.
                byte[] buf_message = new byte[READ_INT + payloadLength];
                DatagramPacket packet_message = new DatagramPacket(buf_message, buf_message.length);

                // Nachricht enpfangen
                UDP_SOCKET.receive(packet_message);
                LOGGER.info(String.format("packet_message: %s", Arrays.toString(packet_message.getData())));

                // Nachricht extrahieren
                byte[] headerPlusPayload = packet_message.getData();
                byte[] payload = new byte[payloadLength];
                System.arraycopy(headerPlusPayload, READ_INT, payload, 0, payloadLength);
                // LOGGER.info(String.format("payload: %s", Arrays.toString(payload)));

                // payload in Queue eintragen.
                SimpleQueue simpleQueue = new SimpleQueue(this.APPSTUBS);
                simpleQueue.addItemToSimpleQueue(payload);

            }
        } catch (IOException e) {

            LOGGER.error("Nachricht konte nicht verarbeitet werden! : " + e.getMessage(), e);

        } finally {

            // Socket schließen
            LOGGER.info("Socket schließen");
            if (!UDP_SOCKET.isClosed()) {
                UDP_SOCKET.close();
            }
        }
    }
}
