package collinvht.projectr.util;

import com.google.gson.JsonParser;

import java.io.FileReader;

public class JSONUtil {
    public static Object readJson(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(reader);
    }
}
