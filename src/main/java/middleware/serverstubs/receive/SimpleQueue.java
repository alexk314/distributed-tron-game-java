package middleware.serverstubs.receive;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import middleware.serverstubs.unmarshalling.RPCUnMarshaller;
import support.MyLog;
import userInterface.ApplicationStubs;

/**
 * Erzeugt eine nicht blockierende Queue, die von einem Worker-Thread
 * abgearbeitet wird.
 */
public class SimpleQueue {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log SimpleQueue");

    private Queue<byte[]> SIMPLE_QUEUE = new LinkedList<byte[]>();
    private Thread t1;
    RPCUnMarshaller rpcUnMarshaller = new RPCUnMarshaller();
    private ApplicationStubs APPSTUBS;

    public SimpleQueue(ApplicationStubs appstubs) {
        this.APPSTUBS = appstubs;
        createWorker();
    }

    /**
     * Erzeugt einen Thread zum Abarbeiten der Queue.Beendet sich, wenn die Queue
     * leer ist.
     */
    private void createWorker() {

        // Thread erzeugen
        this.t1 = new Thread() {

            @Override
            public void run() {
                LOGGER.info("Worker gestartet!");

                // Solange die Queue nicht leer ist, ...
                while (!isEmpty()) {

                    // Item (payload) von der Queue nehmen.
                    byte[] item = getItemFromSimpleQueue();
                    LOGGER.info("remove -> " + Arrays.toString(item));

                    JSONObject json;
                    try {

                        // Nachricht verarbeiten: byte-Array -> Json
                        json = JsonDecapsulation.decapsulation(item);

                        // RPC
                        rpcUnMarshaller.receive(json, APPSTUBS);
                    } catch (IOException | ParseException e) {

                        // Logging
                        LOGGER.warn("Worker Fehler: " + e.getMessage(), e);
                        e.printStackTrace();
                    }

                }
                LOGGER.info("Worker beendet!");

            }
        };
    }

    /**
     * Startet den Worker zum Abarbeiten der Queue.
     */
    private void startWorker() {
        if (!t1.isAlive()) {
            LOGGER.info("Worker starten ...");
            this.createWorker();
            this.t1.start();
        }
    }

    /**
     * Alle greifen über diese Methode auf die Queue zu.
     * 
     * @return die Queue als byte Array
     */
    private synchronized Queue<byte[]> getSIMPLE_QUEUE() {
        return this.SIMPLE_QUEUE;
    }

    /**
     * Payload in die Queue einfügen. 
     * 
     * @param payload als byte[]
     */
    public void addItemToSimpleQueue(byte[] payload) {
        this.getSIMPLE_QUEUE().add(payload);
        LOGGER.info("add -> " + Arrays.toString(payload));
        this.startWorker();
    }

    /**
     * Liefert das oberste Item (FIFO) und entfernt es aus der Queue.
     * 
     * @return ein Item als byte[]
     */
    private byte[] getItemFromSimpleQueue() {
        return this.getSIMPLE_QUEUE().remove();
    }

    /**
     * Liefert true wenn die Queue leer ist. Sonst false;
     * 
     * @return boolean
     */
    private boolean isEmpty() {
        return this.getSIMPLE_QUEUE().isEmpty();
    }

}
