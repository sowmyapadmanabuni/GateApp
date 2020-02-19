package com.oyespace.guards.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.oyespace.guards.models.VisitorLog;
import com.oyespace.guards.repo.VisitorLogRepo;
import com.oyespace.guards.responce.VisitorLogExitResp;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import io.realm.RealmList;

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

    public static ArrayList<VisitorLog> getSortedVisitorLog(ArrayList<VisitorLog> arrayList) {
        ArrayList<VisitorLog> nonExitedSort = new ArrayList<>();
        ArrayList<VisitorLog> exitedSort = new ArrayList<>();

        for (VisitorLog s : arrayList) {
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

        Collections.sort(exitedSort, new Comparator<VisitorLog>() {
            @Override
            public int compare(VisitorLog lhs, VisitorLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return rhs.getVlExitT().compareTo(lhs.getVlExitT());

            }
        });
        Collections.sort(nonExitedSort, new Comparator<VisitorLog>() {
            @Override
            public int compare(VisitorLog lhs, VisitorLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getVlVisType().compareTo(rhs.getVlVisType());

            }
        });

        ArrayList<VisitorLog> newAl = new ArrayList<>();

        newAl.addAll(nonExitedSort);
        newAl.addAll(exitedSort);

        return  newAl;
    }



    public static ArrayList<VisitorLog> getSortedVisitorLog_old(ArrayList<VisitorLog> arrayList ){
        ArrayList<VisitorLog> nonExitedSort =new ArrayList<>();
        ArrayList<VisitorLog> exitedSort =new ArrayList<>();

        for (VisitorLog s : arrayList) {
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

        Collections.sort(exitedSort, new Comparator<VisitorLog>() {
            @Override
            public int compare(VisitorLog lhs, VisitorLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return rhs.getVlExitT().compareTo(lhs.getVlExitT());

            }
        });
        Collections.sort(nonExitedSort, new Comparator<VisitorLog>() {
            @Override
            public int compare(VisitorLog lhs, VisitorLog rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getVlVisType().compareTo(rhs.getVlVisType());

            }
        });

        ArrayList<VisitorLog> newAl =new ArrayList<>();

        newAl.addAll(nonExitedSort);
        newAl.addAll(exitedSort);

        return  newAl;
    }


    public static boolean entryExists(String isdCode ,String mobNum ) {
        return VisitorLogRepo.Companion.check_IN_VisitorByPhone(isdCode + mobNum);
    }

    public static boolean contain(RealmList<VisitorLog> list, int id) {

        for (VisitorLog item : list) {
            if (item.getReRgVisID()==id) {
                return true;
            }
        }
        return false;
    }

    public static boolean containMobileNumber(RealmList<VisitorLog> list, String mobileNumber) {

        for (VisitorLog item : list) {

            if (item.getVlMobile()==mobileNumber) {
                Log.v("oyespace number",mobileNumber);
                return true;
            }
        }
        return false;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
//        image.compress(compressFormat, quality, byteArrayOS);
//        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        image.compress(Bitmap.CompressFormat.PNG,100,byteArrayOS);
        byte[] byteArray = byteArrayOS.toByteArray();
        String encoded = Base64.encodeToString(byteArray,Base64.DEFAULT);
        return encoded;
    }
}
