package com.oyespace.guards.utils;

import android.util.Log;

import com.oyespace.guards.pojo.VisitorEntryLog;
import com.oyespace.guards.responce.VisitorLogExitResp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import io.realm.Realm;

/**
 * Created by Kalyan on 5/28/2017.
 */

public class RandomUtils {

    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();

    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static ArrayList<VisitorEntryLog> getSortedVisitorLog(ArrayList<VisitorEntryLog> arrayList) {
        ArrayList<VisitorEntryLog> nonExitedSort = new ArrayList<>();
        ArrayList<VisitorEntryLog> exitedSort = new ArrayList<>();

        for (VisitorEntryLog s : arrayList) {
            //if the existing elements contains the search input
            Log.d("button_done ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlExitT().equals("0001-01-01T00:00:00"));

            if (s.getVlExitT().equals("0001-01-01T00:00:00")) {
                Log.d("vlExitT ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlfName() + " ");
                nonExitedSort.add(s);

                //adding the element to filtered list
            } else {
                exitedSort.add(s);

            }
        }

       // LocalDb.saveEnteredVisitorLog(nonExitedSort);

        Collections.sort(exitedSort, new Comparator<VisitorEntryLog>() {
            @Override
            public int compare(VisitorEntryLog lhs, VisitorEntryLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return rhs.getVlExitT().compareTo(lhs.getVlExitT());

            }
        });
        Collections.sort(nonExitedSort, new Comparator<VisitorEntryLog>() {
            @Override
            public int compare(VisitorEntryLog lhs, VisitorEntryLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getVlVisType().compareTo(rhs.getVlVisType());

            }
        });

        ArrayList<VisitorEntryLog> newAl = new ArrayList<>();

        newAl.addAll(nonExitedSort);
        newAl.addAll(exitedSort);

        return  newAl;
    }



    public static ArrayList<VisitorLogExitResp.Data.VisitorLog> getSortedVisitorLog_old(ArrayList<VisitorLogExitResp.Data.VisitorLog> arrayList ){
        ArrayList<VisitorLogExitResp.Data.VisitorLog> nonExitedSort =new ArrayList<>();
        ArrayList<VisitorLogExitResp.Data.VisitorLog> exitedSort =new ArrayList<>();

        for (VisitorLogExitResp.Data.VisitorLog s : arrayList) {
            //if the existing elements contains the search input
            Log.d("button_done ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlExitT().equals("0001-01-01T00:00:00"));

            if (s.getVlExitT().equals("0001-01-01T00:00:00")) {
                Log.d("vlExitT ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlfName() + " ");
                nonExitedSort.add(s);

                //adding the element to filtered list
            } else {
                exitedSort.add(s);

            }
        }

        LocalDb.saveEnteredVisitorLog_old(nonExitedSort);

        Collections.sort(exitedSort, new Comparator<VisitorLogExitResp.Data.VisitorLog>() {
            @Override
            public int compare(VisitorLogExitResp.Data.VisitorLog lhs, VisitorLogExitResp.Data.VisitorLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return rhs.getVlExitT().compareTo(lhs.getVlExitT());

            }
        });
        Collections.sort(nonExitedSort, new Comparator<VisitorLogExitResp.Data.VisitorLog>() {
            @Override
            public int compare(VisitorLogExitResp.Data.VisitorLog lhs, VisitorLogExitResp.Data.VisitorLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getVlVisType().compareTo(rhs.getVlVisType());

            }
        });

        ArrayList<VisitorLogExitResp.Data.VisitorLog> newAl =new ArrayList<>();

        newAl.addAll(nonExitedSort);
        newAl.addAll(exitedSort);

        return  newAl;
    }


    public static boolean entryExists(String isdCode ,String mobNum ) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<VisitorEntryLog> filteredList = new ArrayList<>();
      //  var filteredList = ArrayList<ResponseVisitorLog.Data.Visitorlogbydate>()

        if (LocalDb.getVisitorEnteredLog() == null) {
            filteredList = new ArrayList<>();
        } else {
            filteredList = LocalDb.getVisitorEnteredLog();
        }

        //looping through existing elements
        for (VisitorEntryLog s : filteredList) {
            //if the existing elements contains the search input
          //  Log.d("button_done ", "visitorlogbydate " + s.vlExitT + " " + s.vlExitT.equals("0001-01-01T00:00:00", true) + " ");
            Log.d("button_done ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlExitT().equalsIgnoreCase("0001-01-01T00:00:00") + " ");

            String phoneNumber = s.getVlMobile().replace("+91", "");
            Log.d("Shalini ", "Shalini " + phoneNumber +"...."+isdCode + mobNum);



            // if (s.getVlMobile().equalsIgnoreCase("+" + isdCode + mobNum)) {
            if (phoneNumber.equalsIgnoreCase( mobNum)) {
                Log.d("vlExitT ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlfName() + " ");
                return true;
                //adding the element to filtered list
            } else {
                Log.d("vlExitT else", "visitorlogbydate " +phoneNumber + " " + isdCode + mobNum + " ");

            }
        }
        return false;
    }
}
