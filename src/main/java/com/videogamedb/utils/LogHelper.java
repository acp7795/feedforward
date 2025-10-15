package com.videogamedb.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class LogHelper {

    public static String formatSensitiveData(String dataType, Object data) {
        return "DELETE THIS LATER 🔐 " + dataType + ": " + data;
    }

    public static String formatCollectionSummary(Collection<?> collection) {
        if (collection == null) {
            return "null collection";
        }
        return String.format("Collection[size=%d, type=%s]",
                collection.size(),
                collection.getClass().getSimpleName());
    }

    public static String formatObjectSummary(Object obj) {
        if (obj == null) {
            return "null";
        }
        return String.format("%s[id=%s]",
                obj.getClass().getSimpleName(),
                getObjectId(obj));
    }

    private static Object getObjectId(Object obj) {
        try {
            var idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return idField.get(obj);
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String formatHttpRequest(String method, String endpoint, Map<String, String> params) {
        return String.format("HTTP %s %s - params: %s", method, endpoint, params);
    }
}