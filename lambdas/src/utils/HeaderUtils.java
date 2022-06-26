package utils;

import java.util.HashMap;
import java.util.Map;

public class HeaderUtils {

    public static Map<String, String> getCorsHeader() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        return headers;
    }
}
