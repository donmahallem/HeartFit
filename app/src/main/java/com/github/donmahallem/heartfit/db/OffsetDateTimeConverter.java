package com.github.donmahallem.heartfit.db;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.room.TypeConverter;

public class OffsetDateTimeConverter {
    private final static  DateTimeFormatter mFormatter=DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    @TypeConverter
    public static OffsetDateTime toDate(String value) {
        if(value==null)
            return null;
        return OffsetDateTime.from(mFormatter.parse(value));
    }

    @TypeConverter
    public static String toLong(OffsetDateTime value) {
        if(value==null)
            return null;
        return value.format(mFormatter);
    }
}