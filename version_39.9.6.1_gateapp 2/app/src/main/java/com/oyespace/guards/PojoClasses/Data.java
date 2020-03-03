
package com.oyespace.guards.PojoClasses;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Data {

    @SerializedName("visitorLatestRecord")
    private VisitorLatestRecord mVisitorLatestRecord;

    public VisitorLatestRecord getVisitorLatestRecord() {
        return mVisitorLatestRecord;
    }

    public void setVisitorLatestRecord(VisitorLatestRecord visitorLatestRecord) {
        mVisitorLatestRecord = visitorLatestRecord;
    }

}
