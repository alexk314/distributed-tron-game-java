package middleware.clientstubs.send;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import support.MyLog;

public class Sender {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log Sender");
    private static Socket SOCKET = null;
    private static DataOutputStream OUT = null;

    /**
     * Senden via UDP Broadcast.
     *
     * @param sourcePort als int
     * @param ip         als String
     * @param senderPort als int
     * @param myPackage  als byte[]
     */
    public synchronized static void sendUDP(int sourcePort, String ip, int senderPort, byte[] myPackage) {
        LOGGER.info("UDP Socket erzeugen und Nachricht senden.");
        DatagramSocket udpSocket = null;
        try {
            // Sender UDP Socket mit server Port + 1 erstellen. Für Senden und Empfangen
            int clientPort = sourcePort + 1;
            /* Port könnte bereits verwendet werden, allerdings wird die Port Range nur für die Demonstration verwendet, um mehrer UIs auf einer Instanz anzuzeigen.
              Dies ist allerdings kein Use Case für die eigentliche Mehrspielerapplikation.
             */
            udpSocket = new DatagramSocket(clientPort);

            // Broadcast aktivieren.
            udpSocket.setBroadcast(true);

            // Nachricht erzeugen.
            InetAddress inetAddress = InetAddress.getByName(ip);
            int firstPort = 50000;
            for (int i = 0; i <= 1000; i++) {
                int potentialPort = firstPort + i; // inkrement from 50000-51000
                LOGGER.info("Broadcast to port: " + potentialPort);
                DatagramPacket datagramPacket = new DatagramPacket(myPackage, myPackage.length, inetAddress,
                        potentialPort);
                LOGGER.info(String.format("datagramPacket: %s", Arrays.toString(datagramPacket.getData())));

                // Nachricht senden.
                udpSocket.send(datagramPacket);
            }

        } catch (IOException e1) {
            LOGGER.warn("sendUDP: Nachricht konte nicht gesendet werden! -> " + e1.getMessage(), e1);
        } finally {
            // Socket schließen
            LOGGER.info("UDP Socket schließen");
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
        }
    }

    /**
     * Senden via TCP.
     *
     * @param ip         als String
     * @param port       als int
     * @param myPackage  als byte[]
     * @param disconnect als boolean
     * @throws InterruptedException
     */
    public synchronized static void sendTCP(String ip, int port, byte[] myPackage, boolean disconnect)
            throws InterruptedException {

        LOGGER.info("Socket erzeugen und Nachricht senden.");

        try {

            // wenn keine Verbindung besteht, ...
            if (!hasConnection()) {

                // Logging
                LOGGER.info(String.format("hasConnection: %s", String.valueOf(hasConnection())));

                // Socket erzeugen.
                createConnection(ip, port);
            } else {

                // Logging
                LOGGER.info(String.format("hasConnection: %s", String.valueOf(hasConnection())));
            }

            // Nachricht in Stream Legen.
            OUT.write(myPackage);

            // Logging
            // LOGGER.info(String.format("senden -> %s", Arrays.toString(myPackage)));

            // wird die Verbindung gehalten, ...
            if (!disconnect) {

                // Logging
                LOGGER.info("Nachricht senden via: flush()");

                // Nachricht senden ohne Stream zu beenden.
                OUT.flush();

            } else {

                // Logging
                LOGGER.info("Nachricht senden via: close()");

                // sonst Nachricht Senden und Stream zu beenden.
                OUT.close();
            }

        } catch (IOException e) {

            // Logging + Fehler
            LOGGER.warn("Socket konnte nicht erstellt werden! : " + e.getMessage(), e);

        } finally {
            try {

                // Ist der ServerSocket aktiv und Verbindung soll nicht halten werden, ...
                if (!SOCKET.isClosed() && disconnect) {

                    // Logging
                    LOGGER.info("finally: Socket beenden.");

                    // Socket schliessen.
                    SOCKET.close();
                }

                // Ist der Socket geschlossen, ...
                if (SOCKET.isClosed()) {

                    // Logging
                    LOGGER.info("Socket und OutputStream auf default = null gesetzt.");

                    // Socket und OutputStream auf default = null setzten.
                    resetConnection();
                }
            } catch (Exception e) {

                // Logging
                LOGGER.warn("Socket konnte nicht beendet werden! : " + e.getMessage(), e);
            }
        }
    }

    /**
     * Prüft auf vorhandene Verbindung vorhanden.
     *
     * @return true, wenn Verbindung vorhanden. Sonst false.
     */
    private synchronized static boolean hasConnection() {
        return SOCKET != null;
    }

    /**
     * Erzeugt einen Socket und einen OutputStream
     *
     * @param ip   als String
     * @param port als int
     * @throws UnknownHostException
     * @throws IOException
     */
    private synchronized static void createConnection(String ip, int port) throws UnknownHostException, IOException {

        // Socket erstellen.
        SOCKET = new Socket(ip, port);

        // Nagle's algorithm deaktivieren.
        SOCKET.setTcpNoDelay(true);

        // Outpout Stream erzeugen.
        OUT = new DataOutputStream(SOCKET.getOutputStream());
    }

    // Setzt den Socket und den OutputStream auf default = null.
    private synchronized static void resetConnection() {
        SOCKET = null;
        OUT = null;
    }
}
