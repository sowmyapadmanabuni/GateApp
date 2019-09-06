package com.oyespace.guards.utils;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.telecom.TelecomManager;
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


import java.lang.reflect.Method;
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

    public static void disconnectCall(Context context){
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();


        }
    }

}
