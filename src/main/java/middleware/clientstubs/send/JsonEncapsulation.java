package middleware.clientstubs.send;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import support.MyLog;

public class JsonEncapsulation {

    // Logging
    private static final Logger LOGGER = MyLog.createLogger("Log JsonEncapsulation");

    // Lägnge des Headers.
    private static final int HEADER_LENGTH = 4;

    /**
     * Erzeugt ein Package für die Informationsübertrageung.
     * 
     * @param json als JSONObject
     * @return ein byte[]
     */
    public synchronized static byte[] encapsulation(JSONObject json) {

        // JSONObjekt in byte Array umwandeln.
        byte[] jsonBytes = json.toJSONString().getBytes(StandardCharsets.UTF_8);
        int jsonBytesLength = jsonBytes.length;

        // Package Größe ermitteln.
        Integer packageSize = HEADER_LENGTH + jsonBytesLength;

        // Logging
        LOGGER.info("packageSize: " + String.valueOf(packageSize));

        // Package als byte Array erzeugen: [{Header}{Payload}].
        byte[] packageArray = createPackage(jsonBytes, jsonBytesLength, packageSize);

        // Logging
        LOGGER.info("package: " + Arrays.toString(packageArray));

        // Package
        return packageArray;

    }

    /**
     * Erzeugt für jedes JSON ein eigenes Package: [Header | payload].
     * 
     * @param jsonBytes       als byte[]
     * @param jsonBytesLength als Integer
     * @param packageSize     als Integer
     * @return ein byte[]
     */
    private synchronized static byte[] createPackage(byte[] jsonBytes, Integer jsonBytesLength, Integer packageSize) {

        // Package erstellen.
        byte[] packageArray = new byte[packageSize];

        // Header: 4 Byte Integer in Big-Endian erstellen.
        byte[] header = ByteBuffer.allocate(HEADER_LENGTH).putInt(jsonBytesLength).array();

        // Header in Package übertragen.
        System.arraycopy(header, 0, packageArray, 0, header.length);

        // packageArray in Package übertragen.
        System.arraycopy(jsonBytes, 0, packageArray, header.length, jsonBytes.length);

        return packageArray;
    }
}
