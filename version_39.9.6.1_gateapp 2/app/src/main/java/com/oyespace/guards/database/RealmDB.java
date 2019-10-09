package com.oyespace.guards.database;

import android.util.Log;

import com.oyespace.guards.models.FingerPrint;
import com.oyespace.guards.models.VisitorLog;
import com.oyespace.guards.models.Worker;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmDB {
    public static RealmResults<FingerPrint> getRegularVisitorsFingerPrint(int AssociationID) {

        Realm realm = Realm.getDefaultInstance();
        ArrayList<FingerPrint> fingerPrints = new ArrayList<>();
        return realm.
                where(FingerPrint.class)
                .equalTo("ASAssnID", AssociationID)
                .findAll();
//        return fingerPrints;
    }

    public static ArrayList<VisitorLog> getVisitorEnteredLog() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<VisitorLog> list = new ArrayList<>();
        list.addAll(realm.where(VisitorLog.class).findAll().sort("vlVisLgID", Sort.DESCENDING));
        realm.close();
        return list;
    }

    public static VisitorLog getVisitorForId(int id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(VisitorLog.class)
                .equalTo("reRgVisID", id)
                .findFirst();

    }

    public static ArrayList<Worker> getStaffs() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Worker> list = new ArrayList<>();
        list.addAll(realm.where(Worker.class).findAll());
        realm.close();
        return list;

    }

    public static Worker getStaffForId(int id) {


        Realm realm = Realm.getDefaultInstance();

        return realm.where(Worker.class)
                .equalTo("wkWorkID", id)
                .findFirst();


    }

    public static void insertFingerPrints(int fpId, String uname, String finger_type, byte[] photo1, byte[] photo2, byte[] photo3, String MemberType, int aid) {
        boolean commitNow = false;

        int _uname = Integer.parseInt(uname);
        Realm realm = Realm.getDefaultInstance();


        FingerPrint existing = realm.where(FingerPrint.class).equalTo("userName", uname).equalTo("FPFngName", finger_type).findFirst();
        if (existing == null) {
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
                commitNow = true;
            }
            FingerPrint fingerPrint = realm.createObject(FingerPrint.class, fpId);
            fingerPrint.setFMID(_uname);
            fingerPrint.setUserName(uname);
            fingerPrint.setFPFngName(finger_type);
            fingerPrint.setFPImg1(photo1);
            fingerPrint.setFPImg2(photo2);
            fingerPrint.setFPImg3(photo3);
            fingerPrint.setFPMemType(MemberType);
            fingerPrint.setASAssnID(aid);
            if (commitNow) {
                realm.commitTransaction();
            }
        } else {
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
                commitNow = true;
            }
            existing.setFMID(_uname);
            existing.setUserName(uname);
            existing.setFPFngName(finger_type);
            existing.setFPImg1(photo1);
            existing.setFPImg2(photo2);
            existing.setFPImg3(photo3);
            existing.setFPMemType(MemberType);
            existing.setASAssnID(aid);
            if (commitNow) {
                realm.commitTransaction();
            }
        }

    }

    public static int fingercount(int MemberID) {

        Realm realm = Realm.getDefaultInstance();

        return (int) realm.where(FingerPrint.class)
                .equalTo("userName", String.valueOf(MemberID))
                .count();

    }

    public static boolean getMemberFingerExists(String username, String FingerName) {

        Realm realm = Realm.getDefaultInstance();

        return realm.where(FingerPrint.class)
                .equalTo("userName", username)
                .and()
                .equalTo("FPFngName", FingerName)
                .findFirst() != null;

    }

    public static int getTotalFingerPrints() {
        int available = 0;
        Realm realm = Realm.getDefaultInstance();
        long avail = realm.where(FingerPrint.class).count();
        available = (int) avail;
        return available;
    }

    public static void saveStaffsList(RealmList<Worker> arrayList) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.copyToRealmOrUpdate(arrayList);
        realm.commitTransaction();
        realm.close();
    }

    public static void saveVisitor(VisitorLog visitorLog) {

        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.insertOrUpdate(visitorLog);
        realm.commitTransaction();

    }

    public static void saveVisitors(RealmList<VisitorLog> visitorsList) {
        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        for (VisitorLog v : visitorsList) {
            Log.i("taaag", "about to put in realm -> " + v.getVlVisLgID());
        }
        realm.insertOrUpdate(visitorsList);
        realm.commitTransaction();
    }

    public static ArrayList<VisitorLog> searchVisitorLog(String searchQuery) {

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

    public static boolean entryExists(String mobile) {

        Realm realm = Realm.getDefaultInstance();

        return realm.where(VisitorLog.class)
                .equalTo("vlMobile", mobile)
                .findFirst() != null;

    }

}
