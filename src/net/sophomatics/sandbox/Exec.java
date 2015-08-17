package net.sophomatics.sandbox;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 */
public class Exec {
    public static void main(String[] args) {
        Map<String, Double> map = new HashMap<>();
        map.put("Hallo", 3d);

        Double value;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            value = entry.getValue();
            value *= 2;
        }

        System.out.println(map.get("Hallo"));

    }
}
