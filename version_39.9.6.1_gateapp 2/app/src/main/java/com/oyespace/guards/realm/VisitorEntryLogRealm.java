package com.oyespace.guards.realm;

import com.oyespace.guards.models.VisitorLog;
import com.oyespace.guards.utils.ConstantUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static void addVisitorEntries(ArrayList<VisitorLog> vlogs, VisitorEntryListener listener) {

        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
                    realm.insert(vlogs);
                }, () -> {
                    if (listener != null)
                        listener.onEntrySave(true);
                },
                (error) -> {
                    if (listener != null)
                        listener.onEntrySave(false);
                });

    }

//    public synchronized static void addVisitorEntries(
//            int vlLogid,
//            int assId,
//            int memID,
//            int regVisID,
//            int unitID,
//            String fName,
//            String mobile,
//            String compName,
//            String type,
//            String unitName,
//            int visCount,
//            String entryTime,
//            VisitorEntryListener listener) {
//
//        Log.i("taaag", "saving on realm " + Thread.currentThread().getName() + " thread");
//
//        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
//
//                    final VisitorLog vlog = realm.createObject(VisitorLog.class, vlLogid);
//                    vlog.setAsAssnID(assId);
//                    vlog.setMEMemID(memID);
//                    vlog.setReRgVisID(regVisID);
//                    vlog.setUNUnitID(unitID);
//                    vlog.setVlfName(fName);
//                    vlog.setVlMobile(mobile);
//                    vlog.setVlComName(compName);
//                    vlog.setVlVisType(type);
//                    vlog.setUnUniName(unitName);
//                    vlog.setVlVisCnt(visCount);
//                    vlog.setVlEntryT(entryTime);
//
//                },
//                () -> {
//                    if (listener != null)
//                        listener.onEntrySave(vlLogid, true);
//                },
//                (error) -> {
//                    if (listener != null)
//                        listener.onEntrySave(-1, false);
//                }
//        );
//
//    }

    public static VisitorLog getVisitorForId(int id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(VisitorLog.class)
                .equalTo("reRgVisID", id)
                .findFirst();

    }

    public static VisitorLog getVisitorForVisitorId(int id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(VisitorLog.class)
                .equalTo("vlVisLgID", id)
                .findFirst();

    }

    public static ArrayList<VisitorLog> getVisitorsForMobile(String phone) {

        Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realm.where(VisitorLog.class)
                .contains("vlMobile", phone)
                .findAll());

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
        realm.insertOrUpdate(visitorsList);
        realm.commitTransaction();
    }

    public static ArrayList<VisitorLog> searchVisitorLog(String searchQuery) {

        Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realm.where(VisitorLog.class)
                .contains("vlfName", searchQuery, Case.INSENSITIVE)
                .or()
                .contains("vlComName", searchQuery, Case.INSENSITIVE)
                .or()
                .contains("vlMobile", searchQuery)
                .or()
                .contains("vLPOfVis", searchQuery, Case.INSENSITIVE)
                .findAll());


    }

    public static boolean entryExists(String mobile) {

        Realm realm = Realm.getDefaultInstance();

        return realm.where(VisitorLog.class)
                .contains("vlMobile", mobile).count() != 0;

    }

    public static void deleteAllVisitorLogs() {
        Realm r = Realm.getDefaultInstance();

        r.executeTransaction(realm -> realm.delete(VisitorLog.class));

    }

    public static int getUnitCountForVisitor(String phone) {

        return (int) Realm.getDefaultInstance().where(VisitorLog.class).equalTo("vlMobile", phone).count();

    }

    @Nullable
    public static ArrayList<VisitorLog> getVisitorsForName(@NotNull String name) {
        Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realm.where(VisitorLog.class)
                .equalTo("vlfName", name)
                .findAll());
    }

    public static boolean staffEntryExists(@NotNull String phone) {
        Realm realm = Realm.getDefaultInstance();

        return realm.where(VisitorLog.class)
                .contains("vlMobile", phone)
                .and()
                .contains("vlVisType", ConstantUtils.STAFF, Case.INSENSITIVE)
                .count() != 0;
    }

    public interface VisitorEntryListener {
        void onEntrySave(boolean success);
    }

}
