package com.oyespace.guards.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oyespace.guards.Myapp;
import com.oyespace.guards.utils.ParseUtils;
import com.oyespace.guards.utils.ProgressBarUtil;
import com.oyespace.guards.utils.Utils;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by kalyan pvs on 23-Sep-16.
 */

public class RestClient {

    private final static String LOGTAG = "RestClient";


    private static RestClient mRestClient;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private RequestQueue requestQueue;
    private HashMap<String, String> myApiHeaders;
    private ProgressBarUtil progressBarUtil;


    private RestClient() {
        requestQueue = Volley.newRequestQueue(Myapp.getContext());
        myApiHeaders = new HashMap<String, String>();
    }


    private RestClient(HashMap<String, String> apiHeaders) {
        requestQueue = Volley.newRequestQueue(Myapp.getContext());
        myApiHeaders = apiHeaders;
    }

    public static RestClient getInstance() {
        if (mRestClient == null) {
            mRestClient = new RestClient();
        }
        return mRestClient;
    }

    public static RestClient getInstance(HashMap<String, String> apiHeaders) {
        return new RestClient(apiHeaders);
    }

    public void addHeader(String key, String value) {
        myApiHeaders.put(key, value);
    }


    public void clearHeaders() {
        myApiHeaders.clear();
    }


    public <T> void post(Context ctx, Object reqObject, Type classType, ResponseHandler listener, URLData urlData) {
        try {
            JSONObject reqJsonObj = null;
            if (reqObject == null) {
                reqJsonObj = new JSONObject();
            } else {
                reqJsonObj = new JSONObject(ParseUtils.tojson(reqObject, urlData.getmUrl()));
            }
            makeCall(Request.Method.POST, ctx, reqJsonObj, classType, urlData, listener, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void post(Context ctx, Object reqObject, Type classType, ResponseHandler listener, URLData urlData, int position) {
        try {
            JSONObject reqJsonObj = new JSONObject(ParseUtils.tojson(reqObject, urlData.getmUrl()));
            makeCall(Request.Method.POST, ctx, reqJsonObj, classType, urlData, listener, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void get(Context ctx, Object reqObject, Type classType,
                        ResponseHandler listener, URLData urlData) {
        try {
            JSONObject reqJsonObj = new JSONObject(ParseUtils.tojson(reqObject, urlData.getmUrl()));
            makeCall(Request.Method.GET, ctx, reqJsonObj, classType, urlData, listener, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void get(Context ctx, Object reqObject, Type classType,
                        ResponseHandler listener, URLData urlData, int position) {
        try {
            JSONObject reqJsonObj = new JSONObject(ParseUtils.tojson(reqObject, urlData.getmUrl()));
            makeCall(Request.Method.GET, ctx, reqJsonObj, classType, urlData, listener, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void makeCall(int type, final Context ctx, final JSONObject reqUestObject, final Type classType, final URLData urlData, final ResponseHandler listener, final int position) {
        if (!Utils.isOnline(ctx)) {
            return;
        }

        if (urlData.isShowProgress()) {
            progressBarUtil = new ProgressBarUtil();
            progressBarUtil.showProgress(ctx, urlData.getProgressText());
        }
        String url = urlData.getmUrl();
        StringRequest stringRequest = new StringRequest(type, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOGTAG, urlData.getmUrl() + "  response:  " + response);
                if (urlData.isShowProgress() || (progressBarUtil != null && progressBarUtil.isShowing())) {
                    progressBarUtil.dismissProgress();
                }
                Object data = ParseUtils.fromHtmlJson(response, classType, LOGTAG);
                listener.onSuccess(response, data, urlData.getUrlId(), position);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGTAG, urlData.getmUrl() + " onErrorResponse: " + error);
                if (urlData.isShowProgress()) {
                    progressBarUtil.dismissProgress();
                }
                if (listener != null) {
                    listener.onFailure(error, urlData.getUrlId());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return myApiHeaders;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (reqUestObject != null) {
                    String requestBody = reqUestObject.toString();
                    Log.d(LOGTAG, "URL : " + urlData.getmUrl());
                    Log.d(LOGTAG, "Request body : " + requestBody);
                    Set<Map.Entry<String, String>> entries = myApiHeaders.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        Log.d(LOGTAG, "Headers : --> " + entry.getKey() + " : " + entry.getValue());
                    }
                    return requestBody.getBytes();
                }
                return super.getBody();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        requestQueue.add(stringRequest);

    }


}
