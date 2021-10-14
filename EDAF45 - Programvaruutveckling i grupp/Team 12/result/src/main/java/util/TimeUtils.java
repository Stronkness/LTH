package util;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final String TIME_FORMAT = "HH:mm:ss";

    public static String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static String formatTime(Duration duration) {
        long s = duration.getSeconds();
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

}
