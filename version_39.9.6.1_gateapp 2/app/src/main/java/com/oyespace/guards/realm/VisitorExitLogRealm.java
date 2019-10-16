package com.oyespace.guards.realm;

import android.util.Log;

import com.oyespace.guards.models.ExitVisitorLog;
import com.oyespace.guards.utils.DateTimeUtils;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;

public class VisitorExitLogRealm {

    public static void updateVisitorLogs(RealmList<ExitVisitorLog> visitorsList) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.delete(ExitVisitorLog.class);
        Log.i("taaag", "about to put " + visitorsList.size() + " objects in realm");
        visitorsList.sort((rhs, lhs) -> (DateTimeUtils.formatDateDMY(lhs.getVldUpdated()) + " " + (lhs.getVlExitT()).replace(
                "1900-01-01T",
                ""
        )).compareTo(
                DateTimeUtils.formatDateDMY(rhs.getVldUpdated()) + " " + (rhs.getVlExitT()).replace(
                        "1900-01-01T",
                        ""
                )
        ));
        realm.insertOrUpdate(visitorsList);
        realm.commitTransaction();
    }

    public static ArrayList<ExitVisitorLog> getVisitorExitLog() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<ExitVisitorLog> list = new ArrayList<>();
        list.addAll(realm
                .where(ExitVisitorLog.class)
                .findAll());
        realm.close();
        return list;
    }

    public static ArrayList<ExitVisitorLog> searchVisitorLog(String searchQuery) {

        if (searchQuery.isEmpty()) {
            return getVisitorExitLog();
        } else {

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


    public static void deleteVisitorLogs() {

        Realm r = Realm.getDefaultInstance();
        r.executeTransaction(realm -> realm.delete(ExitVisitorLog.class));

    }
}
