package com.oyespace.guards.utils;

import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.oyespace.guards.PojoClasses.DashboardPojo;
import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.models.VisitorLog;
import com.oyespace.guards.models.Worker;
import com.oyespace.guards.pojo.*;
import com.oyespace.guards.responce.ResponseVisitorLog.Data.Visitorlogbydate;
import com.oyespace.guards.responce.VisitorLogExitResp;
import io.realm.Realm;
import io.realm.Sort;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static ArrayList<VisitorEntryLog> _getVisitorEnteredLog() {
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

    public static ArrayList<VisitorLog> getVisitorEnteredLog(){
        Realm realm = Realm.getDefaultInstance();
        ArrayList<VisitorLog> list = new ArrayList<>();
        list.addAll(realm.where(VisitorLog.class).findAll());
        realm.close();
        return list;

    }

    public static ArrayList<Worker> getStaffs(){
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Worker> list = new ArrayList<>();
        list.addAll(realm.where(Worker.class).findAll());
        realm.close();
        return list;

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

    public static ArrayList<Worker> getStaffs(Realm realm){
        ArrayList<Worker> list = new ArrayList<>();
        list.addAll(realm.where(Worker.class).sort("wklName", Sort.ASCENDING).findAll());
        return list;

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
}
