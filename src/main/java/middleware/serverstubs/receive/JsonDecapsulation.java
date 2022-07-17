package middleware.serverstubs.receive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import support.MyLog;

public class JsonDecapsulation {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log JsonDecapsulation");

    /**
     * Extrahiert aus einem als Parameter übergebenen Byte Array ein Json Objekte
     * und
     * gibt es zurück.
     * 
     * @param payload       als byte[]
     * @param payloadLength als int
     * @return JSONObject
     * @throws IOException
     * @throws org.json.simple.parser.ParseException
     */
    public synchronized static JSONObject decapsulation(byte[] payload)
            throws IOException, org.json.simple.parser.ParseException {

        // Payload als String in UTF8 interpretieren.
        String jsonAsString = new String(payload, StandardCharsets.UTF_8);

        // Json von String in JSONObject umwandeln.
        JSONObject jsonObject = JsonDecapsulation.stringToJson(jsonAsString);

        return jsonObject;
    }

    /**
     * JSON Objekt aus einem String erzeugen.
     * 
     * @param jsonString
     * @return ein JSONObject.
     * @throws org.json.simple.parser.ParseException
     */
    private synchronized static JSONObject stringToJson(String jsonString)
            throws org.json.simple.parser.ParseException {

        // Logging
        LOGGER.info(String.format("stringToJson: %s", jsonString));

        // Json Parser erzeugen.
        JSONParser jp = new JSONParser();

        // Json Objekt erzeugen.
        JSONObject jo = (JSONObject) jp.parse(jsonString);

        return jo;
    }

}
