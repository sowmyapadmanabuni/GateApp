package com.oyespace.guards.realm;

import android.util.Log;

import com.oyespace.guards.models.ExitVisitorLog;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

public class VisitorExitLogRealm {

    public static void addVisitorLogs(RealmList<ExitVisitorLog> visitorsList) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        for (ExitVisitorLog v : visitorsList) {
            Log.i("taaag", "about to put in realm -> " + v.getVlVisLgID());
        }
        realm.insertOrUpdate(visitorsList);
        realm.commitTransaction();
    }

    public static ArrayList<ExitVisitorLog> getVisitorExitLog() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<ExitVisitorLog> list = new ArrayList<>();
        list.addAll(realm
                .where(ExitVisitorLog.class)
                .findAll().sort("vlVisLgID", Sort.DESCENDING));
        realm.close();
        return list;
    }

    public static ArrayList<ExitVisitorLog> searchVisitorLog(String searchQuery) {

        Realm realm = Realm.getDefaultInstance();
        ArrayList<ExitVisitorLog> results = new ArrayList<>();
        results.addAll(realm.where(ExitVisitorLog.class)
                .contains("vlfName", searchQuery, Case.INSENSITIVE)
                .or()
                .contains("vlComName", searchQuery, Case.INSENSITIVE)
                .or()
                .contains("vlMobile", searchQuery)
                .or()
                .contains("vLPOfVis", searchQuery, Case.INSENSITIVE)
                .findAll());
        return results;

    }


}
