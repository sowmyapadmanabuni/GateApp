package com.oyespace.guards.realm;

import com.oyespace.guards.models.Worker;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

public class StaffRealm {
    public static ArrayList<Worker> getStaff() {
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

    public static boolean staffForPhoneExists(String phone) {

        Realm realm = Realm.getDefaultInstance();
        return realm.where(Worker.class)
                .equalTo("wkMobile", phone)
                .count() > 0;

    }

    public static void updateStaffsList(RealmList<Worker> arrayList) {
        Realm realm = Realm.getDefaultInstance();


        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.delete(Worker.class);
        realm.copyToRealmOrUpdate(arrayList);
        realm.commitTransaction();
        realm.close();
    }
}
