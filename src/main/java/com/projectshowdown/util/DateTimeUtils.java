package com.projectshowdown.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtils {

    /**
     * Converts LocalDateTime to epoch seconds
     */
    public static long toEpochSeconds(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * Converts epoch seconds to LocalDateTime
     */
    public static LocalDateTime fromEpochSeconds(long epochSeconds) {
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC);
    }

    /**
     * Checks if an epoch timestamp has expired compared to current time
     */
    public static boolean isExpired(long epochSeconds) {
        LocalDateTime dateTime = fromEpochSeconds(epochSeconds);
        return dateTime.isBefore(LocalDateTime.now());
    }
}