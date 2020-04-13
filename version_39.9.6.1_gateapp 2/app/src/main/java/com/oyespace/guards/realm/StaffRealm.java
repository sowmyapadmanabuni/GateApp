package com.oyespace.guards.realm;

import com.oyespace.guards.models.Worker;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;

public class StaffRealm {
    public static ArrayList<Worker> getStaff() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Worker> list = new ArrayList<>();
        list.addAll(realm.where(Worker.class).findAll().sort("wkfName"));
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
                .contains("wkMobile", phone)
                .count() > 0;

    }

    public static void updateStaffsList(RealmList<Worker> arrayList) {

        try (Realm realmInstance = Realm.getDefaultInstance()) {
            if(realmInstance.isInTransaction()){
                realmInstance.delete(Worker.class);
                realmInstance.copyToRealmOrUpdate(arrayList);
            }else {
                realmInstance.executeTransaction((realm) -> {
                    realm.delete(Worker.class);
                    realm.copyToRealmOrUpdate(arrayList);
                });
            }
        }
    }

    public static ArrayList<Worker> searchStaff(String searchQuery) {

        Realm realm = Realm.getDefaultInstance();
        return new ArrayList<>(realm.where(Worker.class)
                .contains("wkfName", searchQuery, Case.INSENSITIVE)
                .findAll());


    }
}
