package com.hb.concert.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CommonUtil {

    private static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
    private static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    public enum DateFormatType {
        DATE_TO_STRING_YYMMDD,
        DATE_TO_STRING_YYYYMMDD,
        DATE_TO_STRING_YYYYMMDDHHMMSS,
        STRING_TO_DATE_YYMMDD,
        STRING_TO_DATE_YYYYMMDD,
        STRING_TO_DATE_YYYYMMDDHHMMSS
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static String padRightZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder(inputString);
        while (sb.length() < length) {
            sb.append('0');
        }
        return sb.toString();
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNonNull(Object o) {
        return o != null;
    }

    public static boolean isListNull(List<?> list) {
        return list == null || list.size() == 0 || list.isEmpty();
    }

    public static String isNullToString(Object o) {
        return o == null ? "" : o.toString();
    }

    public static String dateToString(LocalDateTime date, DateFormatType type) {
        Date dateObj = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        return switch (type) {
            case DATE_TO_STRING_YYMMDD -> yyMMdd.format(dateObj);
            case DATE_TO_STRING_YYYYMMDD -> yyyyMMdd.format(dateObj);
            case DATE_TO_STRING_YYYYMMDDHHMMSS -> yyyyMMddHHmmss.format(dateObj);
            default -> date.toString();
        };
    }

    public static Date stringToDate(String date, DateFormatType type) throws ParseException {
        return switch (type) {
            case STRING_TO_DATE_YYMMDD -> yyMMdd.parse(date);
            case STRING_TO_DATE_YYYYMMDD -> yyyyMMdd.parse(date);
            case STRING_TO_DATE_YYYYMMDDHHMMSS -> yyyyMMddHHmmss.parse(date);
            default -> null;
        };
    }
}
