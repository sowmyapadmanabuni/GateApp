package com.oyespace.guards.realm;

import com.oyespace.guards.models.VisitorEntryFirebaseObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class VisitorEntryFirebaseObjectRealm {

    public static void updateFirebaseTime(int vLogId, @NotNull String time) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            VisitorEntryFirebaseObject log = realm.where(VisitorEntryFirebaseObject.class).equalTo("vlVisLgID", vLogId).findFirst();
            if (log == null) {
                log = new VisitorEntryFirebaseObject();
                log.setVlVisLgID(vLogId);
            }
            log.setVlUpdatedTime(time);
            realm.insertOrUpdate(log);
        });
    }

    public static void updateStatus(int vLogId, @NotNull String status) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            VisitorEntryFirebaseObject log = realm.where(VisitorEntryFirebaseObject.class).equalTo("vlVisLgID", vLogId).findFirst();
            if (log == null) {
                log = new VisitorEntryFirebaseObject();
                log.setVlVisLgID(vLogId);
            }
            log.setVlStatus(status);
            realm.insertOrUpdate(log);
        });
    }

    public static VisitorEntryFirebaseObject getVisitorFirebaseObject(int vLogId) {
        return Realm.getDefaultInstance().where(VisitorEntryFirebaseObject.class).equalTo("vlVisLgID", vLogId).findFirst();

    }

    public static void deleteEntry(int vlLogId) {
//        Realm.getDefaultInstance().executeTransaction(realm -> {
//
//            final RealmResults<VisitorEntryFirebaseObject> results = realm.where(VisitorEntryFirebaseObject.class)
//                    .equalTo("vlVisLgID", vlLogId)
//                    .findAll();
//            results.deleteAllFromRealm();
//        });
    }

    public static void deleteAllBeforeId(int vlLogId) {
        Realm.getDefaultInstance().executeTransaction(realm -> {

            final RealmResults<VisitorEntryFirebaseObject> results = realm.where(VisitorEntryFirebaseObject.class)
                    .lessThan("vlVisLgID", vlLogId)
                    .findAll();
            results.deleteAllFromRealm();
        });
    }

    public static void deleteAll() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.delete(VisitorEntryFirebaseObject.class));
    }

    public static ArrayList<VisitorEntryFirebaseObject> getAll() {
        return new ArrayList<>(Realm.getDefaultInstance().where(VisitorEntryFirebaseObject.class).findAll());
    }

}
