package middleware.serverstubs.unmarshalling;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import edu.cads.bai5.vsp.tron.view.Coordinate;
import support.MyLog;
import userInterface.ApplicationStubs;
import userInterface.Connection;
import userInterface.enums.RPCMethodName;

public class RPCUnMarshaller {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log RPC UnMarshaller");

    // Verbindung trennen?
    private boolean DISCONNECT = false;

    /**
     * Extrahiert aus einer als Parameter übergeben JSON die Information für einen Methodenaufruf.
     * 
     * @param json als JSONObject
     * @param appStubs als ApplicationStubs
     * @throws ParseException
     */
    public void receive(JSONObject json, ApplicationStubs appStubs) throws ParseException {

        // Keys aus Json auslesen:

        try {

            // Adresse auslesen.
            String sourceIP = (String) json.get("SOURCE_IP");
            Long sourcePORT = (Long) json.get("SOURCE_PORT");
            Connection srcConnestion = new Connection(sourceIP, sourcePORT);

            // Methode auslesen.
            String methodeNameString = (String) json.get("METHODE_NAME");
            RPCMethodName methodeName = RPCMethodName.valueOf(methodeNameString);

            // Deserialize param depending on MethodName:
            Object paramAsObject = deserializeParam(methodeName, json); //Object type is required in order to call App Stub with any type

            // Verbindungsanweisung auslesen.
            boolean disconnect = (boolean) json.get("DISCONNECT");
            this.setDisconnect(disconnect);

            LOGGER.info(String.format(
                    "SOURCE_IP: %s | SOURCE_PORT: %s | METHODE_NAME: %s | PARAM: %s | DISCONNECT: %s",
                    sourceIP, sourcePORT, methodeName, paramAsObject, disconnect));

            // Call local Method in Application Stubs:
            appStubs.call(srcConnestion, methodeName, paramAsObject);
        } catch (Exception e) {
            LOGGER.warn("Keys aus Json konnte nicht abgerufen werden! : " + e.getMessage(), e);
        }

    }

    private Object deserializeParam(RPCMethodName methodeName, JSONObject json) {
        Object outputParam;
        switch(methodeName){
            case SENDCURRENTCOORDINATES:
                ArrayList<List<Long>> paramAsListListOfLongs = (ArrayList<List<Long>>) json.get("PARAM");
                LOGGER.info("Deserialize param for DRAWPLAYERCOORDINATES: " + paramAsListListOfLongs);
                ArrayList<Coordinate> param = toListListOfCoords(paramAsListListOfLongs);
                LOGGER.info(String.format("Deserialized PARAMList of Coords: %s", param));
                // Cast param in order to have a generic method call in App Stubs:
                outputParam = (Object) param;
                break;
            case SENDPLAYERLOST:
                ArrayList<Boolean> paramAsListOfBool = (ArrayList<Boolean>) json.get("PARAM");
                LOGGER.info("Deserialize param for SENDPLAYERLOST: " + paramAsListOfBool);
                outputParam = (Object) paramAsListOfBool;
                break;
            default:
                outputParam = (Object) json.get("PARAM"); // Special treatment is not required in default case
                break;
        }
        return outputParam;
    }


    private ArrayList<Coordinate> toListListOfCoords(ArrayList<List<Long>> oldList) {

        // Neue Liste
        ArrayList<Coordinate> newList = new ArrayList<>();

        // create List of Coords:
        for (List<Long> playerCoordList : oldList) {
                Coordinate singleCoord = new Coordinate(playerCoordList.get(0).intValue(), playerCoordList.get(1).intValue());
                newList.add(singleCoord);
            }
        return newList;
    }

    public void setDisconnect(boolean disconnect) {
        this.DISCONNECT = disconnect;
    }

    public boolean getDisconnect() {
        return this.DISCONNECT;
    }
}
