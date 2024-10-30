package com.projectshowdown.util;

import com.google.cloud.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class DateTimeUtils {

    /**
     * Converts LocalDateTime to Firebase Timestamp
     */
    public static Timestamp toFirebaseTimestamp(LocalDateTime localDateTime) {
        return Timestamp.of(Date.from(
                localDateTime.toInstant(ZoneOffset.systemDefault().getRules().getOffset(localDateTime))
        ));
    }

    /**
     * Converts Firebase Timestamp back to LocalDateTime
     */
    public static LocalDateTime fromFirebaseTimestamp(Timestamp firebaseTimestamp) {
        return LocalDateTime.ofInstant(
                firebaseTimestamp.toDate().toInstant(),
                ZoneOffset.systemDefault()
        );
    }

    /**
     * Checks if a Firebase Timestamp has expired compared to current time
     */
    public static boolean isExpired(Timestamp firebaseTimestamp) {
        LocalDateTime dateTime = fromFirebaseTimestamp(firebaseTimestamp);
        return dateTime.isBefore(LocalDateTime.now());
    }

}
