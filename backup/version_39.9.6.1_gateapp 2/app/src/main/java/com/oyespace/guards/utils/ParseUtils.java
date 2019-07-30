package com.oyespace.guards.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by kalyan pvs on 23-Sep-16.
 */

public class ParseUtils {

    private static Gson sGson = new Gson();

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static String tojson(Object object, String className) {
        try {
            String json = sGson.toJson(object);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logMessage(className, object.toString());
            Utils.logCrash(e);
        }
        return "";
    }

    public static <T> T fromJson(String object, Class<T> classs, String className) {
        try {
            T t = sGson.fromJson(object, classs);
            return t;
        } catch (Exception e) {
            Log.d("ParseUtils", classs.toString() + "" + object);
            e.printStackTrace();
            Utils.logMessage(className, object);
            Utils.logCrash(e);
        }
        return null;
    }

    public static <T> T fromJson(String object, Type classs, String className) {
        try {
            T t = sGson.fromJson(object, classs);
            return t;
        } catch (Exception e) {
            Log.d("ParseUtils", classs.toString() + "" + object);
            e.printStackTrace();
            Utils.logMessage(className, object);
            Utils.logCrash(e);
        }
        return null;
    }


    public static String toHtmljson(Object object, String className) {
        try {
            String json = gson.toJson(object);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logMessage(className, object.toString());
            Utils.logCrash(e);
        }
        return "";
    }

    public static <T> T fromHtmlJson(String object, Class<T> classs, String className) {
        try {
            T t = gson.fromJson(object, classs);
            return t;
        } catch (Exception e) {
            Log.d("ParseUtils", classs.toString() + "" + object);
            e.printStackTrace();
            Utils.logMessage(className, object);
            Utils.logCrash(e);
        }
        return null;
    }

    public static <T> T fromHtmlJson(String object, Type classs, String className) {
        try {
            T t = gson.fromJson(object, classs);
            return t;
        } catch (Exception e) {
            Log.d("ParseUtils", classs.toString() + "" + object);
            e.printStackTrace();
            Utils.logMessage(className, object);
            Utils.logCrash(e);
        }
        return null;
    }


}
