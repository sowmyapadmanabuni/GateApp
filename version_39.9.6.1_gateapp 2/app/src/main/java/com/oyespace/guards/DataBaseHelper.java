package com.oyespace.guards;


import android.content.Context;
import android.util.Log;
import com.oyespace.guards.models.FingerPrint;
import com.oyespace.guards.models.VisitorLog;
import com.oyespace.guards.models.Worker;
import io.realm.Realm;
import io.realm.RealmList;

import java.util.ArrayList;



public class DataBaseHelper {

    private Context context;


    public DataBaseHelper(Context context){
        super();
        this.context = context;
    }

    public DataBaseHelper(){

    }


    public void insertFingerPrints(int fpId, String uname, String finger_type, byte[] photo1, byte[] photo2, byte[] photo3, String MemberType, int aid)
    {
        boolean commitNow = false;

        int _uname = Integer.parseInt(uname);
        Realm realm = Realm.getDefaultInstance();


        FingerPrint existing = realm.where(FingerPrint.class).equalTo("userName",uname).equalTo("FPFngName",finger_type).findFirst();
        if(existing == null) {
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
        }

    }

    public int getTotalFingerPrints(){
        int available=0;
        Realm realm = Realm.getDefaultInstance();
        long avail = realm.where(FingerPrint.class).count();
        available = (int) avail;
        return available;
    }



    public boolean getMemberFingerExists(String username, String FingerName )
    {
        boolean available=false;
        Realm realm = Realm.getDefaultInstance();
        FingerPrint existing = realm.where(FingerPrint.class).equalTo("userName",username).equalTo("FPFngName",FingerName).findFirst();
        Log.e("getMemberFingerExists",""+existing);
        if(existing != null && existing.isValid()){
            available = true;
        }
        return available;
    }

    public int fingercount(int MemberID) {
        Log.e("Check_FingerCount","OF: "+MemberID);
        String memberString = ""+MemberID;
        memberString = memberString.trim();
        int available=0;
        Realm realm = Realm.getDefaultInstance();
        long avail = realm.where(FingerPrint.class).equalTo("FMID",MemberID).count();
        available = (int) avail;
        Log.e("Check_Exist"," "+available);
        return available;

    }


    public ArrayList<FingerPrint> getRegularVisitorsFingerPrint( int AssociationID){

        Realm realm = Realm.getDefaultInstance();
        ArrayList<FingerPrint> fingerPrints = new ArrayList<>();
        fingerPrints.addAll(realm.where(FingerPrint.class).equalTo("ASAssnID",AssociationID).findAll());
        return  fingerPrints;
    }

    public void saveStaffsList(RealmList<Worker> arrayList){
        Realm realm = Realm.getDefaultInstance();
        if(!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.copyToRealmOrUpdate(arrayList);
        realm.commitTransaction();
        realm.close();
    }

    public void saveVisitors(RealmList<VisitorLog> visitorsList){
        Realm realm = Realm.getDefaultInstance();
        if(!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.insertOrUpdate(visitorsList);
        realm.commitTransaction();
    }

    public static ArrayList<VisitorLog> getVisitorEnteredLog(){
        Realm realm = Realm.getDefaultInstance();
        ArrayList<VisitorLog> list = new ArrayList<>();
        list.addAll(realm.where(VisitorLog.class).findAll());
        realm.close();
        return list;
    }

    public static ArrayList<Worker> getStaffs(){
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Worker> list = new ArrayList<>();
        list.addAll(realm.where(Worker.class).findAll());
        realm.close();
        return list;

    }

}
