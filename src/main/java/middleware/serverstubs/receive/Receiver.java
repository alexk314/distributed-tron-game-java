package middleware.serverstubs.receive;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import support.MyLog;
import userInterface.ApplicationStubs;

public class Receiver {
    
    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log Receiver");

    /**
     * Der Server erzeugt einen Socket, einen Thread Pool und reicht sie an den
     * NetworkService weiter.
     * 
     * @param port des Servers als int.
     * @throws IOException
     */
    public static void start(int port,ApplicationStubs appstubs) throws IOException {

        // Socket erzeugen.
        LOGGER.info("Server erzeugen.");
        ServerSocket serverSocket = new ServerSocket(port);
        DatagramSocket datagramSocket = new DatagramSocket(port);
        
        // TheadPool erzeugen.
        LOGGER.info("ThreadPool erzeugen.");
        ExecutorService pool = Executors.newCachedThreadPool();

        // NetzwerkService als Thread starten.
        LOGGER.info("NetzwerkService als Thread starten.");
        Thread t1 = new Thread(new NetworkService(serverSocket, pool, appstubs));
        t1.start();

        // NetzwerkService als Thread starten.
        LOGGER.info("NetzwerkServiceUDP als Thread starten.");
        Thread t2 = new Thread(new NetworkServiceUDP(datagramSocket, appstubs));
        t2.start();
    }
}

