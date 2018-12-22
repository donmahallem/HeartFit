package com.github.donmahallem.common.recycler.model;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;


public class OffsetDateTimeConverter {
    private final static  DateTimeFormatter mFormatter=DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static OffsetDateTime toDate(String value) {
        if(value==null)
            return null;
        return OffsetDateTime.from(mFormatter.parse(value));
    }

    public static String toLong(OffsetDateTime value) {
        if(value==null)
            return null;
        return value.format(mFormatter);
    }
}