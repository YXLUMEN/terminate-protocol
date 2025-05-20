package lumen.terminate_protocol.util;

import java.util.Map;
import java.util.Objects;

public class MapUtils {
    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
