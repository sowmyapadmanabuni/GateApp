package com.oyespace.guards.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by kalyan pvs on 04-Oct-16.
 */

public class PrefKeys {

    public static final String CheckPointList = "CheckPointList";
    public static final String HOTEL_ID = "HOTEL_ID";
    public static final String HOTEL_DATA = "HOTEL_DATA";
    public static final String RECENT_SEARCH_DATA = "RECENT_SEARCH_DATA";
    public static final String SEARCH_DATA = "SEARCH_DATA";
    public static final String IS_LOGGEDIN = "IS_LOGGEDIN";
    public static final String LOGIN_DATA = "LOGIN DATA";
    public static final String PREPASSINGMOBILE = "MOBILE_DATA";
    public static final String PREPASSINGOTP = "OTP_DATA";
    public static final DateFormat dateFormat_YMD = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat dateFormat_YMD_hms = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final String DAILY_HELP = "Daily Help";
    public static final String Workers = "Workers";
    public static final String VisitorEnteredLogLocalDB = "VisitorEnteredLogLocalDB";
    public static final String VisitorEnteredLogLocalDBOLD = "VisitorEnteredLogLocalDBOLD";
    public static final String VisitorAllLogLocalDB = "VisitorAllLogLocalDB";
    public static final String StaffList = "StaffList";
    public static final String UnitList = "UnitList";

    public static final String REGULAR = "REGULAR";
    public static final String Association = "Association";
    public static final String IS_APP_INSTALL = "is_app_install";
    public static final String REGISTER = "register";
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_SUPERVISOR = 2;
    public static final int ROLE_RESIDENT = 3;
    public static final int ROLE_GUARD = 7;
    public static final int ROLE_NEWUSER = 5;
    public static final int ROLE_REJECTED = 6;
    public static final int ROLE_TENANT = 4;
    public static final int ROLE_FACILITY_MANAGER = 8;
    public static final int ROLE_SUB_MANAGER = 9;
    public static final String OTP_VALIDATE = "otp";
    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String MODEL_NUMBER = "MODEL_NUMBER";
    public static final String PATROLLING_ID = "PATROLLING_ID";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String EMERGENCY_SOUND_ON = "EMERGENCY_SOUND_ON";
    public static final String BG_NOTIFICATION_ON = "BG_NOTIFICATION_ON";


}
