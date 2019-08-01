package com.oyespace.guards.utils;

import java.text.NumberFormat;

/**
 * Created by kalyan pvs on 07-Nov-16.
 */

public class NumberUtils {

    static NumberFormat numberFormat = NumberFormat.getInstance();

    public static float toFloat(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return Float.parseFloat(string);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return 0f;
    }

    public static long toLong(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return Long.parseLong(string);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }

    public static double toDouble(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }
        return 0f;
    }

    public static int toInteger(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.intValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return Integer.parseInt(string);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }
}
