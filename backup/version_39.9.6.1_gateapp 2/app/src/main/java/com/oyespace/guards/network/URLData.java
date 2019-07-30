package com.oyespace.guards.network;

import static com.oyespace.guards.utils.ConstantUtils.BASE_URL;

/**
 * Created by Hemanth A on 10/5/2016.
 */

public enum URLData {
    URL_SEND_OTP(BASE_URL+"oyeliving/api/v1/account/sendotp", false, true, "", 1),
    URL_VERIFY_OTP(BASE_URL+"oyeliving/api/v1/account/verifyotp", false, true, "", 2),
    URL_CHECK_GUARD_BY_MOBILE(BASE_URL+"oye247/api/v1/GetWorkersListByMobileNumber", false, true, "", 3),
    URL_SAVE_FINGERPRINT(BASE_URL+"oyesafe/api/v1/FingerPrint/Create", false, true, "", 4),
    URL_VISITOR_LOG(BASE_URL+"oyesafe/api/v1/VisitorLog/Create", false, true, "", 5),
    URL_VISITOR_MAKE_ENTRY(BASE_URL+"oyesafe/api/v1/VisitorEntryWIDAndTime/Update", false, true, "", 6),
    URL_VISITOR_MAKE_EXIT(BASE_URL+"oyesafe/api/v1/VisitorExitWIDAndTime/Update", false, true, "", 7),
    URL_CHEK_VEHICAL(BASE_URL+"oyeliving/api/v1/GetVisitorRoleByVehicleNumber",false,true,"",8),
    URL_PATROLLING_CREATE(BASE_URL+"oye247/api/v1/Patrolling/create",false,true,"",9),
    URL_PATROLLING_STOP(BASE_URL+"oye247/api/v1/Patrolling/PatrollingEndDateUpdate",false,true,"",10),
    URL_SAVE_CHECK_POINT(BASE_URL+"oye247/api/v1/Tracking/Create",false,true,"",11),
    URL_STAFF_REGISTRATION(BASE_URL+"oye247/api/v1/Worker/Create",false,true,"",12),
    URL_CREATE_TICKETING_RESPONSE(BASE_URL+"oye247/api/v1/TicketingResponse/Create",false,true,"",13);


//    URL_HOTELS_MENU_LIST_DETAILS("https://goohungrry.com/stack/v1/menuItems", false, true, "", 4),
//    URL_LOGIN("https://goohungrry.com/stack/v1/clogin", true, true, "", 5),
//    URL_SIGNUP("https://goohungrry.com/stack/v1/cregister", true, true, "", 6),
//    URL_HOTEL_LIST_NEARBY("https://goohungrry.com/stack/v1/nearby ", false, true, "", 7),
//    URL_ADDCART("https://goohungrry.com/stack/v1/additemtocart", true, true, "", 8),
//    URL_GET_ADDRESS("https://goohungrry.com/stack/v1/getaddress", true, true, "", 9),
//    URL_CHECKOUT("https://goohungrry.com/stack/v1/checkout", true, true, "", 10),
//    URL_ADD_ADDRESS("https://goohungrry.com/stack/v1/addaddress", true, true, "", 11),
//    URL_PAST_ORDER("https://goohungrry.com/stack/v1/getuserorders", true, true, "", 12),
//    URL_ADD_DEVICEID("https://goohungrry.com/stack/v1/adddeviceid", false, true, "", 13);


    private String mUrl;
    private boolean showProgress;
    private boolean showNoNetworkAlert;
    private String progressText;
    private int urlId;

    URLData(String url, boolean showProgress, boolean showNoNetworkAlert, String progressText, int urlId) {
        this.mUrl = url;
        this.showProgress = showProgress;
        this.showNoNetworkAlert = showNoNetworkAlert;
        this.progressText = progressText;
        this.urlId = urlId;
    }


    public String getmUrl() {
        return mUrl;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isShowNoNetworkAlert() {
        return showNoNetworkAlert;
    }

    public String getProgressText() {
        return progressText;
    }

    public int getUrlId() {
        return urlId;
    }

    @Override
    public String toString() {
        return mUrl;
    }
}
