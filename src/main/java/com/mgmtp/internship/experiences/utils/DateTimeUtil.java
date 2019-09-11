package com.mgmtp.internship.experiences.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static Timestamp getCurrentDate() {
       return Timestamp.valueOf(LocalDateTime.now());
    }
}
