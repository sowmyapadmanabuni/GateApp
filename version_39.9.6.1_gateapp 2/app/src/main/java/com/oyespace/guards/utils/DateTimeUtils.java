package com.oyespace.guards.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Kalyan on 22-Dec-17.
 */

public class DateTimeUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy 'at' hh:mm a");
    public static final SimpleDateFormat DATE_FORMATYMD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_YMDHMS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMATHM = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat DATE_FORMAT_DMY = new SimpleDateFormat("dd-MM-yyyy");


    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formateFullData(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        return localDateTime.toString();
    }

    public static String formatDateYMD(Date date) {

        if (date == null) {
            return "";
        }
        return DATE_FORMATYMD.format(date);
    }

    public static String formatDate_YMDHMS(Date date) {

        if (date == null) {
            return "";
        }
        return DATE_FORMAT_YMDHMS.format(date);
    }

    public static String getCurrentTimeLocal() {
        final SimpleDateFormat DATE_FORMAT_YMDHMS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String currentDate = DATE_FORMAT_YMDHMS.format(new Date());
        return currentDate;
    }

    public static String getCurrentTimeLocalYMD() {
        String currentDate = DATE_FORMATYMD.format(new Date());
        return currentDate;
    }

    public static String formatDateHM(String date) {
        try {
            java.util.Date dt_dwnld_date;
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("hh:mm:ss aa");
            SimpleDateFormat dateFormatGMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormatGMT.setTimeZone(TimeZone.getDefault());
            dt_dwnld_date = dateFormatGMT.parse(date);
            return dateFormatLocal.format(dt_dwnld_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return date;
        } catch (Exception ex) {
            return "";
        }

    }

    public static long msLeft(String et, int maxSec) {

        try {

            DateFormat dateFormatLocal = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            Calendar c1 = Calendar.getInstance();
            final String timeString = "1900-01-01T" + dateFormatLocal.format(DATE_FORMAT_YMDHMS.parse(et));
            c1.setTime(DATE_FORMAT_YMDHMS.parse(timeString));
            String t = "t= " + timeString;
            c1.add(Calendar.SECOND, maxSec);
            t += " t+" + maxSec + "= " + timeString;
            Calendar c2 = Calendar.getInstance();
            final String currentString = "1900-01-01T" + dateFormatLocal.format(DATE_FORMAT_YMDHMS.parse(getCurrentTimeLocal()));
            c2.setTime(DATE_FORMAT_YMDHMS.parse(currentString));
            t += ", c: " + currentString;
            Log.v("taaag", t);
            //get Time in milli seconds
            long ms1 = c1.getTimeInMillis();
            long ms2 = c2.getTimeInMillis();

            return ms1 - ms2;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static boolean dateExpired(String entryDate) {

        try {

            DateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Calendar c1 = Calendar.getInstance();
            final String entryDateString = dateFormatLocal.format(DATE_FORMAT_YMDHMS.parse(entryDate)) + "T00:00:00";
            c1.setTime(DATE_FORMAT_YMDHMS.parse(entryDateString));

            Calendar c2 = Calendar.getInstance();
            final String currentDateString = dateFormatLocal.format(DATE_FORMAT_YMDHMS.parse(getCurrentTimeLocal())) + "T00:00:00";
            c2.setTime(DATE_FORMAT_YMDHMS.parse(currentDateString));

            //get Time in milli seconds
            long ms1 = c1.getTimeInMillis();
            long ms2 = c2.getTimeInMillis();

            return true;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean deliveryTimeUp(String downloaded_date, String curr_date_YMD_hms, int maxMins) {

        try {
            java.util.Date dt_dwnld_date, dt_curr_date;
            // DateFormat dateFormatGMT =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",  Locale.getDefault());
            // DateFormat dateFormatHMS =new SimpleDateFormat("HH:mm:ss",  Locale.getDefault());
            DateFormat dateFormatLocal = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            //  dateFormatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
            dt_dwnld_date = DATE_FORMAT_YMDHMS.parse(downloaded_date);
            Calendar c1 = Calendar.getInstance();
            //Change to Calendar Date
            c1.setTime(dt_dwnld_date);

            dt_curr_date = DATE_FORMAT_YMDHMS.parse(curr_date_YMD_hms);

            Calendar c2 = Calendar.getInstance();
            //Change to Calendar Date
            c2.setTime(DATE_FORMAT_YMDHMS.parse(DATE_FORMAT_YMDHMS.format(dt_curr_date)));
            c2.setTime(DATE_FORMAT_YMDHMS.parse("1900-01-01T" + dateFormatLocal.format(dt_curr_date)));
            //get Time in milli seconds
            long ms1 = c1.getTimeInMillis();
            long ms2 = c2.getTimeInMillis();

            //get difference in milli seconds
            long diff_sec = ms2 - ms1;
            int day_diff = (int) diff_sec / (60 * 1000);
            int allottedTime = maxMins;

            //int allottedTime=8+itemCount*7;

            if (day_diff >= 120) {
//                return day_diff;
                return true;
            }
            return day_diff >= allottedTime;
//            return day_diff;
        } catch (java.text.ParseException e) {

            e.printStackTrace();
            return true;
//            return 7;
        }

    }


    public static String formatDateDMY(String date) {
        try {
            java.util.Date dt_dwnld_date;
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat dateFormatGMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            dateFormatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
            dt_dwnld_date = dateFormatGMT.parse(date);
            return dateFormatLocal.format(dt_dwnld_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return date;
        } catch (Exception ex) {
            return "";
        }

    }

    public static boolean compareDate(String startSH, String stopSH) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date strDate = null, strDate1 = null, nowDate = null;
            strDate = sdf.parse(startSH);
            strDate1 = sdf.parse(stopSH);
            nowDate = sdf.parse(sdf.format(new Date()));

            Log.d(" value familyMembers", startSH + "  " + stopSH + " " + strDate.before(nowDate) + " " + strDate1.after(nowDate));

            if (strDate.equals(nowDate) || strDate1.equals(nowDate)) {
                return true;
            }
            if (strDate.after(strDate1)) {
                return strDate1.before(nowDate);
            } else {
                return strDate.before(nowDate) && strDate1.after(nowDate);
            }
        } catch (java.text.ParseException e) {
            return false;
        }

    }


    public static String formatTimeHM(String date) {
        try {
            java.util.Date dt_dwnld_date;


            String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";

            //  String outputPattern = "dd/MM/yyyy HH:mm:ss";MM/dd/yyyy '@'hh:mm a
            String outputPattern = "dd/MM/yyyy HH:mm:ss";

            LocalDateTime inputDate = null;
            String outputDate = null;


            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern, Locale.ENGLISH);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputPattern, Locale.ENGLISH);

            inputDate = LocalDateTime.parse(date, inputFormatter);
            outputDate = outputFormatter.format(inputDate);

            System.out.println("inputDate: " + inputDate);
            System.out.println("outputDate: " + outputDate);


            //  SimpleDateFormat dateFormatLocal = new SimpleDateFormat("hh:mm:ss aa");
            SimpleDateFormat dateFormatGMT = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            dateFormatGMT.setTimeZone(TimeZone.getDefault());
            dt_dwnld_date = dateFormatGMT.parse(date);
            //  Log.d("mycode1156 time", dateFormatLocal.format(dt_dwnld_date) + " ");
            return outputDate;
        } catch (java.text.ParseException e) {
            //Log.d("mycode1156 time", " " + e.toString());
            e.printStackTrace();
            return date;
        } catch (Exception ex) {
            return "";
        }

    }

    public static boolean CheckDates(String startDate, String endDate,Context context) {

        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        boolean b = false;

        try {
            // Toast.makeText(context,"222",Toast.LENGTH_LONG).show();
            // If start date is after the end date.
            //Toast.makeText(context,"333",Toast.LENGTH_LONG).show();
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                //Toast.makeText(context,"111",Toast.LENGTH_LONG).show();
                b = true;  // If start date is before end date.
            } else
                b = dfDate.parse(startDate).equals(dfDate.parse(endDate));  // If two dates are equal.
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return b;
    }

//    public void overlayAlert(Context context) {
//
//        final Dialog dialog = new Dialog(context); // Context, this, etc.
//        dialog.setContentView(R.layout.layout_subscriptiondailog);
//        dialog.setTitle(R.string.dialog_title);
//        dialog.show();
//
//    }



}
