package com.oyespace.guards.realm;

import android.util.Log;

import com.oyespace.guards.models.VisitorLog;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class VisitorEntryLogRealm {


    public static ArrayList<VisitorLog> getVisitorEntryLog() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<VisitorLog> list = new ArrayList<>();
        list.addAll(realm
                .where(VisitorLog.class)
                .findAll().sort("vlVisLgID", Sort.DESCENDING));
        realm.close();
        return list;
    }

    public static VisitorLog getVisitorForId(int id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(VisitorLog.class)
                .equalTo("reRgVisID", id)
                .findFirst();

    }

    public static void deleteVisitor(int visitorId) {

        Realm rm = Realm.getDefaultInstance();
        rm.executeTransaction(realm -> {

            final RealmResults<VisitorLog> results = realm.where(VisitorLog.class)
                    .equalTo("vlVisLgID", visitorId)
                    .findAll();
            results.deleteAllFromRealm();

        });


    }

    public static void updateVisitorLogs(RealmList<VisitorLog> visitorsList) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.delete(VisitorLog.class);
        for (VisitorLog v : visitorsList) {
            Log.i("taaag", "about to put in realm -> " + v.getVlVisLgID());
        }
        realm.insertOrUpdate(visitorsList);
        realm.commitTransaction();
    }

    public static ArrayList<VisitorLog> searchVisitorLog(String searchQuery) {

        if (searchQuery.isEmpty()) {
            return getVisitorEntryLog();
        } else {
            Realm realm = Realm.getDefaultInstance();
            ArrayList<VisitorLog> results = new ArrayList<>();
            results.addAll(realm.where(VisitorLog.class)
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

    public static boolean entryExists(String mobile) {

        Realm realm = Realm.getDefaultInstance();

        return realm.where(VisitorLog.class)
                .equalTo("vlMobile", mobile).count() != 0;

    }

    public static void deleteAllVisitorLogs() {
        Realm r = Realm.getDefaultInstance();

        r.executeTransaction(realm -> realm.delete(VisitorLog.class));

    }
}
