package middleware.clientstubs.marshalling;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import edu.cads.bai5.vsp.tron.view.Coordinate;
import middleware.clientstubs.send.Sender;
import middleware.clientstubs.send.JsonEncapsulation;
import support.MyLog;
import userInterface.Connection;
import userInterface.enums.RPCMethodName;

public class RPCMarshaller {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log RPC Marshaller");

    /**
     * Erzeugt einen JSON für ein RPC.
     * 
     * @param source      als Connection
     * @param methodName  als RPCMethodName
     * @param param       als Object
     * @param disconnect  als boolean
     * @param destination als Connection
     */
    public static void invoke(Connection source, RPCMethodName methodName, Object param,
            boolean disconnect, Connection destination) {

        // Json erzeugen.
        LOGGER.info("Json erzuegen");
        LOGGER.info("Input Param: " + param);

        String sourceIP = source.getIp();
        int sourcePORT = source.getPort();

        // Serialize Param depending on MethodName:
        Object paramAsObject = serializeParam(methodName, param);

        JSONObject json = createJson(sourceIP, sourcePORT, methodName, paramAsObject, disconnect);
        LOGGER.info("Json: " + json.toJSONString());

        // Json -> byteArray.
        LOGGER.info("Json -> byteArray");
        byte[] myPackage = JsonEncapsulation.encapsulation(json);

        // Nachrticht an Server senden.
        LOGGER.info("Nachricht an Server senden.");
        try {

            if (methodName == RPCMethodName.REQUESTGAMELIST) {

                // Nachricht via UDP senden
                Sender.sendUDP(sourcePORT, destination.getIp(), destination.getPort(), myPackage);
            } else {

                // Nachricht via TCP senden.
                Sender.sendTCP(destination.getIp(), destination.getPort(), myPackage, disconnect);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hilfsmethode um die Parameter zu serialisieren.
     * 
     * @param methodName als RPCMethodName
     * @param inputParam als Object
     * @return ein Object.
     */
    private static Object serializeParam(RPCMethodName methodName, Object inputParam) {
        Object outputParam;
        switch (methodName) {
            case SENDCURRENTCOORDINATES:
                ArrayList<List<Integer>> paramAsListOfInt = toListListOfInt((ArrayList<Coordinate>) inputParam);
                LOGGER.info("paramAsString: " + paramAsListOfInt);
                outputParam = (Object) paramAsListOfInt;
                LOGGER.info("Param as Object" + outputParam);
                break;
            case SENDPLAYERLOST:
                ArrayList<Boolean> paramAsListOfBool = (ArrayList<Boolean>) inputParam;
                LOGGER.info("input param as Boolean" + inputParam);
                outputParam = (Object) paramAsListOfBool;
                break;
            default:
                outputParam = inputParam; // Special treatment is not required in default case
                break;
        }
        return outputParam;
    }

    /**
     * Erzeugt ein JSON und trägt alle als Parameter übergeben Werte ein.
     * 
     * @param sourceIP   als String
     * @param sourcePORT als int
     * @param methodName als RPCMethodName
     * @param parameter  als Object
     * @param disconnect als boolean
     * @return ein JSONObject
     */
    public static JSONObject createJson(String sourceIP, int sourcePORT, RPCMethodName methodName,
            Object parameter,
            boolean disconnect) {
        JSONObject json = new JSONObject();

        json.put("SOURCE_IP", sourceIP);
        json.put("SOURCE_PORT", sourcePORT);
        json.put("METHODE_NAME", methodName.name());
        json.put("PARAM", parameter);
        json.put("DISCONNECT", disconnect);

        return json;
    }

    private static ArrayList<List<Integer>> toListListOfInt(ArrayList<Coordinate> oldList) {

        // Neue result Liste
        ArrayList<List<Integer>> newList = new ArrayList<>();

        // create List of Lists
        for (Coordinate coordinate : oldList) {

            // create List of Integer
            List<Integer> singleCoordinateList = new ArrayList<>();

            singleCoordinateList.add(coordinate.x);
            singleCoordinateList.add(coordinate.y);
            newList.add(singleCoordinateList); // jetzt haben wir beide Koordinaten einer Koordinate
        }

        // Liste mit Coords in "neue Liste" eintragen
        return newList;
    }

}
