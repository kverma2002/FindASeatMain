package com.example.findaseat.Utils;
import java.util.HashMap;

public class Time {
    public static HashMap<Long, String> timeMap = new HashMap<>();

    static {
        for (int i = 0; i <= 47; i++) {
            int hours = i / 2;
            int minutes = (i % 2) * 30;
            String time = String.format("%02d:%02d %s", (hours == 0 || hours == 12) ? 12 : hours % 12, minutes, (hours < 12) ? "AM" : "PM");
            timeMap.put(Integer.toUnsignedLong(i), time);
        }
    }
}
