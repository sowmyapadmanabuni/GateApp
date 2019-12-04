package com.oyespace.guards.realm;

import com.oyespace.guards.models.FingerPrint;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

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
        realm.close();

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

}
