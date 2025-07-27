package com.example.application.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral('Z')
            .toFormatter();

    public static LocalDateTime stringToLocalDateTime(final String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static Date stringToDate(final String date) {
        return Date.from(stringToLocalDateTime(date).toInstant(ZoneOffset.UTC));
    }

    public static Date dateNowUTC() {
        return localDateTimeToDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
    }

    public static LocalDateTime localDateTimeNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }


    public static String format(final LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public static String nowFormatted() {
        return localDateTimeNowUTC().format(FORMATTER);
    }

    public static Long milliNowUTC() {
        return localDateTimeNowUTC().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static Date localDateTimeToDate(final LocalDateTime startDate) {
        return Date.from(startDate.toInstant(ZoneOffset.UTC));
    }

}
