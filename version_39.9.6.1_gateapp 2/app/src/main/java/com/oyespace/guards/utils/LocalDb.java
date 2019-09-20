
package com.oyespace.guards.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;

import com.google.gson.reflect.TypeToken;
import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.pojo.Association;
import com.oyespace.guards.pojo.CheckPointByAssocID;
import com.oyespace.guards.pojo.SearchResult;
import com.oyespace.guards.pojo.UnitPojo;
import com.oyespace.guards.pojo.VisitorEntryLog;
import com.oyespace.guards.pojo.WorkerDetails;
import com.oyespace.guards.responce.ResponseVisitorLog.Data.Visitorlogbydate;
import com.oyespace.guards.responce.VisitorLogExitResp;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Kalyan on 4/29/2017.
 */

public class LocalDb {


    public static ArrayList<CheckPointByAssocID> getCheckPointList() {
        String cartData = Prefs.getString(PrefKeys.CheckPointList, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<CheckPointByAssocID>>() {
            }.getType();
            ArrayList<CheckPointByAssocID> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            return menuList;
        }
    }

    public static void saveCheckPointList(ArrayList<CheckPointByAssocID> menuItems) {
        String tojson;
        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.CheckPointList, tojson);
    }
    public static ArrayList<SearchResult> getRecentSearchData() {
        String cartData = Prefs.getString(PrefKeys.RECENT_SEARCH_DATA, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<SearchResult>>() {
            }.getType();
            ArrayList<SearchResult> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            return menuList;
        }
    }

    public static void saveSearchData(ArrayList<SearchResult> menuItems) {
        String tojson;
        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.RECENT_SEARCH_DATA, tojson);
    }

    public static SearchResult getSearchData() {
        String cartData = Prefs.getString(PrefKeys.SEARCH_DATA, null);
        if (cartData == null) {
            return null;
        } else {
            SearchResult menuList = ParseUtils.fromJson(cartData, SearchResult.class, "LocalDb");
            return menuList;
        }
    }

    public static void saveAssociation(Association menuItems) {
        String tojson;
        if (menuItems == null ) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.Association, tojson);
    }

    public static Association getAssociation() {
        String cartData = Prefs.getString(PrefKeys.Association, null);
        if (cartData == null) {
            return null;
        } else {
            Association menuList = ParseUtils.fromJson(cartData, Association.class, "LocalDb");
            return menuList;
        }
    }

    public static void saveEnteredVisitorLog(ArrayList<VisitorEntryLog> menuItems) {
        String tojson;
        //Log.d("SYCNCHECK","in 79"+menuItems.size());

        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.VisitorEnteredLogLocalDB, tojson);
    }

    public static ArrayList<VisitorEntryLog> getVisitorEnteredLog() {
        String cartData = Prefs.getString(PrefKeys.VisitorEnteredLogLocalDB, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<VisitorEntryLog>>() {
            }.getType();
            ArrayList<VisitorEntryLog> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            // Log.d("SYCNCHECK","in 99"+menuList.size());

            return menuList;
        }
    }

    public static void saveEnteredVisitorLog_old(ArrayList<VisitorLogExitResp.Data.VisitorLog> menuItems) {
        String tojson;
        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.VisitorEnteredLogLocalDBOLD, tojson);
    }

    public static ArrayList<Visitorlogbydate> getVisitorEnteredLog_old() {
        String cartData = Prefs.getString(PrefKeys.VisitorEnteredLogLocalDBOLD, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<Visitorlogbydate>>() {
            }.getType();
            ArrayList<Visitorlogbydate> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            return menuList;
        }
    }

    public static void saveAllVisitorLog(ArrayList<VisitorLogExitResp.Data.VisitorLog> menuItems) {
        String tojson;
        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.VisitorAllLogLocalDB, tojson);
    }

    public static ArrayList<Visitorlogbydate> getVisitorAllLog() {
        String cartData = Prefs.getString(PrefKeys.VisitorAllLogLocalDB, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<Visitorlogbydate>>() {
            }.getType();
            ArrayList<Visitorlogbydate> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            return menuList;
        }
    }

    public static ArrayList<WorkerDetails> getStaffList() {
        String cartData = Prefs.getString(PrefKeys.StaffList, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<WorkerDetails>>() {
            }.getType();
            ArrayList<WorkerDetails> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            return menuList;
        }
    }

    public static void saveStaffList(ArrayList<WorkerDetails> menuItems) {
        String tojson;
        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.StaffList, tojson);
    }

    public static ArrayList<UnitPojo> getUnitList() {
        String cartData = Prefs.getString(PrefKeys.UnitList, null);
        if (cartData == null) {
            return null;
        } else {
            Type type = new TypeToken<ArrayList<UnitPojo>>() {
            }.getType();
            ArrayList<UnitPojo> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
            return menuList;
        }
    }

    public static void saveUnitList(ArrayList<UnitPojo> menuItems) {
        String tojson;
        if (menuItems == null || menuItems.size() == 0) {
            // saveHotelId("");
            tojson = "";
        } else {
            tojson = ParseUtils.tojson(menuItems, "LocalDb");
        }
        Prefs.putString(PrefKeys.UnitList, tojson);
    }

    @SuppressLint("MissingPermission")
    public static void disconnectCall(Context context) {
        try {

            if (Build.VERSION.SDK_INT >= 28) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

                if (tm != null) {
                    boolean success = tm.endCall();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}