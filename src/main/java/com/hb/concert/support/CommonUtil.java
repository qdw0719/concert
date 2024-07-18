package com.hb.concert.support;

import java.util.List;

public class CommonUtil {

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

    public static boolean isListNull(List list) {
        return list == null || list.size() == 0 || list.isEmpty();
    }

    public static String isNullToString(Object o) {
        return o == null ? "" : o.toString();
    }
}