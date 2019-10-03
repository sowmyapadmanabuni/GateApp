package com.oyespace.guards;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.CallLog;
import android.util.Log;

import com.oyespace.guards.models.FingerPrint;
import com.oyespace.guards.models.VisitorLog;
import com.oyespace.guards.models.Worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

import static com.oyespace.guards.utils.ConstantUtils.Emergency;


public class DataBaseHelper extends SQLiteOpenHelper {

    static final String dbName = "ghtest43.db";
    private static final int DATABASE_VERSION = 2;
    private static String DB_PATH;
    private final Context context;
    private SQLiteDatabase sqliteDBInstance = null;

    public DataBaseHelper(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        this.context = context;
        ContextWrapper cw = new ContextWrapper(context);

        //DB_PATH =cw.getFilesDir().getAbsolutePath()+ "/databases/";
        //  DB_PATH = "/data/data/" + context.getPackageName() + "/";
        DB_PATH = "/data/data/\" + context.getPackageName() + \"/databases/";
    }

    public static List<String> getfiveCallDetails(Context context) {

        StringBuffer sb = new StringBuffer();
        String phNumber = "k";

        List<String> list = new ArrayList<String>();
        //  Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        @SuppressLint("MissingPermission") Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        if (managedCursor.getCount() > 5) {

            for (int i = managedCursor.getCount(); i >= managedCursor.getCount() - 5; i--) {
                managedCursor.moveToPosition(i - 1);
                phNumber = managedCursor.getString(number); // mobile number
                list.add(phNumber);

            }

        }

        return list;
    }

    public static ArrayList<Worker> getStaffs() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Worker> list = new ArrayList<>();
        list.addAll(realm.where(Worker.class).findAll());
        realm.close();
        return list;

    }

    public void createDataBase() {
        boolean dbExist = checkDataBase();
        if (dbExist) {

        } else {
            this.getReadableDatabase();
            //		copyDataBase();
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + dbName);
        return dbFile.exists();
    }

    public void deleteDataBase() {
        File dbFile = new File(DB_PATH + dbName);
        dbFile.delete();
        Log.d("Database", " database " + checkDataBase());
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = context.getAssets().open(dbName);
        String outFileName = DB_PATH + dbName;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // 	Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.i("Database", "New database has been copied to device!");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 	TODO Auto-generated method stub
        Log.d("database", "created...!!!!!!");


        String CREATE_VisitorData_TABLE = " create table IF NOT EXISTS VisitorData(VisitorID integer primary key autoincrement, UnitName VARCHAR(50),AssociationID VARCHAR(150), Name VARCHAR(150),MemberId integer, StaffId integer, UnitID integer , MobileNumber VARCHAR(20) , Designation VARCHAR(50), WorkerType VARCHAR(50),VisitorCount integer, VisitorEntryTime DateTime2(7), VisitorExitTime DateTime2(7)) ";
        db.execSQL(CREATE_VisitorData_TABLE);
        Log.d("BlockUnit_TABLE", "Created");

//
//        String CREATE_Worker_TABLE ="create table IF NOT EXISTS Worker(WorkerID integer primary key autoincrement, AssociationID integer)";
//        //+
////                ", MemberID integer, SfaffID integer,"+
////                "UnitID integer, MobileNumber VARCHAR(50), Name VARCHAR(50), Designation VARCHAR(50), WorkerType VARCHAR(50),UnitName VARCHAR(50),VisitorCount integer, VisitorEntryTime VARCHAR(50), VisitorExitTime VARCHAR(50) ) ";
//        db.execSQL(CREATE_Worker_TABLE);

//        String CREATE_StaffWorker_TABLE = " create table IF NOT EXISTS StaffWorker(StaffWorkeID integer primary key autoincrement,AssociationID integer ," +
//                " MemberId integer, StaffId integer, UnitID integer , MobileNumber text not null , Name VARCHAR(40), Designation VARCHAR(50), WorkerType VARCHAR(50),UnitName VARCHAR(50),VisitorCount integer, VisitorEntryTime DateTime2(7), VisitorExitTime DateTime2(7)) ";
//        db.execSQL(CREATE_StaffWorker_TABLE);
//

        String CREATE_StaffWorker_TABLE = " create table IF NOT EXISTS StaffWorker(StaffWorkeID integer primary key autoincrement,AssociationID integer ," +
                " MemberId integer, StaffId integer, UnitID integer , MobileNumber VARCHAR(20) , Name VARCHAR(40), Designation VARCHAR(50), WorkerType VARCHAR(50),UnitName VARCHAR(50),VisitorCount integer, VisitorEntryTime DateTime2(7), VisitorExitTime DateTime2(7)) ";
        db.execSQL(CREATE_StaffWorker_TABLE);

//        String CREATE_StudyList_TABLE = " create table IF NOT EXISTS userdetails(usersno integer primary key autoincrement," +
//                " username text not null, finger_type text not null , photo_FP1 BLOB, photo_FP2 BLOB, photo_FP3 BLOB) ";
//        db.execSQL(CREATE_StudyList_TABLE);
//        Log.d("CREATE_StudyList  has ", "created...!!!!!!");
        String CREATE_StudyList_TABLE = " create table IF NOT EXISTS userdetails(usersno integer primary key autoincrement," +
                " username text not null, finger_type text not null , photo_FP1 varbinary(255), photo_FP2  varbinary(255) , photo_FP3  varbinary(255) , MemberType text not null,AssociationID integer) ";
        db.execSQL(CREATE_StudyList_TABLE);
        Log.d("CREATE_StudyList  has ", "created...!!!!!!");


        String CREATE_Counter_TABLE = " create table IF NOT EXISTS countereference(counter integer primary key autoincrement) ";
        db.execSQL(CREATE_Counter_TABLE);
        Log.d("CREATE_Counter has", "created...!!!!!!");

        String CREATE_Association_TABLE = " create table IF NOT EXISTS Association(AssociationID integer ," +
                " Name TEXT, Country VARCHAR(40) , Locality VARCHAR(80) , PanNumber VARCHAR(20) , Pincode VARCHAR(40) , " +
                " GPSLocation VARCHAR(40) , TotalUnits integer, MaintenanceRate double, MaintenancePenalty double, " +
                " PropertyCode VARCHAR(40) , FyStart integer, MaintPymtFreq integer, OTPStatus VARCHAR(20), " +
                "PhotoStatus VARCHAR(20), NameStatus VARCHAR(20), MobileStatus VARCHAR(20), LogoffStatus VARCHAR(20), Validity VARCHAR(20), AssPrpType VARCHAR(20) ) ";
        db.execSQL(CREATE_Association_TABLE);
        Log.d("Association  has ", "created...!!!!!!");

        String CREATE_OyeUnit_TABLE = " create table IF NOT EXISTS OyeUnit(UnitID integer , " +
                " AssociationID integer , UnitName VARCHAR(20) ,  Type VARCHAR(20) , AdminAccountID integer , " +
                " CreatedDateTime VARCHAR(20),  ParkingSlotNumber VARCHAR(20) ) ";
        db.execSQL(CREATE_OyeUnit_TABLE);
        Log.d("OyeUnit  has ", "created...!!!!!!");

        String CREATE_OyeMembers_TABLE = " create table IF NOT EXISTS OyeMembers(MemberID integer , " +
                " AssociationID integer , UnitID integer , AccountID integer,ParentMemberID integer, MemberRoleID integer, " +
                " CreatedDate VARCHAR(40) , ReferalID VARCHAR(20), ResidentType VARCHAR(20), RemovalMemberID integer, " +
                " RemovedDateTime VARCHAR(20), MobileNumber VARCHAR(20) not null , Name VARCHAR(40), VehicleNumber VARCHAR(20), " +
                " MemberStatus VARCHAR(50), DownloadedDate VARCHAR(20), LeaveAtGate VARCHAR(20), DND VARCHAR(20)  ) ";
        db.execSQL(CREATE_OyeMembers_TABLE);
        Log.d("OyeMembers  has ", "created...!!!!!!");


        String CREATE_UnitOwner_TABLE = " create table IF NOT EXISTS UnitOwner(UnitOwnerID integer , " +
                " AssociationID integer , UnitID integer , FirstName VARCHAR(20), " +
                " LastName VARCHAR(20), MobileNumber VARCHAR(20),CreatedDate VARCHAR(40), UnitOwnerStatus VARCHAR(20) ) ";
        db.execSQL(CREATE_UnitOwner_TABLE);
        Log.d("UnitOwener  has ", "created...!!!!!!");

        String CREATE_SecurityGuard_TABLE = " create table IF NOT EXISTS SecurityGuard(GuardID integer , " +
                " AccountID integer, AssociationID integer , OYEMemberID integer , OYEMemberRoleID integer," +
                " GuardRoleID integer, Name VARCHAR(40), MobileNumber VARCHAR(20) not null , PhotoID integer, " +
                " CreatedDate VARCHAR(20) , AadharNumber VARCHAR(20), PhotoArray BLOB , Status VARCHAR(40) ) ";
        db.execSQL(CREATE_SecurityGuard_TABLE);
        Log.d("SecurityGuard  has ", "created...!!!!!!");

        String CREATE_Patrolling_Notification_TABLE = "create table IF NOT EXISTS PatrollingNotification(PatrolNid integer primary key autoincrement," +
                "AssociatoinID integer, Date VARCHAR(30), GuardID integer,  PatrolTime VARCHAR(30), StartTime VARCHAR(30), PatrolDone boolean, Notified boolean)";
        db.execSQL(CREATE_Patrolling_Notification_TABLE);
        Log.d("Notification created", "created...!!!!!!");

        String CREATE_Shifts_TABLE = " create table IF NOT EXISTS Shifts(ShiftID integer, " +
                " AccountID integer, AssociationID integer , GuardID integer , " +
                " StartDate VARCHAR(20), EndDate VARCHAR(20),  ShiftStartTime VARCHAR(20), ShiftEndTime VARCHAR(20), " +
                " CreatedDate VARCHAR(20)  ) ";
        db.execSQL(CREATE_Shifts_TABLE);
        Log.d("Shifts  has ", "created...!!!!!!");

        String CREATE_Invitation_TABLE = " create table IF NOT EXISTS Invitations(OYEFamilyMemberID integer primary key , " +
                " AssociationID integer , OYEUnitID integer, FirstName VARCHAR(20),  LastName VARCHAR(20), MobileNumber VARCHAR(20), " +
                " PhotoID integer, VisitorType VARCHAR(40), Active integer, " +
                " CreatedDate VARCHAR(20) , AadharNumber VARCHAR(20) , PhotoArray BLOB, VehicleNumber VARCHAR(12), VisitorCount integer) ";
        db.execSQL(CREATE_Invitation_TABLE);
        Log.d("Invitations  has ", "created...!!!!!!");

        String CREATE_Attendance_TABLE = " create table IF NOT EXISTS Attendance(AttendanceID integer , " +
                " GuardID integer , AssociationID integer , IMEINo VARCHAR(30), StartDate VARCHAR(20),EndDate VARCHAR(20),  " +
                " StartTime VARCHAR(10), StartGPSPoint VARCHAR(30), " +
                " EndTime VARCHAR(10) , EndGPSPoint VARCHAR(30) ) ";
        db.execSQL(CREATE_Attendance_TABLE);
        Log.d("Attendance  has ", "created...!!!!!!");

        String CREATE_FamilyMembers_TABLE = " create table IF NOT EXISTS FamilyMembers(OYEFamilyMemberID integer primary key , " +
                " AssociationID integer , OYEUnitID integer, FirstName VARCHAR(20),  LastName VARCHAR(20), MobileNumber VARCHAR(20), " +
                " PhotoID integer, VisitorType VARCHAR(40), Active integer, " +
                " CreatedDate VARCHAR(20) , AadharNumber VARCHAR(20) , PhotoArray BLOB, VehicleNumber VARCHAR(12), VisitorCount integer) ";
        db.execSQL(CREATE_FamilyMembers_TABLE);
        Log.d("FamilyMembers  has ", "created...!!!!!!");

        String CREATE_CourierNotification_TABLE = " create table IF NOT EXISTS CourierNotification(CourierID integer, AssociationID integer , " +
                " OYENonRegularVisitorID integer , OYEMemberID integer , OYEFamilyMemberID integer , " +
                " CreateDateTime VARCHAR(20), ResponseText VARCHAR(20)) ";
        db.execSQL(CREATE_CourierNotification_TABLE);
        Log.d("CourierNotification ", "created...!!!!!!");


        /*"oyeFamilyMemberID": 5,         "firstName": "Billu",        "lastName": "B",
                "mobileNumber": "+919494664646",        "PhotoID": "System.Byte[]",         "visitorType": "Family",
                "AadharNumber": "",         "associationID": 12,         "oyeUnitID": 10,
                "CreateDate": "2018-05-10T17:20:54"*/

        String CREATE_RegularVisitors_TABLE = " create table IF NOT EXISTS RegularVisitors(RegularVisitorsID integer primary key, " +
                " VirtualID integer, MemberID integer , UnitID integer," +
                " StartDate VARCHAR(20), EndDate VARCHAR(20),  WorkStartTime VARCHAR(20), WorkEndTime VARCHAR(20), " +
                " CreatedDate VARCHAR(20) , UpdatedDate VARCHAR(20) ) ";
        db.execSQL(CREATE_RegularVisitors_TABLE);
        Log.d("RegularVisitors  has ", "created...!!!!!!");

        String CREATE_RegularVisitorsLog_TABLE = " create table IF NOT EXISTS RegularVisitorsLog(RegVisitorLogID integer primary key, " +
                " VirtualID integer, AssociationID integer , UnitID integer, VisitorType VARCHAR(20), " +
                " VisitorCount integer,  PhotoID integer, VehiclePhotoID integer, ParcelPhotoID integer , " +
                " EntryDateTime VARCHAR(20), ExitDateTime VARCHAR(20),  EntryGuardID integer, ExitGuardID integer, " +
                " VehicleNumber VARCHAR(20), VehicleType VARCHAR(20),  ItemCount integer, " +
                " UpdatedDate VARCHAR(20) , Offline integer,  OYEMemberID integer, Comment VARCHAR(200), CommentImage VARCHAR(200) ) ";
        db.execSQL(CREATE_RegularVisitorsLog_TABLE);
        Log.d("RegularVisitorsLo  has ", "created...!!!!!!");

        String CREATE_NRVisitorsLog_TABLE = " create table IF NOT EXISTS NRVisitorsLog(NRVisitorLogID integer primary key, " +
                " AssociationID integer , UnitID integer, VisitorType VARCHAR(20), " +
                " FirstName VARCHAR(20), LastName VARCHAR(20) , MobileNumber VARCHAR(20) not null,  " +
                " VisitorCount integer,  PhotoID integer, VehiclePhotoID integer, ParcelPhotoID integer , " +
                " ServiceProviderName VARCHAR(20), Purpose VARCHAR(20) , " +
                " EntryDateTime VARCHAR(20), ExitDateTime VARCHAR(20),  EntryGuardID integer, ExitGuardID integer, " +
                " VehicleNumber VARCHAR(20),RegType VARCHAR(20), VehicleType VARCHAR(20),  ItemCount integer, " +
                " UpdatedDate VARCHAR(20) , Offline integer ,PhotoArray BLOB, VehPhotoArray BLOB,ParcelPhotoArray BLOB, UnitNames text, OYEMemberID integer, Comment VARCHAR(200), CommentImage VARCHAR(200)) ";
        db.execSQL(CREATE_NRVisitorsLog_TABLE);
        Log.d("NRVisitorsLog  has ", "created...!!!!!!");

        String CREATE_WaterTankerDetails_TABLE = " create table IF NOT EXISTS WaterTankerDetails(NRVisitorID integer primary key, " +
                " Capacity integer, GPSPointUnloading VARCHAR(30) , Block VARCHAR(20) ) ";
        db.execSQL(CREATE_WaterTankerDetails_TABLE);
        Log.d("WaterTankerDetails ", "created...!!!!!!");

        String CREATE_VisitorApprovals_TABLE = " create table IF NOT EXISTS VisitorApprovals(VisitorApprovalID integer primary key, " +
                " NRVisitorID integer, MemberID integer , UnitID integer, " +
                " ApprovalDateTime VARCHAR(20) , PermitType VARCHAR(20) ) ";
        db.execSQL(CREATE_VisitorApprovals_TABLE);
        Log.d("VisitorApprovals  has ", "created...!!!!!!");

        String CREATE_RouteTracker_TABLE = " create table IF NOT EXISTS RouteTracker(ID integer primary key , " +
                " AssociationID integer, GuardID integer , PatrollingTrackerID integer,  Date VARCHAR(20) , " +
                " Time VARCHAR(20) , GPSPoint VARCHAR(30) , CheckPointName VARCHAR(20) , Image VARCHAR(20)  ) ";
        db.execSQL(CREATE_RouteTracker_TABLE);
        Log.d("RouteTracker  has ", "created...!!!!!!");

        String CREATE_RouteCheckPoints_TABLE = " create table IF NOT EXISTS RouteCheckPoints(CheckPointsID integer, " +
                " AssociationID integer , CheckPointName VARCHAR(20) , MemberID integer , GPSPoint VARCHAR(30) , " +
                " Image VARCHAR(20) ,  CreatedDate VARCHAR(20) ) ";
        db.execSQL(CREATE_RouteCheckPoints_TABLE);
        Log.d("RouteCheckPoints  has ", "created...!!!!!!");

        String CREATE_VisitorParkingLot_TABLE = " create table IF NOT EXISTS VisitorParkingLot(AssociationID integer , " +
                " MemberID integer , VisitorParkingLotName VARCHAR(20) primary key,  CreatedDate VARCHAR(20) , " +
                " GPSPoint VARCHAR(30) , Status VARCHAR(20)  ) ";
        db.execSQL(CREATE_VisitorParkingLot_TABLE);
        Log.d("VisitorParkingLot  has ", "created...!!!!!!");

        String CREATE_VisitorParkingAlotment_TABLE = " create table IF NOT EXISTS VisitorParkingAlotment(AssociationID integer , " +
                " NRVisitorID integer , VisitorParkingLotName VARCHAR(20),  StartDateTime VARCHAR(20) , " +
                " EndDateTime VARCHAR(20)  ) ";
        db.execSQL(CREATE_VisitorParkingAlotment_TABLE);
        Log.d("VisitorParkingAlotment ", "created...!!!!!!");

        String CREATE_IncidentCategory_TABLE = " create table IF NOT EXISTS IncidentCategory(IncidentCategoryID integer , " +
                " AssociationID integer , IncidentCategoryName VARCHAR(30) ,  Active VARCHAR(20) , " +
                " CreatedDate VARCHAR(20) ,RemoverMemberID integer , RemovedDate VARCHAR(20)   ) ";
        db.execSQL(CREATE_IncidentCategory_TABLE);
        Log.d("IncidentCategory ", "created...!!!!!!");

      /*  String CREATE_IncidentReport_TABLE = " create table IF NOT EXISTS IncidentReport(IncidentID integer , " +
                " GuardID integer ,AssociationID integer , UnitName VARCHAR(30) ,  IncidentCategoryID VARCHAR(20) , " +
                " IncidentDetails VARCHAR(20) ,IncidentGPS VARCHAR(30) , DateTime VARCHAR(20) , " +
                "  PhotoID integer ,AudioID integer , Status VARCHAR(20)   ) ";
        db.execSQL(CREATE_IncidentReport_TABLE);
      */
        Log.d("IncidentReport ", "created...!!!!!!");
        String CREATE_IncidentReport_TABLE = " create table IF NOT EXISTS IncidentReport(IncidentID integer , " +
                " GuardID integer ,AssociationID integer , UnitName VARCHAR(30) ,  IncidentCategoryID VARCHAR(20) , " +
                " IncidentDetails VARCHAR(20) ,IncidentGPS VARCHAR(30) , DateTime VARCHAR(20) , " +
                "  PhotoID integer ,AudioID integer , Status VARCHAR(20) ,assignedTo VARCHAR(20) , eta VARCHAR(20) ) ";
        db.execSQL(CREATE_IncidentReport_TABLE);


        String CREATE_MyMembership_TABLE = " create table IF NOT EXISTS MyMembership( OYEMemberID integer , " +
                " AssociationID integer ,OYEUnitID integer , FirstName VARCHAR(30) ,  LastName VARCHAR(30) , " +
                " MobileNumber VARCHAR(20) ,Email VARCHAR(30) ,ParentAccountID integer ,  " +
                "  OYEMemberRoleID integer , Status VARCHAR(20) ,AccountID  integer ,VehicleNumber VARCHAR(100) ) ";
        db.execSQL(CREATE_MyMembership_TABLE);
        Log.d("MyMemberShip ", "created...!!!!!!");

        String CREATE_EmergencyResponses_TABLE = " create table IF NOT EXISTS EmergencyResponses(ResponseID integer , " +
                " IncidentID integer ,MemberID integer , DateTime VARCHAR(20) ,  Status VARCHAR(20) , " +
                " ResolvedDetails VARCHAR(20) ,GPSPoint VARCHAR(30) ) ";
        db.execSQL(CREATE_EmergencyResponses_TABLE);
        Log.d("EmergencyResponses ", "created...!!!!!!");

       /* String CREATE_Security_Notification_TABLE = "create table IF NOT EXISTS SecurityNotification(Nid integer primary key autoincrement," +
                "AssociatoinID integer, noti_title VARCHAR(30), sub_title VARCHAR(30), notified VARCHAR(30))";
        db.execSQL(CREATE_Security_Notification_TABLE);
        Log.d("Notification created","created...!!!!!!");
*/
        String CREATE_Security_Notification_TABLE = "create table IF NOT EXISTS SecurityNotification(Nid integer primary key autoincrement," +
                "AssociatoinID integer, noti_title VARCHAR(30), sub_title VARCHAR(30), notified VARCHAR(30), noti_type VARCHAR(30), noti_id integer," +
                "MobileNumber VARCHAR(30) )";
        db.execSQL(CREATE_Security_Notification_TABLE);
        Log.d("Notification created", "created...!!!!!!");


        String CREATE_PhotosTable_TABLE = " create table IF NOT EXISTS PhotosTable(PhotoID integer primary key autoincrement, " +
                " AssociationId integer , Table integer , UniqueID integer,  DateofCapture VARCHAR(20) , " +
                " ImagePurpose VARCHAR(20) , gpsPoint VARCHAR(30) ) ";
//        db.execSQL(CREATE_PhotosTable_TABLE);
        Log.d("PhotosTable ", "created...!!!!!!");

        String CREATE_LocalPhotosTable_TABLE = " create table IF NOT EXISTS LocalPhotosTable(PhotoID integer primary key autoincrement, " +
                " AssociationId integer , Table VARCHAR(20) , UniqueID integer,  DateofCapture VARCHAR(20) , " +
                " ImagePurpose VARCHAR(20) , gpsPoint VARCHAR(30), photoData BLOB , ImageName VARCHAR(40) ) ";
//        db.execSQL(CREATE_LocalPhotosTable_TABLE);
        Log.d("PhotosTable ", "created...!!!!!!");

        String CREATE_Invitedvisitorlocal_TABLE = " create table IF NOT EXISTS Invitedvisitorlocal(MTypeID integer , " +
                " AssociationID integer ,OYEUnitID integer , MemberType VARCHAR(20) ,  StartDate VARCHAR(20) , " +
                " EndDate VARCHAR(20) ,WorkStartTime VARCHAR(30),WorkEndTime integer , OYEFamilyMemberID integer) ";
        db.execSQL(CREATE_Invitedvisitorlocal_TABLE);
        Log.d("Invitedvisitorlocal ", "created...!!!!!!");

        String CREATE_PatrollingShiftDetails_TABLE = " create table IF NOT EXISTS PatrollingShiftDetails(PatrollingShiftID integer, AssociationID integer , " +
                " OYEMemberID integer ,Patrollingfrequency integer , Starttime VARCHAR(20) ,  Endtime VARCHAR(20) , " +
                " RepeatingDays VARCHAR(50), PatrollingcheckpointID VARCHAR(50), GuardID VARCHAR(30), ValidityDate VARCHAR(30)) ";
        db.execSQL(CREATE_PatrollingShiftDetails_TABLE);
        Log.d("PatrollingShiftDetails ", "created...!!!!!!");

        String CREATE_ResidentVehicles_TABLE = " create table IF NOT EXISTS ResidentVehicles(OYEVehicleId integer , " +
                "AssociationID integer , " +
                " OYEUnitID integer , OYEMemberID integer ,  VehicleNo VARCHAR(30) , " +
                " VehicleType VARCHAR(20) , Status VARCHAR(20)) ";
        db.execSQL(CREATE_ResidentVehicles_TABLE);
        Log.d("ResidentVehicle ", "created...!!!!!!");


        String CREATE_Temp_FamilyMembers_TABLE = " create table IF NOT EXISTS TempFamilyMembers(OYEFamilyMemberID integer , " +
                " AssociationID integer , OYEUnitID integer, FirstName VARCHAR(20),  LastName VARCHAR(20), MobileNumber VARCHAR(20), " +
                " PhotoID integer, VisitorType VARCHAR(40), Active integer, " +
                " CreatedDate VARCHAR(20) , AadharNumber VARCHAR(20) , PhotoArray BLOB, VehicleNumber VARCHAR(12), VisitorCount integer) ";
        db.execSQL(CREATE_Temp_FamilyMembers_TABLE);
        Log.d("Temp_Family  has ", "created...!!!!!!");

        String CREATE_Temp_FingerPrint_TABLE = " create table IF NOT EXISTS TempFinger(usersno integer," +
                " username text not null, finger_type text not null , photo_FP1 BLOB, photo_FP2 BLOB, photo_FP3 BLOB, MemberType text not null) ";
        db.execSQL(CREATE_Temp_FingerPrint_TABLE);
        Log.d("Temp_ FingerPrint  has ", "created...!!!!!!");


      /*  String CREATE_Worker_TABLE = " create table IF NOT EXISTS Workers(WorkerID integer , " +
                "AssnID integer , FName VARCHAR(50), LName VRACHAR(50), WKMobile VARCHAR(20), WKImgName VARCHAR(200)" +
                " WrkType VARCHAR(20) , Design VARCHAR(20) ,  VehicleNo VARCHAR(30) , " +
                " VehicleType VARCHAR(20) , Status VARCHAR(20)) ";
        db.execSQL(CREATE_ResidentVehicles_TABLE);
        Log.d("WorkerTable ", "created...!!!!!!");*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        if (oldVersion < 2) {

            db.execSQL("ALTER TABLE userdetails ADD COLUMN AssociationID INTEGER");


            Log.d("+++++++", "'INSIDE UpGrade...'");
            // 7th upgrade code is masked/*
//            if(!isFieldExist(NBA_Table,"pre_lat",db)){
//                db.execSQL("ALTER TABLE "+ NBA_Table +" ADD pre_lat VARCHAR(15)");
//                Log.d("NBA_Table ", "pre_lat added.!!!!");
//            }

            //		db.execSQL("ALTER TABLE "+ NBA_Table_public +" ADD pre_dpvt_reason VARCHAR(50)");
        }


    }

    public boolean isFieldExist(String tableName, String fieldName, SQLiteDatabase DB) {
        boolean isExist = true;
        //		SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = DB.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        int value = res.getColumnIndex(fieldName);

        if (value == -1) {
            isExist = false;
        }
        res.close();
        return isExist;
    }

    public void openDB() throws SQLException {
        Log.i("openDB", "Checking sqliteDBInstance...");
        if (this.sqliteDBInstance == null) {
            Log.i("openDB", "Creating sqliteDBInstance...");
            this.sqliteDBInstance = this.getWritableDatabase();
        }
    }

    public long insertSecurityNotificationTable_old(int aid, String title, String subtitle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociatoinID", aid);
        initialValues.put("noti_title", title);
        initialValues.put("sub_title", subtitle);
        initialValues.put("notified", "false");
//        Cursor cursor = db.rawQuery("SELECT * FROM SecurityNotification where Nid=trim('"+number
//                +"') and   email=trim('"+email+"')  ", null);
//        Log.d("count",cursor.getCount()+"");
//        if(cursor.getCount() >0)
//        {
//            cursor.moveToFirst();
//            Log.d(" value"," updated "+number+" "+email);
//            return cursor.getInt(0);
//        }else{
//            Log.d(" value"," inserted "+Fname+" "+email);
        return db.insert("SecurityNotification", null, initialValues);


    }

    public Cursor updatesecuritynotification_setNotified(int notificationID) {
        String value = "true";
        String lvalue = "false";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE SecurityNotification SET notified=trim('" + value + "') where Nid=trim('" + notificationID + "')";
        Cursor cur = db.rawQuery(sql, null);
        Log.d("thor", String.valueOf(cur.getCount()));
        return cur;

    }

    public long insertSecurityNotificationTable(int aid, String title, String subtitle, String type, int id, String mob) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociatoinID", aid);
        initialValues.put("noti_title", title);
        initialValues.put("sub_title", subtitle);
        initialValues.put("notified", "false");
        initialValues.put("noti_type", type);
        initialValues.put("noti_id", id);
        initialValues.put("MobileNumber", mob);
//        Cursor cursor = db.rawQuery("SELECT * FROM SecurityNotification where Nid=trim('"+number
//                +"') and   email=trim('"+email+"')  ", null);
        Log.d("Dgddfdfeemer", id + " " + title);
//        if(cursor.getCount() >0)
//        {
//            cursor.moveToFirst();
//            Log.d(" value"," updated "+number+" "+email);
//            return cursor.getInt(0);
//        }else{
//            Log.d(" value"," inserted "+Fname+" "+email);
        long in = db.insert("SecurityNotification", null, initialValues);
        Log.d("Dgddfdfeemer", id + " " + title + " " + in);
        return in;

    }

    public Cursor getEmergencyNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        String value = "false";
        String sql = "SELECT * FROM SecurityNotification where notified=trim('" + value + "') and noti_title ='" + Emergency + "'";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                Log.d(" value1", " Assid " + cur.getInt(1) + " ");
                Log.d(" value2", " title " + cur.getString(2) + " ");
                Log.d(" value3", " subtitle " + cur.getString(2) + " ");
                Log.d(" value4", " notified " + cur.getString(4) + " ");
                Log.d(" value", " nid" + cur.getInt(0) + " ");

            } while (cur.moveToNext());
            Log.d(" value315", " all count " + cur.getCount() + " ");
            return cur;


        }
        return cur;
    }

    public long insertPatrollingShiftDetailsTable(int patrolid, int aid, int oymid, int P_freq, String stime, String etime,
                                                  String days, String checkpointsid, String GuardID, String createddate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("PatrollingShiftID", patrolid);
        initialValues.put("AssociationID", aid);
        initialValues.put("OYEMemberID", oymid);
        initialValues.put("Patrollingfrequency", P_freq);
        initialValues.put("Starttime", stime);
        initialValues.put("Endtime", etime);
        initialValues.put("RepeatingDays", days);
        initialValues.put("PatrollingcheckpointID", checkpointsid);
        initialValues.put("GuardID", GuardID);
        initialValues.put("ValidityDate", createddate);

        Cursor cursor = db.rawQuery("SELECT * FROM PatrollingShiftDetails where PatrollingShiftID=trim('" + patrolid
                + "') and   AssociationID=trim('" + aid + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            cursor.close();
            //  Log.d(" value"," updated "+number+" "+email);
            return id;
        } else {
            // Log.d(" value"," inserted "+Fname+" "+email);
            cursor.close();
            return db.insert("PatrollingShiftDetails", null, initialValues);
        }

    }

    public Cursor getSecurityNotification() {
        SQLiteDatabase db = this.getReadableDatabase();
        String value = "false";
        String sql = "SELECT * FROM SecurityNotification where notified=trim('" + value + "')";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                Log.d(" value1", " Assid " + cur.getInt(1) + " ");
                Log.d(" value2", " title " + cur.getString(2) + " ");
                Log.d(" value3", " subtitle " + cur.getString(2) + " ");
                Log.d(" value4", " notified " + cur.getString(4) + " ");
                Log.d(" value", " nid" + cur.getInt(0) + " ");

            } while (cur.moveToNext());
            Log.d(" value315", " all count " + cur.getCount() + " ");
            return cur;


        }
        return cur;
    }

    public Cursor updatesecuritynotification() {
        String value = "true";
        String lvalue = "false";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE SecurityNotification SET notified=trim('" + value + "') where notified=trim('" + lvalue + "')";
        Cursor cur = db.rawQuery(sql, null);
        Log.d("thor", String.valueOf(cur.getCount()));
        return cur;

    }

    public Cursor getUserData(String VehicleNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("Vehicleno", VehicleNumber.trim());
        String sql = "SELECT * FROM NRVisitorsLog where VehicleNumber =trim('" + VehicleNumber + "')";
        Cursor cur = db.rawQuery(sql, null);

        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public int getUserCount(String VehicleNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("Vehicleno", VehicleNumber.trim());
        String sql = "SELECT * FROM NRVisitorsLog where VehicleNumber =trim('" + VehicleNumber + "')";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            return cur.getCount();
        } else {
            return 0;
        }

    }

    public String getVehicleNumberbyMemberID(int memberid) {
        String name = "Empty";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT VehicleNumber from OyeMembers where MemberID=trim('" + memberid + "')";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            if (cur.getString(0) != null) {
                name = cur.getString(0);
            }
        }
        cur.close();
        return name;

    }

    /*FamilyMembers(oyeFamilyMemberID integer primary key , " +
                " associationID integer , oyeUnitID integer, firstName VARCHAR(20),  lastName VARCHAR(20), mobileNumber VARCHAR(20), " +
                " PhotoID integer, visitorType VARCHAR(40), Active integer, " +
                " createdDate VARCHAR(20) , AadharNumber VARCHAR(20) , PhotoArray BLOB, vehicleNumber VARCHAR(12), VisitorCount integer) ";
        ); */
/*"oyeFamilyMemberID": 5,         "firstName": "Billu",        "lastName": "B",
                "mobileNumber": "+919494664646",        "PhotoID": "System.Byte[]",         "visitorType": "Family",
                "AadharNumber": "",         "associationID": 12,         "oyeUnitID": 10,
                "CreateDate": "2018-05-10T17:20:54"*/
    public void insertFamilyMembers(int OYEFamilyMemberID, int AssociationID, int OYEUnitID, int MemberID,
                                    String FirstName, String LastName, String MobileNumber, String VisitorType,
                                    String AadharNumber, String CreatedDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("OYEFamilyMemberID", OYEFamilyMemberID);
        initialValues.put("AssociationID", AssociationID);

//        initialValues.put("MemberID",MemberID);
        initialValues.put("OYEUnitID", OYEUnitID);
        initialValues.put("FirstName", FirstName);
        initialValues.put("LastName", LastName);
        initialValues.put("MobileNumber", MobileNumber);
        initialValues.put("VisitorType", VisitorType);

        initialValues.put("AadharNumber", AadharNumber);
        initialValues.put("CreatedDate", CreatedDate);


        Cursor cursor = db.rawQuery("SELECT * FROM FamilyMembers  where OYEFamilyMemberID=" + OYEFamilyMemberID
                + "   ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" FamilyMembers", " updated " + OYEFamilyMemberID + " " + AadharNumber);
            db.update("FamilyMembers", initialValues, "OYEFamilyMemberID=" + OYEFamilyMemberID
                    + " ", null);
            Log.d("Dgddfdf check w", "update ");

        } else {
            Log.d(" FamilyMembers", " inserted " + OYEFamilyMemberID);
            db.insert("FamilyMembers", null, initialValues);
            Log.d("Dgddfdf check w", "inserted ");
        }
        cursor.close();

    }

    public void insertFamilyMembers_temp(int OYEFamilyMemberID, int AssociationID, int OYEUnitID, int MemberID,
                                         String FirstName, String LastName, String MobileNumber, String VisitorType,
                                         String AadharNumber, String CreatedDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("OYEFamilyMemberID", OYEFamilyMemberID);
        initialValues.put("AssociationID", AssociationID);

//        initialValues.put("MemberID",MemberID);
        initialValues.put("OYEUnitID", OYEUnitID);
        initialValues.put("FirstName", FirstName);
        initialValues.put("LastName", LastName);
        initialValues.put("MobileNumber", MobileNumber);
        initialValues.put("VisitorType", VisitorType);

        initialValues.put("AadharNumber", AadharNumber);
        initialValues.put("CreatedDate", CreatedDate);

        Cursor cursor = db.rawQuery("SELECT * FROM FamilyMembers where OYEFamilyMemberID=" + OYEFamilyMemberID
                + "   ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d("Dgddfdf TempFamil", " updated " + OYEFamilyMemberID + " " + AadharNumber);
            db.update("TempFamilyMembers", initialValues, "OYEFamilyMemberID=" + OYEFamilyMemberID
                    + " ", null);

        } else {
            Log.d("Dgddfdf TempFamilyM", " inserted " + OYEFamilyMemberID);
            db.insert("TempFamilyMembers", null, initialValues);
        }
        cursor.close();

    }

    public Cursor getAllTempFamilyMemberID() {
        String str = "HEKKI ABSBSB";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM TempFamilyMembers where FirstName='HEKKI ABSBSB'";
//        String sql ="SELECT * FROM TempFamilyMembers";
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Dgddfd tempmemID 315", " all count " + cur.getCount() + " ");
        return cur;

    }

    public Cursor getAllFIngerTempByMemID(int memID) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM TempFinger where username=" + memID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Dgddfdf AllTempFIngers", " all count " + cur.getCount() + " " + sql);
        return cur;

    }

    public Cursor getAllFinger() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM TempFinger ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;

    }

    public String getParkingSlotNumber(int slotnumber) {
        String name = "Empty";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT ParkingSlotNumber from OyeUnit where UnitID=trim('" + slotnumber + "')";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            if (cur.getString(0) != null) {
                name = cur.getString(0);
            }
        }
        cur.close();
        return name;

    }

    public Cursor getFamilyMembers_byID(int OYEFamilyMemberID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM FamilyMembers where OYEFamilyMemberID=" + OYEFamilyMemberID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;

    }

    public String getFamMemName_byID(int OYEFamilyMemberID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT FirstName, LastName FROM FamilyMembers where OYEFamilyMemberID=" + OYEFamilyMemberID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(0) + " " + cur.getString(1);
        }
        cur.close();
        return name;
    }

    public String getFamValidate(int memberId) {
        String visitors = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MemberType FROM Invitedvisitorlocal where MemberType!='Invited' and   OYEFamilyMemberID=" + memberId;
        Log.d(" value907", " all count " + sql);
        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            visitors = cur.getString(cur.getColumnIndex("MemberType"));
            Log.d(" value909", " all count " + cur.getCount() + " " + sql);
        }
        cur.close();
        return visitors;
    }

    public int getFamDailyHelp(int memberid) {
        int member = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT OYEFamilyMemberID FROM FamilyMembers where  OYEFamilyMemberID=" + memberid;
        Log.d(" value923", " all count " + sql);
        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            member = cur.getInt(cur.getColumnIndex("OYEFamilyMemberID"));
            Log.d(" value927", " all count " + cur.getCount() + " " + sql);
        }
        cur.close();
        return member;

    }

    public String getFamMemMobile_byID(int OYEFamilyMemberID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MobileNumber FROM FamilyMembers where OYEFamilyMemberID=" + OYEFamilyMemberID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315_Number", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(0);
        }
        cur.close();
        return name;
    }

    public int getRVLogID(Integer VirtualID, String EntryDateTime) {
        int RegVisitorLogID = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RegularVisitorsLog where VirtualID=" + VirtualID + " " +
                " and EntryDateTime like '%" + EntryDateTime + "%' and ExitGuardID=0 ", null);
        Log.d("count450", cursor.getCount() + "");
        Log.d("count451", +VirtualID + " " + EntryDateTime);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            RegVisitorLogID = cursor.getInt(cursor.getColumnIndex("RegVisitorLogID"));
        }
        cursor.close();
        return RegVisitorLogID;
    }

    public void UpdateIncidentStatus(Integer IncidentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String s = "Resolved";
        Log.d("TAG", "Table updated");
        // db= SQLiteDatabase.openDatabase("data/data/com.example.schduled_messages/SMS_Schdule_Sample.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL("UPDATE IncidentReport SET Status='" + s + "' WHERE IncidentID=" + IncidentID + "");
    }

    public String getStatus_IncidentReport(int IncidentID, String IncidentReportStatus) {
        String unitName = "";
        String status = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Status FROM IncidentReport where IncidentID=" + IncidentID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value318", " all count " + cursor.getCount() + " " + IncidentID + " " + IncidentReportStatus);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                unitName = cursor.getString(0);
                status = cursor.getString(cursor.getColumnIndex("Status"));
                Log.d("status", status);
            }
        }
        cursor.close();
        return status;
    }

    public boolean getRVLog(Integer memberId, String EntryDateTime) {
        boolean available = false;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RegularVisitorsLog where VirtualID=" + memberId + " " +
                " and EntryDateTime like '%" + EntryDateTime + "%' and ExitGuardID=0 ", null);
        Log.d("checkit 2502", cursor.getCount() + "");
        Log.d("checkit 2503", +memberId + " " + EntryDateTime);

        if (cursor.getCount() > 0) {
            available = true;
            cursor.getCount();
//            RegVisitorLogID= cursor.getInt(cursor.getColumnIndex("RegVisitorLogID"));

        }
        cursor.close();
        return available;

    }

    public void updateRegularVisitorLog_exitguard(Integer RegVisiorID, String ExitDateTime, Integer ExitGuardID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("ExitDateTime", ExitDateTime);
        initialValues.put("ExitGuardID", ExitGuardID);

        Cursor cursor = db.rawQuery("SELECT * FROM RegularVisitorsLog where RegVisitorLogID=" + RegVisiorID
                + "   ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value503", "RegularVisitorsLog updated " + ExitDateTime + " " + RegVisiorID);
            db.update("RegularVisitorsLog", initialValues, "RegVisitorLogID=" + RegVisiorID
                    + "  ", null);
        } else {
            Log.d(" value507", " RegularVisitorsLog inserted " + RegVisiorID + " " + ExitDateTime);
        }
        cursor.close();
    }

    public void deleteRegularVisitorLog_onexit(Integer RegVisiorID) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete  FROM RegularVisitorsLog where RegVisitorLogID=" + RegVisiorID;

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "Log one deleted ");
    }

    public int getFamMemID_byPhoneNumber(String MobileNumber) {
        Log.d("Value426", "loop entered");
        int famMemID = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT OYEFamilyMemberID FROM FamilyMembers where MobileNumber='" + MobileNumber + "' ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Value432", " loop " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            famMemID = cur.getInt(cur.getColumnIndex("OYEFamilyMemberID"));
            Log.d("Value436", String.valueOf(famMemID));
        }
        Log.d("Value440", String.valueOf(famMemID));
        cur.close();
        return famMemID;
    }

    public Cursor getFamily_byPhoneNumber(String MobileNumber, int AssociationID) {
        Log.d("Value426", "loop entered");

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM FamilyMembers where MobileNumber='" + MobileNumber + "' and AssociationID=" + AssociationID;
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Value432", " loop " + cur.getCount() + " ");

        return cur;
    }

    public Cursor getOyeMembers_byPhoneNumber(String MobileNumber, int AssociationID) {
        Log.d("Value426", "loop entered");

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where MobileNumber='" + MobileNumber + "' and AssociationID=" + AssociationID;
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Value432", " loop " + cur.getCount() + " ");

        return cur;
    }

    public boolean assnFamMember_phonenumber_exist(String MobileNumber) {
        Log.d("Value426", "loop entered");
        boolean present = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM FamilyMembers where VisitorType='Family' and MobileNumber='" + MobileNumber + "' ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Value432", " loop " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            present = true;
            Log.d("Value436", String.valueOf(present));
        }
        Log.d("Value440", String.valueOf(present));
        cur.close();
        return present;

    }

    /*RegularVisitorsLog(RegVisitorLogID integer primary key, " +
                " VirtualID integer, associationID integer , UnitID integer, visitorType VARCHAR(20), " +
                " VisitorCount integer,  PhotoID integer, VehiclePhotoID integer, ParcelPhotoID integer , " +
                " EntryDateTime VARCHAR(20), ExitDateTime VARCHAR(20),  entryGuardID integer, exitGuardID integer, " +
                " vehicleNumber VARCHAR(20), vehicleType VARCHAR(20),  itemCount integer, " +
                " updatedDate VARCHAR(20) , Offline integer )*/

    public boolean nonFamMember_phonenumber_exist(String MobileNumber) {
        Log.d("Value426", "loop entered");
        boolean present = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM FamilyMembers where VisitorType!='Family' and MobileNumber='" + MobileNumber + "' ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" Value432", " loop " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            present = true;
            Log.d("Value436", String.valueOf(present));
        }
        Log.d("Value440", String.valueOf(present));
        cur.close();
        return present;

    }

    public void deleteAll_UnitFamilyMembers(int AssociationID, int OYEUnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete  FROM FamilyMembers where AssociationID=" + AssociationID + " and OYEUnitID=" + OYEUnitID;

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "FamilyMembers deleted ");
    }

    /*  jsonObject1.getInt("associationID"), jsonObject1.getInt("oyeUnitID"),
                                            jsonObject1.getString("regularVisitorType"), jsonObject1.getString("firstName"),
        "", "",//jsonObject1.getString("mobileNumber"),   "", jsonObject1.getString("regularVisitorType"),
       jsonObject1.getString("entryTime"), jsonObject1.getString("vehicleNumber"), jsonObject1.getString("vehicleType"), "",//jsonObject1.getInt("itemCount"),
//       jsonObject1.getBlob("Photo"),jsonObject1.getBlob("VehiclePhoto"),jsonObject1.getBlob("ItemPhoto"),
                                            null,null,null,jsonObject1.getInt("oyeRegularVisitorID"),
                                            jsonObject1.getString("exitTime"),jsonObject1.getInt("entryGuardID"),
                                            false,jsonObject1.getInt("oyeFamilyMemberID") ,jsonObject1.getInt("exitGuardID")*/
    public void insertRegularVisitorsLogSync(int AssociationID, int UnitID, String VisitorType,
                                             String EntryTime, String VehicleNumber, String VehicleType, int OYERegularVisitorID,
                                             String ExitTime, int EntryGuardID, int OYEFamilyMemberID, int ExitGuardID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("UnitID", UnitID);
        initialValues.put("VisitorType", VisitorType);
        initialValues.put("EntryDateTime", EntryTime);
        initialValues.put("VehicleNumber", VehicleNumber);
        initialValues.put("VehicleType", VehicleType);
        initialValues.put("RegVisitorLogID", OYERegularVisitorID);
        initialValues.put("ExitDateTime", ExitTime);
        initialValues.put("EntryGuardID", EntryGuardID);
        initialValues.put("VirtualID", OYEFamilyMemberID);
        initialValues.put("ExitGuardID", ExitGuardID);

        Cursor cursor = db.rawQuery("SELECT * FROM RegularVisitorsLog  where RegVisitorLogID=" + OYERegularVisitorID
                + "  and   UnitID=" + UnitID + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated ");
            db.update("RegularVisitorsLog", initialValues, "RegVisitorLogID=" + OYERegularVisitorID
                    + " and   UnitID=" + UnitID + " ", null);

        } else {
            Log.d(" value", " inserted " + OYERegularVisitorID);
            db.insert("RegularVisitorsLog", null, initialValues);
        }
        cursor.close();
    }

    public void delete_old300_log_regular(int delete_count) {
        getAllRegularVisitorLogCount();
        //  Delete from table_name where rowid IN (Select rowid from table_name limit X);
        SQLiteDatabase db = this.getReadableDatabase();
        //   String sql="DELETE FROM ResidentVehicles where id IN(SELECT OYEVehicleId from ResidentVehicles ORDER BY OYEVehicleId ASC) LIMIT 5";

        String sql = "DELETE FROM RegularVisitorsLog WHERE RegVisitorLogID IN ( SELECT RegVisitorLogID FROM RegularVisitorsLog ORDER BY RegVisitorLogID ASC LIMIT '" + delete_count + "' )";

        //   String ALTER_TBL ="delete from " + "ResidentVehicles" + " where OYEVehicleId IN (Select TOP  OYEVehicleId from " + "ResidentVehicles" + ")";
        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" vehicle123  ", "oyevehicles deleted ");
    }

    public int getAllRegularVisitorLogCount() {
        int log_count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RegularVisitorsLog";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("darling main", cursor.getCount() + " " + sql);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getCount();
        } else return log_count;
    }

    public int getDashboardCount() {
        int log_count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM countereference";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("darling main", cursor.getCount() + " " + sql);
        //cursor.moveToFirst();
//        if(cursor.getCount()>0){
//            return cursor.getCount();
//        }
//        else
        return log_count;
    }

    public void delete_old300_log_nonregular(int delete_count) {
        getAllNonRegularVisitorLogCount();
        //  Delete from table_name where rowid IN (Select rowid from table_name limit X);
        SQLiteDatabase db = this.getReadableDatabase();
        //   String sql="DELETE FROM ResidentVehicles where id IN(SELECT OYEVehicleId from ResidentVehicles ORDER BY OYEVehicleId ASC) LIMIT 5";

        String sql = "DELETE FROM NRVisitorsLog WHERE NRVisitorLogID IN ( SELECT NRVisitorLogID FROM NRVisitorsLog ORDER BY NRVisitorLogID ASC LIMIT '" + delete_count + "' )";

        //   String ALTER_TBL ="delete from " + "ResidentVehicles" + " where OYEVehicleId IN (Select TOP  OYEVehicleId from " + "ResidentVehicles" + ")";
        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" vehicle124  ", "oyevehicles deleted ");
    }

    public int getAllNonRegularVisitorLogCount() {
        int log_count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM NRVisitorsLog";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("darling main", cursor.getCount() + " " + sql);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getCount();
        } else return log_count;
    }

    public Cursor getAttendanceReport(Integer assid, String date) {

        String[] name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Attendance where AssociationID=" + assid + " and StartDate='" + date + "' order by AttendanceID desc ";
        Cursor cursor = db.rawQuery(sql, null);
        name = new String[cursor.getCount()];
        Log.d("darling main", cursor.getCount() + " " + sql);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
//                if(getAttend(1,cursor.getInt(cursor.getColumnIndex("AssociationID")))>0);
            return cursor;
        } else return cursor;

//        if(cursor.getCount()>0){
//
//        {
//            int i=0;
//
//            do {
//                name[i]= cursor.getString(cursor.getColumnIndex("StartDate"))+","+cursor.getString(cursor.getColumnIndex("StartTime"))+","+cursor.getString(cursor.getColumnIndex("EndTime"));
//
////                    name[i] = cur.getString(cur.getColumnIndex("MemberType"))==null ? " hi":cur.getString(cur.getColumnIndex("MemberType"));
////                name[i] =getSecurityAttendancename(1)+" "+getAttendance(1);
//                Log.d("checkit 3984",name[i]+" "+getSecurityName(1));
//                i++;
//            }while (cursor.moveToNext());
//        }


    }

    public String getDailyHelpName(Integer famid) {
        String accountName = "";
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM FamilyMembers where OYEFamilyMemberID=" + famid + " ";

        Cursor cursor = db.rawQuery(sql, null);
        Log.d("checkit 2909", String.valueOf(cursor.getCount()));

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            accountName = cursor.getString(cursor.getColumnIndex("FirstName")) + " " + cursor.getString(cursor.getColumnIndex("LastName"));
            Log.d("loosu 2915", accountName);
        }
        cursor.close();

        return accountName;
    }

    public Cursor getDailyHelpAttendanceReport(Integer assid, String date) {

        String[] name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RegularVisitorsLog where AssociationID=" + assid + " and  EntryDateTime like '%" + date + "%'  ";
        Cursor cursor = db.rawQuery(sql, null);
        name = new String[cursor.getCount()];
        Log.d("loosu main", cursor.getCount() + " " + sql);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
//                if(getAttend(1,cursor.getInt(cursor.getColumnIndex("AssociationID")))>0);
            return cursor;
        } else return cursor;

//        if(cursor.getCount()>0){
//
//        {
//            int i=0;
//
//            do {
//                name[i]= cursor.getString(cursor.getColumnIndex("StartDate"))+","+cursor.getString(cursor.getColumnIndex("StartTime"))+","+cursor.getString(cursor.getColumnIndex("EndTime"));
//
////                    name[i] = cur.getString(cur.getColumnIndex("MemberType"))==null ? " hi":cur.getString(cur.getColumnIndex("MemberType"));
////                name[i] =getSecurityAttendancename(1)+" "+getAttendance(1);
//                Log.d("checkit 3984",name[i]+" "+getSecurityName(1));
//                i++;
//            }while (cursor.moveToNext());
//        }

    }

    public Cursor getDailyHelpAttendanceReport(Integer assid) {

        String[] name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RegularVisitorsLog where AssociationID=" + assid + "  ";
        Cursor cursor = db.rawQuery(sql, null);
        name = new String[cursor.getCount()];
        Log.d("loosu main", cursor.getCount() + " " + sql);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
//                if(getAttend(1,cursor.getInt(cursor.getColumnIndex("AssociationID")))>0);
            return cursor;
        } else return cursor;

//        if(cursor.getCount()>0){
//
//        {
//            int i=0;
//
//            do {
//                name[i]= cursor.getString(cursor.getColumnIndex("StartDate"))+","+cursor.getString(cursor.getColumnIndex("StartTime"))+","+cursor.getString(cursor.getColumnIndex("EndTime"));
//
////                    name[i] = cur.getString(cur.getColumnIndex("MemberType"))==null ? " hi":cur.getString(cur.getColumnIndex("MemberType"));
////                name[i] =getSecurityAttendancename(1)+" "+getAttendance(1);
//                Log.d("checkit 3984",name[i]+" "+getSecurityName(1));
//                i++;
//            }while (cursor.moveToNext());
//        }


    }

    public Cursor getRegVisitorsLog(String DateYMD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RegularVisitorsLog where EntryDateTime like ('%" + DateYMD + "%') and ExitDateTime='0001-01-01T00:00:00'  " +
                " or ExitDateTime='0001-01-01T00:00:00' or ExitDateTime is null ";//where EntryDateTime like ('%"+DateYMD+"%')

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " " + DateYMD);
        return cur;
    }

    public void delete_TodaysRVLog(int AssociationID, String EntryDateTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete  FROM RegularVisitorsLog where AssociationID=" + AssociationID + " and EntryDateTime like '%" + EntryDateTime + "%'";

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "FamilyMembers deleted ");
    }

    public Cursor getInvitedMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM FamilyMembers where VisitorType='Invited' ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;

    }

    public Cursor getFamilyMembers_byUnit(int UnitID, String VisitorType) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM FamilyMembers where VisitorType=('" + VisitorType + "') and UnitID=" + UnitID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getDailyHelps() {
        SQLiteDatabase db = this.getReadableDatabase();
//        String sql ="SELECT * FROM FamilyMembers where VisitorType=('"+DAILY_HELP+"') or VisitorType='School Bus'  " ;

        String sql = "SELECT * FROM FamilyMembers";// where VisitorType=('"+DAILY_HELP+"') or VisitorType='School Bus'  " ;
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value31558", " all count " + cur.getCount() + " ");
        return cur;
    }

    public void update_FamilyMemberTable(int oyeFamilyMemID, String fname, String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
//        String sql ="SELECT * FROM FamilyMembers where VisitorType=('"+DAILY_HELP+"') or VisitorType='School Bus'  " ;

        String sql = "UPDATE FamilyMembers SET FirstName='" + fname + "' and MobileNumber='" + mobile + "' where OYEFamilyMemberID=" + oyeFamilyMemID;
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value420", " all count " + cur.getCount() + " " + sql);

    }

    public Cursor getIncidentReportTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM IncidentReport";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public void IncidentReportValues(Integer IncidentID, Integer GuardID, Integer AssociationID,
                                     String UnitName, String IncidentCategoryID, String IncidentDetails,
                                     String IncidentGPS,
                                     String DateTime, Integer PhotoID,
                                     Integer AudioID, String Status, String assignedTo, String eta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("IncidentID", IncidentID);
        initialValues.put("GuardID", GuardID);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("UnitName", UnitName);
        initialValues.put("IncidentCategoryID", IncidentCategoryID);
        initialValues.put("IncidentDetails", IncidentDetails);
        initialValues.put("IncidentGPS", IncidentGPS);
        initialValues.put("DateTime", DateTime);
        initialValues.put("PhotoID", PhotoID);
        initialValues.put("AudioID", AudioID);
        initialValues.put("Status", Status);
        initialValues.put("assignedTo", assignedTo);
        initialValues.put("eta", eta);

        Cursor cur1 = db.rawQuery("SELECT * FROM IncidentReport where IncidentID=" + IncidentID + " ", null);
        Log.d("IncidentReport", cur1.getCount() + " ");
        if (cur1.getCount() > 0) {
            Log.d(" IncidentReport", " updated " + IncidentID + " " + Status + " " + IncidentDetails);
            db.update("IncidentReport", initialValues, "IncidentID=" + IncidentID + " ", null);
        } else {
            Log.d(" IncidentReport", " inserted " + IncidentID + " " + " " + Status + " " + IncidentDetails);
            db.insert("IncidentReport", null, initialValues);
        }
        cur1.close();
    }

    public void IncidentReportValues(Integer IncidentID, Integer GuardID, Integer AssociationID,
                                     String UnitName, String IncidentCategoryID, String IncidentDetails,
                                     String IncidentGPS,
                                     String DateTime, Integer PhotoID,
                                     Integer AudioID, String Status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("IncidentID", IncidentID);
        initialValues.put("GuardID", GuardID);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("UnitName", UnitName);
        initialValues.put("IncidentCategoryID", IncidentCategoryID);
        initialValues.put("IncidentDetails", IncidentDetails);
        initialValues.put("IncidentGPS", IncidentGPS);
        initialValues.put("DateTime", DateTime);
        initialValues.put("PhotoID", PhotoID);
        initialValues.put("AudioID", AudioID);
        initialValues.put("Status", Status);

        Cursor cur1 = db.rawQuery("SELECT * FROM IncidentReport where IncidentID=" + IncidentID + " ", null);
        Log.d("IncidentReport", cur1.getCount() + " ");
        if (cur1.getCount() > 0) {
            Log.d(" IncidentReport", " updated " + IncidentID + " " + Status + " " + IncidentDetails);
            db.update("IncidentReport", initialValues, "IncidentID=" + IncidentID + " ", null);
        } else {
            Log.d(" IncidentReport", " inserted " + IncidentID + " " + " " + Status + " " + IncidentDetails);
            db.insert("IncidentReport", null, initialValues);
        }
        cur1.close();
    }

    public int getPendingIncidentCount(int AssociationID) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM IncidentReport where AssociationID=" + AssociationID + " and Status like '%Pending%'";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value318", " all count " + cursor.getCount() + " " + AssociationID + " ");
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getResolvedIncidentCount(int AssociationID) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM IncidentReport where AssociationID=" + AssociationID + " and Status like '%Resolved%'";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value318", " all count " + cursor.getCount() + " " + AssociationID + " ");
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public String getStaus_IncidentReport(int IncidentID, String IncidentReportStatus) {
        String unitName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Status FROM IncidentReport where IncidentID=" + IncidentID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value318", " all count " + cursor.getCount() + " " + IncidentID + " " + IncidentReportStatus);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                unitName = cursor.getString(0);
            }
        }
        cursor.close();
        return unitName;
    }

    public String getIncidentStatus(int IncidentID) {
        String unitName = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Status FROM IncidentReport where IncidentID=" + IncidentID + "";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Log.d(" value318emer", " all count " + cursor.getCount() + " " + IncidentID + " " + cursor.getString(0));
            if (cursor.getString(0) != null) {
                unitName = cursor.getString(0).trim();
            }
        } else {
//            Log.d(" value318emer"," else  "+cursor.getCount()+" "+incidentID+" "+cursor.getString(0));
        }
        cursor.close();
        return unitName;
    }

    public String getIncidentUnitName(int IncidentID) {
        String UnitName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT UnitName FROM IncidentReport where IncidentID=" + IncidentID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value318", " all count " + cursor.getCount() + " " + IncidentID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                UnitName = cursor.getString(0);
            }
        }
        cursor.close();
        return UnitName;
    }

    public int getIncidentCreatedGuardID(int IncidentID) {
        int GuardID = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT GuardID FROM IncidentReport where IncidentID=" + IncidentID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value318", " all count " + cursor.getCount() + " " + IncidentID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                GuardID = cursor.getInt(0);
            }
        }
        cursor.close();
        return GuardID;
    }

    public Cursor IncidentCount43(int AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM IncidentReport where AssociationID=" + AssociationID;
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value3181111", " all count " + cursor.getCount() + " " + AssociationID + " ");
        return cursor;
    }

    public String getRegMemEndDate(int VirtualID, int UnitID) {
        String unitName = "NA";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT max(EndDate) FROM RegularVisitors where VirtualID=" + VirtualID + " and UnitID=" + UnitID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cursor.getCount() + " " + VirtualID + " " + UnitID);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                unitName = cursor.getString(0);
            }
        }
        cursor.close();
        return unitName;
    }

    public long insertRegularVisitorsValidity(int VirtualID, int UnidID, String StartDate, String EndDate, String WorkStartTime, String WorkEndTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put("VirtualID", VirtualID);
        initialValues.put("UnitID", UnidID);
        initialValues.put("StartDate", StartDate);
        initialValues.put("EndDate", EndDate);
        initialValues.put("WorkStartTime", WorkStartTime);
        initialValues.put("WorkEndTime", WorkEndTime);
        Cursor cursor = db.rawQuery("SELECT * FROM RegularVisitors where StartDate =trim('" + StartDate + "') and VirtualID=" + VirtualID + " ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", "RegularVisitors updated " + StartDate + EndDate + " " + VirtualID);
            cursor.close();
            return -1;
        } else {
            Log.d(" value", "RegularVisitors inserted " + StartDate + EndDate + " " + VirtualID + " " + UnidID);
            cursor.close();
            return db.insert("RegularVisitors", null, initialValues);
        }

    }

    /* VisitorApprovals(VisitorApprovalID integer primary key, " +
                " NRVisitorID integer, MemberID integer , UnitID integer, " +
                " ApprovalDateTime VARCHAR(20) , PermitType VARCHAR(20)*/
    public void VisitorApproval(Integer NRVisitorID, Integer MemberID, Integer UnitID,
                                String ApprovalDateTime, String PermitType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("NRVisitorID", NRVisitorID);
        initialValues.put("MemberID", MemberID);
        initialValues.put("UnitID", UnitID);
        initialValues.put("ApprovalDateTime", ApprovalDateTime);
        initialValues.put("PermitType", PermitType);

        Cursor cursor = db.rawQuery("SELECT * FROM VisitorApprovals where NRVisitorID=" + NRVisitorID
                + " and   UnitID=" + UnitID + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", "VisitorApprovals updated " + NRVisitorID + " " + UnitID);
            db.update("VisitorApprovals", initialValues, "NRVisitorID=" + NRVisitorID
                    + " and   UnitID=" + UnitID, null);
        } else {
            Log.d(" value", " inserted " + NRVisitorID + " " + UnitID);
            db.insert("VisitorApprovals", null, initialValues);
        }
        cursor.close();

    }

    public String getStatus_VisitorApproval(int NRVisitorID, int UnitID) {
        String unitName = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT PermitType FROM VisitorApprovals where NRVisitorID=" + NRVisitorID + " and UnitID=" + UnitID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cursor.getCount() + " " + NRVisitorID + " " + UnitID);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                unitName = cursor.getString(0);
            }
        }
        cursor.close();
        return unitName;
    }

    public void ApprovedVisitorEntry(Integer NRVisitorID, Integer EntryGuardID,
                                     String EntryDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("EntryDateTime", EntryDateTime);
        initialValues.put("EntryGuardID", EntryGuardID);
        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID
                + "   ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", "NRVisitorsLog updated " + NRVisitorID + " ");
            db.update("NRVisitorsLog", initialValues, "NRVisitorLogID=" + NRVisitorID
                    + "  ", null);
        } else {
            Log.d(" value", " inserted " + NRVisitorID + " ");
//            return db.insert("NRVisitorsLog", null, initialValues);

        }
        cursor.close();

    }

    public void ApprovedVisitorExit(Integer NRVisitorID, Integer ExitGuardID,
                                    String ExitDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("ExitDateTime", ExitDateTime);
        initialValues.put("ExitGuardID", ExitGuardID);
        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID
                + "   ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", "NRVisitorsLog updated " + NRVisitorID + " ");
            db.update("NRVisitorsLog", initialValues, "NRVisitorLogID=" + NRVisitorID
                    + "  ", null);
        } else {
            Log.d(" value", " inserted " + NRVisitorID + " ");
//            return db.insert("NRVisitorsLog", null, initialValues);
        }
        cursor.close();

    }

    public void deleteNonRegularVisitorLog_onexit(Integer NRVisitorID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete  FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID;

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "Log two deleted ");

    }

    public Cursor getFamValidate_byUnit_DailyHelp_old(int UnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Invitedvisitorlocal where MemberType!='Invited' and  OYEUnitID=" + UnitID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;

    }

    public String getDailyHelps_UnitsList(int OYEFamilyMemberID) {
        String unitNames = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Invitedvisitorlocal where OYEFamilyMemberID=" + OYEFamilyMemberID + " ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                unitNames += getUnitName(cur.getInt(cur.getColumnIndex("OYEUnitID"))) + ",";
                Log.d(" valueInvitedLocal", cur.getString(cur.getColumnIndex("StartDate")) + "endDate" + cur.getString(cur.getColumnIndex("EndDate")));
            } while (cur.moveToNext());
        }


        cur.close();
        return unitNames;

    }

    public boolean getNRVisitors_Phone_exits(String DateYMD, String mobileNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM NRVisitorsLog where EntryDateTime like ('%" + DateYMD + "%') and ExitDateTime='0001-01-01T00:00:00' and MobileNumber= '" + mobileNumber + "'";
        //   " or ExitDateTime='0001-01-01T00:00:00' or ExitDateTime is null and MobileNumber= '"+mobileNumber+"' ";//where EntryDateTime like ('%"+DateYMD+"%')


        Log.d("AAAA", sql);
        Cursor cur = db.rawQuery(sql, null);

        return cur.getCount() > 0;
    }

    public String getDailyHelpVisitorStartDate(int memberId) {
        String details = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT StartDate FROM Invitedvisitorlocal where   OYEFamilyMemberID=" + memberId; //
        Cursor cursor = db.rawQuery(sql, null);
        //Log.d("hvsdvk"," all count "+cursor.getCount()+" "+GuardID+" ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                details = cursor.getString(0);
            }
        }
        cursor.close();
        return details;
    }

    public String getDailyHelpVisitorStartEnd(int memberId) {
        String details = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT EndDate FROM Invitedvisitorlocal where   OYEFamilyMemberID=" + memberId; //
        Cursor cursor = db.rawQuery(sql, null);
        //Log.d("hvsdvk"," all count "+cursor.getCount()+" "+GuardID+" ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                details = cursor.getString(0);
            }
        }
        cursor.close();
        return details;
    }

    public String getFamilyMemberTypebyid(int famid) {
        String name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MemberType FROM Invitedvisitorlocal where OYEFamilyMemberID =trim('" + famid + "') ";
        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            if (cur.getString(0) != null) {
                name = cur.getString(0);
            }
        }
        cur.close();
        return name;
    }

    public void familymembeslistbyid(int Mtypeid, String membertype, String sdate, String edate, String wstime, String wetime, int Oyefamid, int assid, int OYEunitid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("MTypeID", Mtypeid);
        initialValues.put("MemberType", membertype);
        initialValues.put("StartDate", sdate);
        initialValues.put("EndDate", edate);
        initialValues.put("WorkStartTime", wstime);
        initialValues.put("WorkEndTime", wetime);
        initialValues.put("OYEFamilyMemberID", Oyefamid);
        initialValues.put("AssociationID", assid);
        initialValues.put("OYEUnitID", OYEunitid);
        Cursor cursor = db.rawQuery("SELECT * FROM Invitedvisitorlocal where MTypeID=trim('" + Mtypeid + "')", null);
        Log.d("count1969", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Log.d(" value", "Invitedvisitorlocal updated " + Mtypeid + " " + OYEunitid);
            db.update("Invitedvisitorlocal", initialValues, "MTypeID=trim('" + Mtypeid + "')", null);
            cursor.getInt(0);
        } else {
            Log.d(" value", "Invitedvisitorlocal inserted " + Mtypeid + " " + membertype);
            db.insert("Invitedvisitorlocal", null, initialValues);
        }
        cursor.close();
    }

    /*NRVisitorLogID integer primary key, " +
                " associationID integer , UnitID integer, visitorType VARCHAR(20), " +
                " firstName VARCHAR(20), lastName VARCHAR(20) , mobileNumber VARCHAR(20) not null,  "+
                " VisitorCount integer,  PhotoID integer, VehiclePhotoID integer, ParcelPhotoID integer , " +
                " serviceProviderName VARCHAR(20), purpose VARCHAR(20) , "+
                " EntryDateTime VARCHAR(20), ExitDateTime VARCHAR(20),  entryGuardID integer, exitGuardID integer, " +
                " vehicleNumber VARCHAR(20),RegType VARCHAR(20), vehicleType VARCHAR(20),  itemCount integer, " +
                " updatedDate VARCHAR(20) , Offline integer */
    public long insertNRVisitorsLogs(int OYENonRegularVisitorID, Integer AssociationID, Integer UnitID, String VisitorType,
                                     String Fname, String Lname, String mobilenumber, Integer VisitorCount,
                                     String serviceProviderName,
                                     String EntryDatetime, Integer EntryGuardID, String Vehiclenumber, String VehicleType,
                                     Integer Itemcount, String Updatetime, Integer offline, String UnitNames, String purpose) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("NRVisitorLogID", OYENonRegularVisitorID);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("UnitID", UnitID);
        initialValues.put("VisitorType", VisitorType);
        initialValues.put("FirstName", Fname);
        initialValues.put("LastName", Lname);
        initialValues.put("MobileNumber", mobilenumber);
        initialValues.put("VisitorCount", VisitorCount);
//        initialValues.put("PhotoID",Photoid);
//        initialValues.put("VehiclePhotoID",VehiclePhotoid);
//        initialValues.put("ParcelPhotoID",ParcelPhotoid);
        initialValues.put("ServiceProviderName", serviceProviderName);
        initialValues.put("EntryDateTime", EntryDatetime);
        initialValues.put("ExitDateTime", "0001-01-01T00:00:00");
        initialValues.put("EntryGuardID", EntryGuardID);
        initialValues.put("VehicleNumber", Vehiclenumber);
        initialValues.put("VehicleType", VehicleType);
        initialValues.put("ItemCount", Itemcount);
        initialValues.put("UpdatedDate", Updatetime);
        initialValues.put("Offline", offline);
        initialValues.put("UnitNames", UnitNames);
        initialValues.put("Purpose", purpose);

        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID=" + OYENonRegularVisitorID + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" NRVisitorsLogs", " updated " + AssociationID + " " + OYENonRegularVisitorID);
            cursor.close();
            return -1;
        } else {
            Log.d(" NRVisitorsLogs", " inserted " + AssociationID + " " + OYENonRegularVisitorID + " " + Vehiclenumber);
            cursor.close();
            return db.insert("NRVisitorsLog", null, initialValues);
        }

    }

    public void insertNRVisitorsLogsSync(int OYENonRegularVisitorID, Integer AssociationID, Integer UnitID, String VisitorType,
                                         String Fname, String Lname, String mobilenumber, Integer VisitorCount,
                                         String serviceProviderName, String EntryDatetime, Integer EntryGuardID,
                                         String Vehiclenumber, String VehicleType, Integer Itemcount, String Updatetime,
                                         Integer offline, String ExitDateTime, Integer ExitGuardID, String unitNames, Integer OYEMemberID,
                                         String Comment, String CommentImage, String purpose) {

        SQLiteDatabase db = this.getWritableDatabase();
//        ExitDateTime VARCHAR(20),  entryGuardID integer, exitGuardID integer
        ContentValues initialValues = new ContentValues();
        initialValues.put("NRVisitorLogID", OYENonRegularVisitorID);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("UnitID", UnitID);
        initialValues.put("VisitorType", VisitorType);
        initialValues.put("FirstName", Fname);
        initialValues.put("LastName", Lname);
        initialValues.put("MobileNumber", mobilenumber);
        initialValues.put("VisitorCount", VisitorCount);
        initialValues.put("ServiceProviderName", serviceProviderName);
        initialValues.put("EntryDateTime", EntryDatetime);
        initialValues.put("EntryGuardID", EntryGuardID);
        initialValues.put("VehicleNumber", Vehiclenumber);
        initialValues.put("VehicleType", VehicleType);
        initialValues.put("ItemCount", Itemcount);
        initialValues.put("UpdatedDate", Updatetime);
        initialValues.put("Offline", offline);
        initialValues.put("ExitDateTime", ExitDateTime);
        initialValues.put("ExitGuardID", ExitGuardID);
        initialValues.put("OYEMemberID", OYEMemberID);
        initialValues.put("Comment", Comment);
        initialValues.put("CommentImage", CommentImage);
        initialValues.put("UnitNames", unitNames);
        initialValues.put("Purpose", purpose);

        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID=" + OYENonRegularVisitorID + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" NRVisitorsLogSync", " updated " + AssociationID + " " + OYENonRegularVisitorID + " " + OYEMemberID);
            db.update("NRVisitorsLog", initialValues, "NRVisitorLogID=" + OYENonRegularVisitorID + "  ", null);
        } else {
            Log.d(" NRVisitorsLogSync", " inserted " + AssociationID + " " + OYENonRegularVisitorID + " " + OYEMemberID);
            db.insert("NRVisitorsLog", null, initialValues);
        }
        cursor.close();

    }


//    public long insertNRVisitorsLogsSync(int oyeNonRegularVisitorID,Integer associationID,Integer UnitID, String visitorType,
//                                         String Fname, String Lname,String mobilenumber,Integer VisitorCount,
//                                         String serviceProviderName,
//                                         String EntryDatetime,Integer entryGuardID,String Vehiclenumber,String vehicleType,
//                                         Integer Itemcount, String Updatetime,Integer offline,
//                                         String  ExitDateTime,Integer exitGuardID )
//    {
//        SQLiteDatabase db=this.getWritableDatabase();
////        ExitDateTime VARCHAR(20),  entryGuardID integer, exitGuardID integer
//        ContentValues initialValues = new ContentValues();
//        initialValues.put("NRVisitorLogID", oyeNonRegularVisitorID);
//        initialValues.put("associationID", associationID);
//        initialValues.put("UnitID",UnitID);
//        initialValues.put("visitorType",visitorType);
//        initialValues.put("firstName",Fname);
//        initialValues.put("lastName",Lname);
//        initialValues.put("mobileNumber",mobilenumber);
//        initialValues.put("visitorCount",visitorCount);
////        initialValues.put("PhotoID",Photoid);
//        initialValues.put("serviceProviderName",serviceProviderName);
//        initialValues.put("EntryDateTime",EntryDatetime);
//        initialValues.put("entryGuardID",entryGuardID);
//        initialValues.put("vehicleNumber",Vehiclenumber);
//        initialValues.put("vehicleType",vehicleType);
//        initialValues.put("itemCount",Itemcount);
//        initialValues.put("updatedDate",Updatetime);
//        initialValues.put("Offline",offline);
//        initialValues.put("ExitDateTime",ExitDateTime);
//        initialValues.put("exitGuardID",exitGuardID);
//
//        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID="+oyeNonRegularVisitorID+"  ", null);
//        Log.d("count",cursor.getCount()+"");
//        if(cursor.getCount() >0)
//        {
//            Log.d(" NRVisitorsLog"," updated "+associationID+" "+oyeNonRegularVisitorID);
//            cursor.close();
//            return db.update("NRVisitorsLog", initialValues,"NRVisitorLogID="+oyeNonRegularVisitorID +"  ",null );
//        }else{
//            Log.d(" NRVisitorsLog"," inserted "+associationID+" "+oyeNonRegularVisitorID);
//            cursor.close();
//            return db.insert("NRVisitorsLog", null, initialValues);
//        }
//
//    }

    public long insertCourierNotification(int CourierID, int Assid, int OYEmemID, int OYEFamID, int OYEnrVistrID,
                                          String createdDate, String Status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("CourierID", CourierID);
        initialValues.put("AssociationID", Assid);
        initialValues.put("OYENonRegularVisitorID", OYEnrVistrID);
        initialValues.put("OYEMemberID", OYEmemID);
        initialValues.put("OYEFamilyMemberID", OYEFamID);
        initialValues.put("CreateDateTime", createdDate);
        initialValues.put("ResponseText", Status);

        Cursor cursor = db.rawQuery("SELECT * FROM CourierNotification where CourierID=trim('" + CourierID + "')", null);
        if (cursor.getCount() > 0) {
            Log.d(" value", "Courier updated " + CourierID + " " + Status);
            db.update("CourierNotification", initialValues, " CourierID=trim(" + CourierID + ")", null);
            return -1;
        } else {
            Log.d(" value", "Courier inserted " + CourierID + " " + OYEmemID + " ," + Status);
            return db.insert("CourierNotification", null, initialValues);
        }
    }

    public int getUnitIDbyOYEMemberID(int oyememid, int assid) {
        int unitName = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM OYEMembers where MemberID='" + oyememid + "' and AssociationID=" + assid + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + oyememid);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            unitName = cursor.getInt(cursor.getColumnIndex("UnitID"));
        }
        cursor.close();
        return unitName;
    }

    public Cursor getNRVisitorsLog(String DateYMD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM NRVisitorsLog where EntryDateTime like ('%" + DateYMD + "%') and ExitDateTime='0001-01-01T00:00:00' " +
                " or ExitDateTime='0001-01-01T00:00:00' or ExitDateTime is null ";//where EntryDateTime like ('%"+DateYMD+"%')

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " " + DateYMD);
        return cur;
    }

    public Cursor getNRVisitorsLogTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM NRVisitorsLog ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getNRVisitorsForApproval(int UnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM NRVisitorsLog where EntryDateTime is null and UnitID=" + UnitID + " ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getNRVisitorsList_SG() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM NRVisitorsLog  ";//where EntryDateTime is null

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public boolean NRCommentDetails(Integer NRVisitorID) {
        boolean details = false;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID
                + " and  OYEMemberID!=0 and Comment!=null", null);
        Log.d("count", "NRCommentDetails " + cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            details = true;
        } else {
            Log.d(" value", " NRcomments " + NRVisitorID + " ");
        }
        cursor.close();
        return details;
    }

    public Cursor NRCommentImageDetails(Integer NRVisitorID) {
        boolean details = false;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID + "  ", null);
        Log.d("count", "NRCommentDetails " + cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            return cursor;
        } else {
            Log.d(" value", " inserted " + NRVisitorID + " ");
        }
        return null;
    }

    public Cursor NRVisitorDetails_byPhoneNum(String phoneNumber) {
        boolean details = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * from NRVisitorsLog where MobileNumber =trim('" + phoneNumber + "')";

        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", "NRCommentDetails " + cursor.getCount() + "" + "SELECT * FROM NRVisitorsLog where MobileNumber=" + phoneNumber);
        if (cursor.getCount() > 0) {
            return cursor;
        } else {
            Log.d(" value", " inserted " + phoneNumber + "not present");
        }
        return cursor;
    }

    public long insertMemberTable(int AssID, int unitid, int accountid, int parentmemberid, int memberroleid, String datecreated, String referralID, String residenttype, String rmid, String rmdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID ", AssID);
        initialValues.put("UnitID", unitid);
        initialValues.put("AccountID", accountid);
        initialValues.put("ParentMemberID", parentmemberid);
        initialValues.put("MemberRoleID", memberroleid);
        initialValues.put("CreatedDate", datecreated);
        initialValues.put("ReferalID", referralID);
        initialValues.put("ResidentType", residenttype);
        initialValues.put("RemovalMemberID", rmid);
        initialValues.put("RemovedDateTime", rmdate);
        Cursor cursor = db.rawQuery("SELECT * FROM OyeMembers where UnitID=" + unitid + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + parentmemberid + " " + memberroleid);
            cursor.close();
            return -1;
        } else {
            Log.d(" value", " inserted " + accountid + " " + memberroleid);
            cursor.close();
            return db.insert("OyeMembers", null, initialValues);
        }

    }

    /*membersList.get(i).getOYEMemberID(),
                                    membersList.get(i).getAccountID(), membersList.get(i).getAssociationID(),
                                    membersList.get(i).getOYEMemberRoleID(), membersList.get(i).getOYEUnitID(),
                                    membersList.get(i).getFirstName() + " " + membersList.get(i).getLastName(),
                                    membersList.get(i).getMobileNumber()*/
    public long insert_UnitOwner(int unid, int AssID, int unitid,
                                 String MobileNumber, String FName, String LName, String createdDt,
                                 String Status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("UnitOwnerID", unid);
        initialValues.put("AssociationID", AssID);
        initialValues.put("UnitID", unitid);
        initialValues.put("FirstName", FName);
        initialValues.put("MobileNumber", MobileNumber);
        initialValues.put("LastName", LName);
        initialValues.put("CreatedDate", createdDt);
        initialValues.put("UnitOwnerStatus", Status);

        Cursor cursor = db.rawQuery("SELECT * FROM UnitOwner where UnitOwnerID=" + unid + "", null);
        Log.d("count UnitOwners", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" UnitOwner OyeMembers", " updated " + unid + " " + FName + " " + LName);
            cursor.close();
            return db.update("UnitOwner", initialValues, "UnitOwnerID=" + unid + "  ", null);
        } else {
            Log.d(" UnitOwner OyeMembers", " inserted " + unid + " " + FName);
            cursor.close();
            return db.insert("UnitOwner", null, initialValues);
        }

    }

    public String getUnitOwnerNamesByUnitID(int unitID) {
        Log.d("Dgddfdf", "Owner entered" + unitID);

        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM UnitOwner where UnitID='" + unitID + "'";
        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            name = cur.getString(cur.getColumnIndex("FirstName")) + " " + cur.getString(cur.getColumnIndex("LastName"));
        }
        Log.d("Dgddfdf", "Owner entered" + unitID + " " + name);
        return name;

    }

    public String getUnitOwnerMobileByUnitID(int unitID) {
        Log.d("Dgddfdf", "Owner entered" + unitID);

        String mobile = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM UnitOwner where UnitID='" + unitID + "'";
        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            mobile = cur.getString(cur.getColumnIndex("MobileNumber"));
        }
        Log.d("Dgddfdf", "Owner entered" + unitID + " " + mobile);
        return mobile;

    }

    public long insertMemberNew(int MemberID, int AssID, int unitid, int accountid, int memberroleid,
                                String MobileNumber, String Name, String VehicleNumber,
                                String MemberStatus, String DownloadedDate, String leaveatgate, String dnd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("MemberID", MemberID);
        initialValues.put("AssociationID", AssID);
        initialValues.put("UnitID", unitid);
        initialValues.put("AccountID", accountid);
//        initialValues.put("ParentMemberID", parentmemberid);
        initialValues.put("MemberRoleID", memberroleid);
        initialValues.put("MobileNumber", MobileNumber);
//        initialValues.put("ResidentType", residenttype);
        initialValues.put("VehicleNumber", VehicleNumber);
        initialValues.put("Name", Name);
        initialValues.put("MemberStatus", MemberStatus);
        initialValues.put("DownloadedDate", DownloadedDate);
        initialValues.put("LeaveAtGate", leaveatgate);
        initialValues.put("DND", dnd);
        Cursor cursor = db.rawQuery("SELECT * FROM OyeMembers where MemberID=" + MemberID + " and UnitID=" + unitid + "  ", null);
        Log.d("count OyeMembers", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value OyeMembers", " updated " + unitid + " " + memberroleid + " " + MemberID + " " + Name);
            cursor.close();
            return db.update("OyeMembers", initialValues, "MemberID=" + MemberID + "  ", null);
        } else {
            Log.d(" value OyeMembers", " inserted " + unitid + " " + memberroleid + " " + MemberID + " " + Name);
            cursor.close();
            return db.insert("OyeMembers", null, initialValues);
        }

    }

    public long insertMemberNew1(int MemberID, int AssID, int unitid, int accountid, int memberroleid,
                                 String MobileNumber, String Name, String VehicleNumber,
                                 String MemberStatus, String DownloadedDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("MemberID", MemberID);
        initialValues.put("AssociationID", AssID);
        initialValues.put("UnitID", unitid);
        initialValues.put("AccountID", accountid);
//        initialValues.put("ParentMemberID", parentmemberid);
        initialValues.put("MemberRoleID", memberroleid);
        initialValues.put("MobileNumber", MobileNumber);
//        initialValues.put("ResidentType", residenttype);
        initialValues.put("VehicleNumber", VehicleNumber);
        initialValues.put("Name", Name);
        initialValues.put("MemberStatus", MemberStatus);
        initialValues.put("DownloadedDate", DownloadedDate);
        Cursor cursor = db.rawQuery("SELECT * FROM OyeMembers where MemberID=" + MemberID + "  ", null);
        Log.d("count OyeMembers", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value OyeMembers", " updated " + unitid + " " + memberroleid + " " + memberroleid);
            cursor.close();
            return db.update("OyeMembers", initialValues, "MemberID=" + MemberID + "  ", null);
        } else {
            Log.d(" value OyeMembers", " inserted " + accountid + " " + memberroleid);
            cursor.close();
            return db.insert("OyeMembers", null, initialValues);
        }

    }

    public int updateDNDlocal(int memid, String dnd, String leavegate) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE OyeMembers SET DND=trim('" + dnd + "'),LeaveAtGate=trim('" + leavegate + "') WHERE MemberID=trim('" + memid + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public int updateVisitorEntryRequest(int visitorlogID, String entryTime, int workID) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE NRVisitorsLog SET EntryDateTime=trim('" + entryTime + "'),EntryGuardID=trim('" + workID + "') WHERE NRVisitorLogID=trim('" + visitorlogID + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value_updated", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public int updateOtpStatus_photoStatus_allsettings(int assid, String otp, String photo, String namestatus, String mobilestatus, String logoff) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE Association SET OTPStatus=trim('" + otp + "'),PhotoStatus=trim('" + photo + "'),MobileStatus=trim('" + namestatus + "'),MobileStatus=trim('" + mobilestatus + "'),LogoffStatus=trim('" + logoff + "') WHERE AssociationID=trim('" + assid + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public int updateOtpStatus_photoStatus(int assid, String otp, String photo) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE Association SET OTPStatus=trim('" + otp + "'),PhotoStatus=trim('" + photo + "') WHERE AssociationID=trim('" + assid + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public int updatesettingStatusAssociation(int assid, String field, String value) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE Association SET " + field + "=trim('" + value + "') WHERE AssociationID=trim('" + assid + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public boolean checkMemberVehicleNumber2(String VehicleNo) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT VehicleNumber from OyeMembers where VehicleNumber =trim('" + VehicleNo + "')";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("Count++", String.valueOf(cursor.getCount()));

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    public boolean checkMemberVehicleNumber(String VehicleNo) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT VehicleNo from ResidentVehicles where VehicleNo =trim('" + VehicleNo + "')";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("Count++", cursor.getCount() + sql);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public Cursor getMemberData_byVehicle(String VehicleNo) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * from OyeMembers where VehicleNumber like '%" + VehicleNo + "%' ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("Count++", String.valueOf(cursor.getCount()));

        return cursor;
    }

    public int checkschoolVehicleNumber(String VehicleNo) {

        int OYEFamilyMemID = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT OYEFamilyMemberID from FamilyMembers where AadharNumber =trim('" + VehicleNo + "') or AadharNumber =trim('" + VehicleNo + "(Edited)" + "') COLLATE NOCASE ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("schoolbuss", cursor.getCount() + " " + VehicleNo);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            OYEFamilyMemID = cursor.getInt(0);
        }
        cursor.close();

        return OYEFamilyMemID;
    }

    public Cursor getSchoolBus(String VehicleNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("Vehicleno", VehicleNumber.trim());
        String sql = "SELECT * FROM FamilyMembers where AadharNumber =trim('" + VehicleNumber + "')";
        Cursor cur = db.rawQuery(sql, null);

        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public String getOYEMemberName(int MemberID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM OyeMembers where MemberID=" + MemberID + "";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " getOYEMemberName " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            return "Added by: " + cur.getString(cur.getColumnIndex("Name"));
        }
        cur.close();
        return "";
    }

    public String getPhone_byMember_ID(int memberid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where MemberID=" + memberid + "";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();

            return cur.getString(11).replace("+", "");
        }
        Log.d(" meme ph", " all count " + cur.getCount() + " ");
        cur.close();
        return "";
    }

    public Cursor getMemberByAssnID(int AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where AssociationID =" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;

    }

    public Cursor getMemberByAssnID_date(int AssociationID, String date_YMD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where associationID =" + AssociationID + " and DownloadedDate!='" + date_YMD + "' ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" OyeMembersDate", " all count " + cur.getCount() + " ");
        return cur;
    }

    public int getUnapprovedMembers(int AssociationID) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where MemberRoleID=5 and AssociationID =" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public int updateMembertable(int id, int roleid) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE OyeMembers SET MemberRoleID=trim('" + roleid + "') WHERE MemberID=trim('" + id + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        count = cur.getCount();
        cur.close();
        return count;
    }

    public int getAdminMemberUnit(int AssociationID) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where AssociationID =" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            count = cur.getInt(cur.getColumnIndex("UnitID"));
        }
        cur.close();
        Log.d(" valueUnitID", " all count " + cur.getCount() + " " + count + " " + sql);
        return count;
    }

    public String getPhone_byMemberID(int UnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where UnitID=" + UnitID + "";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();

            return cur.getString(11).replace("+", "");
        }
        Log.d(" meme ph", " all count " + cur.getCount() + " ");
        cur.close();
        return "";
    }

    public int spintext(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * from OYEUnit  where UnitName='" + name + "'";
        Cursor cursor = db.rawQuery(sql, null);
        int x = 0;
        cursor.moveToFirst();
        x = cursor.getInt(0);
        Log.d("Valuefromdb413", String.valueOf(x));
        return x;
    }

    public List<String> unitname(int AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
//        String sql ="SELECT * from OYEUnit ";OyeMembers where UnitID 9108121422
        String sql = "SELECT * FROM OYEUnit pm  WHERE pm.UnitID NOT IN (SELECT pd.UnitID FROM OyeMembers pd where pd.AssociationID =" + AssociationID + " ) " +
                " and pm.AssociationID=" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(2));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }


    /*OyeUnit(UnitID integer primary key autoincrement, " +
                " associationID integer , unitName VARCHAR(20),  Type VARCHAR(20) , AdminAccountID integer , " +
                " CreatedDateTime VARCHAR(20),  ParkingSlotNumber VARCHAR(20) */

    public boolean checkMobileNumber(String mobNumber) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * from NRVisitorsLog where MobileNumber =trim('" + mobNumber + "')";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("Count++", String.valueOf(cursor.getCount()));

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    public boolean checkVehicleNumber(String VehicleNo) {

        SQLiteDatabase db = this.getWritableDatabase();
//        String sql1 ="SELECT * FROM NRVisitorsLog";
//
//        Cursor cur1 = db.rawQuery(sql1, null);
//        if(cur1.getCount()>0) {
//            cur1.moveToFirst();
//
//            do {
//                Log.d(" value1", " UnitID " + cur1.getInt(2) + " ");
//                Log.d(" value2", " Fname " + cur1.getString(4) + " ");
//                Log.d(" value3", " Lname " + cur1.getString(5) + " ");
//                Log.d(" value4", " phone " + cur1.getString(6) + " ");
//                Log.d(" value", " No of persons" + cur1.getInt(7) + " ");
//                Log.d(" value", " Vehicle number |" + cur1.getString(17) + "| |" + VehicleNo + "| ");
//                Log.d(" value", " Service provider" + cur1.getString(11) + " ");
//
//            } while (cur1.moveToNext());
//            Log.d(" value315", " all count " + cur1.getCount() + " ");
//        }
        String sql = "SELECT VehicleNumber from NRVisitorsLog where VehicleNumber =trim('" + VehicleNo + "')";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("Count++", cursor.getCount() + sql);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    public void insertOyeUnits(Integer UnitID, Integer AssociationID, String UnitName, String Type,
                               Integer AdminAccountID, String ParkingSlotNumber, String CreatedDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("UnitID", UnitID);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("UnitName", UnitName);
        initialValues.put("Type", Type);
        initialValues.put("AdminAccountID", AdminAccountID);
        initialValues.put("ParkingSlotNumber", ParkingSlotNumber);
        initialValues.put("CreatedDateTime", CreatedDateTime);
        Cursor cursor = db.rawQuery("SELECT * FROM OyeUnit where UnitID=" + UnitID + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" Dgddfdf unit ", " updated homemenuactivity2 " + AssociationID + " " + UnitName + " " + UnitID);
            cursor.close();
            db.update("OyeUnit", initialValues, "UnitID=" + UnitID + " ", null);
        } else {

            Log.d(" Dgddfdf unit ", " inserted homemenuactivity2 " + AssociationID + " " + UnitName + " " + UnitID);
            cursor.close();
            db.insert("OyeUnit", null, initialValues);
        }

    }

    public String getUnitName(Integer UnitID) {
        String unitName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeUnit where UnitID=" + UnitID + " ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " homemenuactivity2 " + UnitID);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            unitName = cursor.getString(cursor.getColumnIndex("UnitName"));
            Log.d("count", cursor.getCount() + " homemenuactivity2 " + unitName);
        }
        cursor.close();
        return unitName;
    }

    public ArrayList<String> getUnitNames(Integer aasid) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT distinct UnitName FROM OyeUnit where AssociationID=" + aasid + "";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" suvarna 1860", " OyeUnit " + cur.getCount() + " " + sql);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                list.add(cur.getString(cur.getColumnIndex("UnitName")));
            } while (cur.moveToNext());


        }
        return list;

    }

    public boolean checkUnitName(String name, int AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM OyeUnit where UnitName=trim('" + name + "') and AssociationID=" + AssociationID;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public int getUnitID(String UnitName, int AssociationID) {
        int unitName = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM OyeUnit where UnitName='" + UnitName + "' and AssociationID=" + AssociationID + "";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + UnitName);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            unitName = cursor.getInt(cursor.getColumnIndex("UnitID"));
        }
        cursor.close();
        return unitName;
    }

    public String getNamebyUnitID(Integer UnitID) {
        String memberName = "Empty Unit";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM OyeMembers where UnitID=" + UnitID + " ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + UnitID);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            memberName = "Name: " + cursor.getString(cursor.getColumnIndex("Name"));
        }
        cursor.close();
        return memberName;
    }

    public boolean duplicateMember(int id, int unutid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select UnitID from OyeMembers Group By UnitID,AssociationID Having Count(*)>2 and AssociationID=trim('" + id + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                if (unutid == cur.getInt(0)) {
                    cur.close();
                    return true;
                }

            } while (cur.moveToNext());
        }
        cur.close();
        return false;

    }

    public boolean getMemberExists(int UnitID) {
        boolean taken = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM OyeMembers where UnitID=" + UnitID + " ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + UnitID);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            taken = true;
        }
        cursor.close();
        return taken;
    }

    public boolean getMemberExists_adminapproval(int UnitID) {
        boolean taken = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM OyeMembers where UnitID=" + UnitID + " and MemberRoleID=1 and MemberStatus='true' or " +
                " UnitID=" + UnitID + " and MemberRoleID=3 and MemberStatus='true' ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + UnitID + " " + sql);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            taken = true;
        }
        cursor.close();
        return taken;
    }

    public Cursor getAllUnitsBy_associationID(Integer AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeUnit where AssociationID=" + AssociationID + " ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getMemberUnits_byassociationID(Integer AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT distinct AssociationID, UnitID FROM OyeUnit where AssociationID=" + AssociationID;//+" " +
//                " and MemberRoleID in ("+ROLE_ADMIN+","+ROLE_RESIDENT+")";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getAllUnits() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeUnit  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public int[][] getAllUnits_arr(int AssociationID) {
        try {
            int[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT UnitID FROM OyeUnit where AssociationID=" + AssociationID + " ";
//            String sql ="SELECT UnitID FROM OyeMembers where associationID="+AssociationID+" and " +
//                    " UnitID NOT IN (SELECT distinct OYEUnitID FROM FamilyMembers where associationID="+AssociationID+" ) ";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count sfsffs 1sl", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new int[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getInt(0);
                        Log.d("data sfsffs", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)
                        );
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[][] getAllMemberIds_arr(int AssociationID) {
        try {
            int[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();
//            String sql ="SELECT UnitID FROM OyeUnit where AssociationID="+AssociationID+" " ;
            String sql = "SELECT MemberID FROM OyeMembers where associationID=" + AssociationID + " ";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count sfsffs sl", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new int[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getInt(0);
                        Log.d("data sfsffs", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)
                        );
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[][] getAllFingerPrint(int AssociationID) {
        try {
            byte[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();

            String sql = "SELECT u.*,r.EntryDateTime FROM userdetails u left join RegularVisitorsLog r on u.username==r.VirtualID where  " +
                    "  u.photo_FP1 not null order by r.EntryDateTime desc  , u.finger_type ";
//            String sql ="SELECT UnitID FROM OyeUnit where AssociationID="+AssociationID+" " ;
            // String sql ="SELECT MemberID FROM OyeMembers where associationID="+AssociationID+" ";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count sfsffs sl", cursor.getCount() + "");
            Log.d("Finger", "Count " + cursor.getCount());
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new byte[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getBlob(3)[0];
                        Log.d("data sfsffs", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)

                        );
                        Log.d("Finger", "Count " + cursor.getString(0));
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



/*RouteCheckPoints(CheckPointsID integer , " +
                " associationID integer, MemberID integer , CheckPointName VARCHAR(20) primary key,  createdDate VARCHAR(20) , " +
                " gpsPoint VARCHAR(30) , Image VARCHAR(20)  */

    public String getResidentsSummary(Integer AssociationID) {
        String txt = "Total Units: 5 \n Occupied: 0";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeUnit where AssociationID=" + AssociationID + " ";
        Cursor curUnit = db.rawQuery(sql, null);
        Log.d(" value", " all count " + curUnit.getCount() + " " + AssociationID);

        String sql_assn = "SELECT * FROM Association where AssociationID=" + AssociationID + " ";
        Cursor curAssn = db.rawQuery(sql_assn, null);
        Log.d(" value", " all count " + curAssn.getCount() + " ");

        if (curAssn.getCount() > 0) {
            curAssn.moveToFirst();
            txt = " Total Units: " + curAssn.getInt(7) + " \n Occupied: " + curUnit.getCount();
        }
        Log.d("sfsfs", txt);
        curAssn.close();
        curUnit.close();
        return txt;
    }

    public int getResidentsSummarytotalUnit(Integer AssociationID) {
        int totalunits = 0;
        SQLiteDatabase db = this.getReadableDatabase();

/*
        String sql_assn ="SELECT * FROM OyeUnit where associationID="+associationID+" ";
        Cursor curAssn = db.rawQuery(sql_assn, null);
        Log.d(" value"," all count "+curAssn.getCount()+" ");

        if(curAssn.getCount()>0) {
            curAssn.moveToFirst();
            totalunits=curAssn.getCount();//curAssn.getInt(7);
        }
        curAssn.close();
*/
        String sql_assn = "SELECT * FROM Association where AssociationID=" + AssociationID + " ";
        Cursor curAssn = db.rawQuery(sql_assn, null);
        Log.d(" Dgddfdf", " assn units count " + curAssn.getCount() + " ");

        if (curAssn.getCount() > 0) {
            curAssn.moveToFirst();
            totalunits = curAssn.getInt(7);
        }
        Log.d("sfsfs", totalunits + "");
        curAssn.close();

        return totalunits;
    }

    public long insertCheckPoints(Integer AssociationID, String CheckPointName, Integer MemberID,
                                  String GPSPoint, String CreatedDate, Integer CheckPointsID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("CheckPointName", CheckPointName);
        initialValues.put("MemberID", MemberID);
        initialValues.put("GPSPoint", GPSPoint);
        initialValues.put("CreatedDate", CreatedDate);
        initialValues.put("CheckPointsID", CheckPointsID);
        Cursor cursor = db.rawQuery("SELECT * FROM RouteCheckPoints where AssociationID=" + AssociationID
                + " and   CheckPointName=trim('" + CheckPointName + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + AssociationID + " " + CheckPointName);
            cursor.close();
            return -1;
        } else {
            Log.d(" value", " inserted " + AssociationID + " " + CheckPointName);
            cursor.close();
            return db.insert("RouteCheckPoints", null, initialValues);
        }

    }

    public int updateCheckPoint(Integer mmid, Integer checkpointid, String checkname, String gps, String date) {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE RouteCheckPoints SET CheckPointName=trim('" + checkname + "'), GPSPoint=trim('" + gps + "'), CreatedDate=trim('" + date + "'), MemberID=trim('" + mmid + "')" +
                "where CheckPointsID=" + checkpointid;
        Log.d("count741", sql);
        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            count = cur.getCount();
            cur.close();
            Log.d("count741", String.valueOf(count));
            return count;
        }
        cur.close();
        return count;

    }

    public long insertPatrollingNotificationTable(int aid, String date, int Gid, String Pat_time, String EndTime, boolean done, boolean ntfd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociatoinID", aid);
        initialValues.put("Date", date);
        initialValues.put("GuardID", Gid);
        initialValues.put("PatrolTime", Pat_time);
        initialValues.put("StartTime", EndTime);
        initialValues.put("PatrolDone", done);
        initialValues.put("Notified", ntfd);

        return db.insert("PatrollingNotification", null, initialValues);


    }

    public long updatePatrolNotification(int aid, String date, int Gid, String Pat_time, String EndTime, boolean done, boolean ntfd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociatoinID", aid);
        initialValues.put("Date", date);
        initialValues.put("GuardID", Gid);
        initialValues.put("PatrolTime", Pat_time);
        initialValues.put("StartTime", EndTime);
        initialValues.put("PatrolDone", done);
        initialValues.put("Notified", ntfd);

        Cursor cursor = db.rawQuery("SELECT * FROM PatrollingNotification where GuardID=trim('" + Gid
                + "') and   Date=trim('" + date + "') and PatrolDone='" + false + "' ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {

            return db.insert("PatrollingNotification", null, initialValues);
        } else {
            // Log.d(" value"," inserted "+Fname+" "+email);
            cursor.close();
            return db.update("PatrollingNotification", initialValues, "GuardID=" + Gid + " and Date=" + date, null);
        }

    }

    public String getPatrolDonedata(int guardid, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM PatrollingNotification where GuardID =trim('" + guardid + "') and PatrolDone='" + 0 + "' and Notified='" + 0 + "' and Date=('" + date + "')";
        Cursor cur = db.rawQuery(sql, null);


        SQLiteDatabase db1 = this.getReadableDatabase();
        String sql1 = "SELECT * FROM PatrollingNotification where GuardID =trim('" + guardid + "') and Date=('" + date + "')";
        Cursor cur1 = db1.rawQuery(sql1, null);

        if (cur1.getCount() == 0) {
            Log.d("codechef", "noentries" + cur.getCount() + " " + cur1.getCount() + " " + sql + " " + sql1);
            return "noentries";
        } else if (cur.getCount() > 0) {
            Log.d("codechef", "notdone" + cur.getCount() + " " + cur1.getCount() + " " + sql + " " + sql1);
            return "notdone";
        }
        Log.d("codechef", "false" + cur.getCount() + " " + cur1.getCount() + " " + sql + " " + sql1);
        return "false";

    }

    public String updatePatrolNotified(int guardid, boolean ntfd) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Update PatrollingNotification SET PatrolDone='" + ntfd + "' where GuardID='" + guardid + "' ";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            return "true";

        } else
            return "false";


    }

    public String getOTPstatusbyAssociationID(int assid) {
        String name = "OFF";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID =" + assid + " ";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("OTPStatus"));
        }
        cur.close();
        Log.d(" Dgddfdf", " Assid " + cur.getCount() + " " + assid + " " + name);
        return name;
    }

    public String getPhotostatusbyAssociationID(int assid) {
        String name = "OFF";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID =" + assid + " ";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("PhotoStatus"));
        }
        Log.d(" Dgddfdf", " Assid " + cur.getCount() + " " + assid + " " + name);
        cur.close();
        return name;
    }

    public String getVisitorNamestatusbyAssociationID(int assid) {
        String name = "OFF";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID =" + assid + " ";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("NameStatus"));
        }
        Log.d(" Dgddfdf", " Assid " + cur.getCount() + " " + assid + " " + name);
        cur.close();
        return name;
    }

    public String getVisitorNumberstatusbyAssociationID(int assid) {
        String name = "OFF";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID =" + assid + " ";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("MobileStatus"));
        }
        Log.d(" Dgddfdf", " Assid " + cur.getCount() + " " + assid + " " + name);
        cur.close();
        return name;
    }

    public String getGuardLogOffstatusbyAssociationID(int assid) {
        String name = "OFF";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID =" + assid + " ";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("LogoffStatus"));
        }
        Log.d(" Dgddfdf", " Assid " + cur.getCount() + " " + assid + " " + name);
        cur.close();
        return name;
    }

    public String AssociationPropertyType(int assid) {
        String name = "Residential";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID =" + assid + " ";

        Cursor cur = db.rawQuery(sql, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("AssPrpType"));
        }

        Log.d(" Dgddfdf", " Assid " + cur.getCount() + " " + assid + " " + name);
        cur.close();
        return name;
    }

    public long syncheckpointlist(int assid, int mmid, String checkpointname, String date, String GPS, int checkpointid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", assid);
        initialValues.put("CheckPointName", checkpointname);
        initialValues.put("MemberID", mmid);
        initialValues.put("GPSPoint", GPS);
        initialValues.put("CreatedDate", date);
        initialValues.put("CheckPointsID", checkpointid);

        Cursor cursor = db.rawQuery("SELECT * FROM RouteCheckPoints where CheckPointsID=trim('" + checkpointid
                + "')   ", null);
        Log.d("count1481", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            cursor.close();
            // Log.d(" value", " updated " + Name + " " + PanNumber);
            return -1;
        } else {
            Log.d(" value1481", " inserted " + checkpointname + " " + GPS);
            db.insert("RouteCheckPoints", null, initialValues);
            cursor.close();
            return 1;
        }
    }

    public void deleteAll_checkpoints(int AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete  FROM RouteCheckPoints where AssociationID=" + AssociationID + " ";

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "Association deleted ");
    }

    public boolean checkcheckpoints(String name, int AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM RouteCheckPoints where CheckPointName='" + name + "' and " +
                " AssociationID=" + AssociationID + "  ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" Dgddfdf  ", "CheckPointName  " + name);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    /*MyMembership( oyeMemberID integer , " +
                " associationID integer ,oyeUnitID integer , firstName VARCHAR(30) ,  lastName VARCHAR(30) , " +
                " mobileNumber VARCHAR(20) ,email VARCHAR(30) ,parentAccountID integer ,  " +
                "  oyeMemberRoleID integer , status VARCHAR(20) ,accountID  integer ,vehicleNumber VARCHAR(100) )*/

    public Cursor getCheckPointData(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM RouteCheckPoints where CheckPointName='" + name + "'";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " cp count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getCheckPoints() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RouteCheckPoints  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public void insertInvitation(int OYEFamilyMemberID, int AssociationID, int OYEUnitID, int MemberID,
                                 String FirstName, String LastName, String MobileNumber, String VisitorType,
                                 String AadharNumber, String CreatedDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("OYEFamilyMemberID", OYEFamilyMemberID);
        initialValues.put("AssociationID", AssociationID);

//        initialValues.put("MemberID",MemberID);
        initialValues.put("OYEUnitID", OYEUnitID);
        initialValues.put("FirstName", FirstName);
        initialValues.put("LastName", LastName);
        initialValues.put("MobileNumber", MobileNumber);
        initialValues.put("VisitorType", VisitorType);

        initialValues.put("AadharNumber", AadharNumber);
        initialValues.put("CreatedDate", CreatedDate);

        Cursor cursor = db.rawQuery("SELECT * FROM Invitations  where OYEFamilyMemberID=" + OYEFamilyMemberID
                + "   ", null);
        Log.d("count5555", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated ");
            db.update("Invitations", initialValues, "OYEFamilyMemberID=" + OYEFamilyMemberID
                    + " ", null);

        } else {
            Log.d(" value", " inserted " + OYEFamilyMemberID);
            //db.insert("Invitations", null, initialValues);
            Log.d("invited12", "" + db.insert("Invitations", null, initialValues));
        }
        cursor.close();

    }

    public Cursor getMyMember_byUnitID(int OYEUnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM MyMembership where OYEUnitID=" + OYEUnitID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getMyMemberships() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM MyMembership  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public void deleteMyMemberships() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Delete FROM MyMembership ";
        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf unit ", " deleted ");
    }



    /*Association(associationID integer  ," +
                " Name TEXT, Country VARCHAR(40) , Locality VARCHAR(80) , PanNumber VARCHAR(20) primary key, Pincode VARCHAR(40) , " +
                " GPSLocation VARCHAR(40) , TotalUnits integer, MaintenanceRate double, MaintenancePenalty double, " +
                " PropertyCode VARCHAR(40) , FyStart integer, MaintPymtFreq integer*/

    public List<String> allAssociation() {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
//        String sql ="SELECT unitName  FROM OyeUnit pm  WHERE pm.UnitID IN (SELECT distinct pd.oyeUnitID FROM MyMembership pd ) " ;
        String sql = " SELECT distinct AssociationID FROM Association ORDER BY Name";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                // Log.d("Assid",getAssociationName(cur.getInt(0)));
                list.add(getAssociationName(cur.getInt(0)));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public int getAssociationIDbyName(String AssName) {
        int assid = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT *FROM Association where Name='" + AssName + "'";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + AssName);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            assid = cursor.getInt(cursor.getColumnIndex("AssociationID"));
            Log.d("count", cursor.getCount() + " " + assid);
        }
        cursor.close();
        return assid;

    }

    public int insertAssociationDetails(Integer AssociationID, String Name, String Country, String PanNumber, String Locality, String Pincode,
                                        String GPSLocation, Integer TotalUnits, String otpstatus, String photosts
            , String namests, String mobilestatus, String logoffstatus, String prpType
//            ,Double MaintenancePenalty,
//                                        String PropertyCode, Integer FyStart, Integer MaintPymtFreq
    ) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues initialValues = new ContentValues();
            initialValues.put("AssociationID", AssociationID);
            initialValues.put("Name", Name);
            initialValues.put("Country", Country);
            initialValues.put("Locality", Locality);
            initialValues.put("Pincode", Pincode);
            initialValues.put("GPSLocation", GPSLocation);

            initialValues.put("TotalUnits", TotalUnits);
            initialValues.put("OTPStatus", otpstatus);
            initialValues.put("PhotoStatus", photosts);
            initialValues.put("NameStatus", namests);
            initialValues.put("MobileStatus", mobilestatus);
            initialValues.put("LogoffStatus", logoffstatus);
            initialValues.put("AssPrpType", prpType);
//            initialValues.put("MaintenanceRate", MaintenanceRate);
//            initialValues.put("MaintenancePenalty", MaintenancePenalty);
//            initialValues.put("PropertyCode", PropertyCode);
//            initialValues.put("FyStart", FyStart);
//            initialValues.put("MaintPymtFreq", MaintPymtFreq);

            Cursor cursor = db.rawQuery("SELECT * FROM Association where PanNumber=trim('" + PanNumber
                    + "')   ", null);
            Log.d("count", cursor.getCount() + "");
            if (cursor.getCount() > 0) {
                Log.d(" value", " updated " + Name + " " + PanNumber);
                cursor.close();
                return db.update("Association", initialValues, "PanNumber=trim('" + PanNumber + "') ", null);

            } else {
                Log.d(" value", " inserted " + Name + " " + PanNumber);
                cursor.close();
                db.insert("Association", null, initialValues);
                return 1;
            }
        } catch (Exception ex) {
            return -1;
        }
/*
        cursor = db.rawQuery("SELECT * FROM Association where PanNumber=trim('"+PanNumber
                +"')   ", null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            return cursor.getInt(0);
        }else{
            return -2;
        }
*/

    }

    public void deleteAll_association() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete FROM Association";

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "Association deleted ");
    }

    public Cursor getAssociations() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public String getAssociationName(int AssociationID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID=" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("Name"));
        }
        cur.close();
        return name;
    }

    public String getAssociationLatLon(int AssociationID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID=" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("GPSLocation"));
        }
        cur.close();
        return name;
    }

    public String getAssociationAddress(int AssociationID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID=" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("Locality"));
        }
        cur.close();
        return name;
    }




    /*RouteTracker(ID integer primary key autoincrement, " +
                " associationID integer, guardID integer , PatrollingTrackerID integer,  Date VARCHAR(20) , " +
                " Time VARCHAR(20) , gpsPoint VARCHAR(30) , CheckPointName VARCHAR(20) , Image VARCHAR(20) */

    public String getAssociationValidity(int AssociationID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Association where AssociationID=" + AssociationID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value_validity", " all count " + cur.getCount() + " " + AssociationID);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("Validity"));
            Log.d(" value_validity", " all count " + cur.getCount() + " " + AssociationID + "Validity:" + name);
        }
        cur.close();

        if (name == null) {
            return "";
        } else
            return name;
    }

    public void setAssociationValidity(int AssociationID, String Validity) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Update Association SET Validity=trim('" + Validity + "') where AssociationID=" + AssociationID;
        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            Log.d("Validity updated", "Assid: " + AssociationID + " Validi: " + Validity);
        } else
            Log.d("Validity notAdded", "Assid: " + AssociationID + " Validi: " + Validity);
    }

    public long insertRouteTracker(Integer AssociationID, Integer GuardID, int PatrollingTrackerID, String Date, String Time,
                                   String GPSPoint, String acc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("GuardID", GuardID);
        initialValues.put("PatrollingTrackerID", PatrollingTrackerID);
        initialValues.put("Date", Date);
        initialValues.put("Time", Time);
        initialValues.put("GPSPoint", GPSPoint);

        Cursor cursor = db.rawQuery("SELECT * FROM RouteTracker where Date=trim('" + Date
                + "') and   Time=trim('" + Time + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" RouteTracker", " updated " + Time + " " + GPSPoint);
            cursor.close();
            return -1;
        } else {
            Log.d(" RouteTracker", " inserted " + Time + " " + GPSPoint + " " + acc);
            cursor.close();
            return db.insert("RouteTracker", null, initialValues);
        }

    }

    public Cursor getPatrollingIDs(String date_YMD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT distinct GuardID, PatrollingTrackerID FROM RouteTracker where Date=trim('" + date_YMD + "')";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" RouteTracker", "dfhfh  pids " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getGuardPatrollingRoute(int PatrollingTrackerID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RouteTracker where PatrollingTrackerID=" + PatrollingTrackerID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getGuardPatrollingRoute1(int PatrollingTrackerID, int GuardID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RouteTracker where PatrollingTrackerID=" + PatrollingTrackerID + " and GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" RouteTracker ", "gpscount dfhfh " + cur.getCount() + " ");
        return cur;
    }

    /*Shifts(shiftID integer primary key, " +
                " accountID integer, associationID integer , guardID integer , " +
                " startDate VARCHAR(20), endDate VARCHAR(20),  ShiftStartTime VARCHAR(20), ShiftEndTime VARCHAR(20), " +
                " createdDate VARCHAR(20) */

    public String getPatrollingStartTime(int PatrollingTrackerID) {
        String time = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT min(Time) FROM RouteTracker where PatrollingTrackerID=" + PatrollingTrackerID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            time = cur.getString(0);
        }
        cur.close();
        return time;
    }

    public String getPatrollingEndTime(int PatrollingTrackerID) {
        String time = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT max(Time) FROM RouteTracker where PatrollingTrackerID=" + PatrollingTrackerID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            time = cur.getString(0);
        }
        cur.close();
        return time;
    }

    public long insertShifts(int ShiftID, Integer AssociationID, Integer GuardID,
                             String StartDate, String EndDate, String ShiftStartTime,
                             String ShiftEndTime, String CreatedDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("ShiftID", ShiftID);
        initialValues.put("GuardID", GuardID);
        initialValues.put("StartDate", StartDate);
        initialValues.put("EndDate", EndDate);
        initialValues.put("ShiftStartTime", ShiftStartTime);
        initialValues.put("ShiftEndTime", ShiftEndTime);
        initialValues.put("CreatedDate", CreatedDate);

        Cursor cursor = db.rawQuery("SELECT * FROM Shifts where ShiftID=trim(" + ShiftID
                + ") and   AssociationID=trim(" + AssociationID + ")  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" Dgddfdf", "GuardShift updated " + ShiftID + " " + AssociationID);
            cursor.close();
            return -1;
        } else {
            Log.d(" Dgddfdf", "GuardShift inserted " + GuardID + " " + StartDate + " " + EndDate);
            cursor.close();
            return db.insert("Shifts", null, initialValues);
        }


    }

    public int getSecurityShiftID(Integer guardID) {

        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Shifts where GuardID=" + guardID + "";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("ShiftID"));
        }
        cursor.close();

        return id;
    }

    public void deleteAll_GuardShifts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Delete FROM Shifts ";
        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf unit ", " deleted ");
    }

    public String getShiftDetails(int GuardID) {
        String details = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT StartDate , EndDate ,  ShiftStartTime , ShiftEndTime  FROM Shifts where GuardID=" + GuardID + " order by ShiftID desc ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cursor.getCount() + " " + GuardID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                details = "Shift Details:- \n" + "From : " + cursor.getString(2) + " To : " + cursor.getString(3) + "\n"
                        + "From: " + cursor.getString(0) + " To : " + cursor.getString(1) + "";
            }
        }
        cursor.close();
        return details;
    }

    public void deleteAll_Attendance() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Delete FROM Attendance ";
        SQLiteStatement st1 = db.compileStatement(sql);
//        st1.executeInsert();
        Log.d(" Dgddfdf unit ", " deleted ");
    }

    public void insertAttendance(Integer AttendanceID, Integer AssociationID, Integer GuardID, String IMEINo,
                                 String StartDate, String EndDate, String GPSPoint, String AttendancePoint
            , String StartTime, String EndTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AttendanceID", AttendanceID);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("GuardID", GuardID);
        initialValues.put("ImeiNo", IMEINo);
        initialValues.put("StartDate", StartDate);
        initialValues.put("EndDate", EndDate);
//        initialValues.put("gpsPoint",gpsPoint);
//        initialValues.put("AttendancePoint",attendancePoint);
        initialValues.put("StartTime", StartTime);
        initialValues.put("EndTime", EndTime);

        Cursor cursor = db.rawQuery("SELECT * FROM Attendance where AttendanceID=" + AttendanceID + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + AttendanceID + " " + AssociationID);
            cursor.close();
            db.update("Attendance", initialValues, "AttendanceID=" + AttendanceID + " ", null);

        } else {
            Log.d(" value", " inserted " + AttendanceID + " " + AssociationID + " ");
            cursor.close();
            db.insert("Attendance", null, initialValues);
        }
        cursor.close();

    }

    public int getGuardPresentCount(Integer AssociationID, String dateYYYYMMDD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT distinct GuardID  FROM Attendance where EndTime='1900-01-01T00:00:00' " +
                " and AssociationID=" + AssociationID + "  "; //and startTime='%"+dateYYYYMMDD+"%'
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" Dgddfdf", " Attendance count " + cursor.getCount() + " " + AssociationID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                Log.d("tag", String.valueOf(cursor.getCount()));
            }
        }
        cursor.close();
        return Integer.valueOf(cursor.getCount());
    }

    public boolean getAttendanceDetails(int GuardID) {
        boolean onDuty = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Attendance where GuardID=" + GuardID + " and EndDate='0001-01-01T00:00:00' order by AttendanceID desc ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cursor.getCount() + " " + GuardID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                onDuty = true;
            }
        }
        cursor.close();
        return onDuty;
    }

    public String getAttendanceStartDate(int GuardID) {
        String StartDate = "Not Logged In";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Attendance where GuardID=" + GuardID + " and EndDate='1900-01-01T00:00:00' order by AttendanceID desc ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cursor.getCount() + " " + GuardID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                StartDate = cursor.getString(cursor.getColumnIndex("StartTime"));
            }
        }
        cursor.close();
        return StartDate;
    }

    public String getSecurityName(Integer guardId) {
        String accountName = "";
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM SecurityGuard where GuardID=" + guardId + " ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("checkit 2909", String.valueOf(cursor.getCount()));

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            accountName = cursor.getString(6);
            Log.d("checkit 2915", accountName);
        }
        cursor.close();

        return accountName;
    }

    public String getShiftStartTime(int GuardID) {
        String details = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT ShiftStartTime  FROM Shifts where GuardID=" + GuardID + " order by ShiftID desc ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("hvsdvk", " all count " + cursor.getCount() + " " + GuardID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                details = cursor.getString(0);
            }
        }
        cursor.close();
        return details;
    }

    public String getShiftEndTime(int GuardID) {
        String details = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT ShiftEndTime  FROM Shifts where GuardID=" + GuardID + " order by ShiftID desc ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("hvsdvk value315", " all count " + cursor.getCount() + " " + GuardID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                details = cursor.getString(0);
            }
        }
        cursor.close();
        return details;
    }

    public String getVisitorType_byID(int OYEFamilyMemberID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MemberType FROM Invitedvisitorlocal where OYEFamilyMemberID=" + OYEFamilyMemberID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("MemberType"));
            Log.d("checkit", name);
        }
        cur.close();
        return name;
    }

    public String getTimings_byID(int OYEFamilyMemberID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT StartDate ,EndDate ,WorkStartTime ,WorkEndTime  FROM Invitedvisitorlocal where OYEFamilyMemberID=" + OYEFamilyMemberID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = " from : " + cur.getString(cur.getColumnIndex("WorkStartTime")) + "\n to  : " + cur.getString(cur.getColumnIndex("WorkEndTime"));
            Log.d("checkit", name);
        }
        cur.close();
        return name;
    }

    public int SecurityGuardList(Integer AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM SecurityGuard where AssociationID=" + AssociationID + " and Status!='InActive' ";
        //where guardID="+guardID+" and endDate='1900-01-01T00:00:00' order by attendanceID desc
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value317", " all count " + cursor.getCount() + " " + AssociationID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                Log.d("tag11", String.valueOf(cursor.getCount()));
            }
        }
        cursor.close();

        return Integer.valueOf(cursor.getCount());
    }

    public List<String> guardsID1(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT GuardID FROM SecurityGuard where AssociationID=trim('" + AssociationID + "')   ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> all_GuardList(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where AssociationID=" + AssociationID + "  ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> managers_guardsID1(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT GuardID FROM SecurityGuard where AssociationID=trim('" + AssociationID + "')  ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> managers_and_GuardList(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where AssociationID=" + AssociationID + "";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> myGuardListID(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT GuardID FROM SecurityGuard where AssociationID=" + AssociationID + "";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> all_GuardAndSupervisorList(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where AssociationID=" + AssociationID + "   ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> myCheckpointist(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT CheckPointName FROM RouteCheckPoints where AssociationID=" + AssociationID + " and CheckPointName!='Attendance Point'";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        Log.d("get123", String.valueOf(cur.getCount()));
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> myGuardList(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where AssociationID=" + AssociationID + " and Status!='InActive' ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public int checkPointID(String CheckPointName, Integer AssociationID) {
        int checkid = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM RouteCheckPoints where AssociationID=" + AssociationID + " and CheckPointName=trim('" + CheckPointName + "')";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("count", cursor.getCount() + " " + CheckPointName + " " + sql);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            checkid = cursor.getInt(cursor.getColumnIndex("CheckPointsID"));
            Log.d("count121", cursor.getCount() + " " + checkid);
        }
        cursor.close();
        return checkid;
    }

    public int getActiveSecurityGuardCount(Integer AssociationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Shifts where AssociationID=" + AssociationID;
        //where guardID="+guardID+" and endDate='1900-01-01T00:00:00' order by attendanceID desc
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" Dgddfdf", " shift count " + cursor.getCount() + " " + AssociationID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                Log.d("tag", String.valueOf(cursor.getCount()));
            }
        }
        int id = Integer.valueOf(cursor.getCount());
        cursor.close();
        return id;
    }

    public String get_Visiting_UnitNames(int NRVisitorID) {
        String unitNames = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT UnitNames FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID;
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cursor.getCount() + " " + NRVisitorID + " ");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                unitNames = cursor.getString(0);
            }
        }
        cursor.close();
        return unitNames;
    }

    public String get_comment_status(int NRVisitorID) {
        String unitNames = "Pending";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Comment FROM NRVisitorsLog where NRVisitorLogID=" + NRVisitorID;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                if (cursor.getString(0).length() == 0) {
                    unitNames = "Pending";
                } else {
                    unitNames = cursor.getString(0);
                }
            }
        }
        cursor.close();
        Log.d(" value315", " all count " + cursor.getCount() + " " + NRVisitorID + " " + unitNames);
        return unitNames;
    }

    public String[] getFamilyMemberType(int unitid) {
        String[] name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM FamilyMembers where VisitorType='Family'  and OYEUnitID =" + unitid, null);
        name = new String[cur.getCount() + 2];
        name[0] = "Select";
        name[1] = "Primary";

        Log.d("3976", cur.getCount() + " " + unitid);
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                int i = 2;

                do {

//                    name[i] = cur.getString(cur.getColumnIndex("MemberType"))==null ? " hi":cur.getString(cur.getColumnIndex("MemberType"));
                    name[i] = getFamilyMemberTypebyid(cur.getInt(0)) == null ? "NA" : getFamilyMemberTypebyid(cur.getInt(0));
                    Log.d("checkit 3984", name[i]);
                    i++;
                } while (cur.moveToNext());
            }
        }
        cur.close();
        return name;
    }



    /*SecurityGuard(guardID integer primary key , " +
                " accountID integer, associationID integer , MemberID integer , ParentRoleID integer," +
                " GuardRoleID integer, LocalPhotoName VARCHAR(40), PhotoID integer, " +
                " createdDate VARCHAR(20) , AadharNumber VARCHAR(20) )*/

    public String[] getmobilenumberinarr(int unitid) {
        String[] name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FamilyMembers where VisitorType='Family'  and OYEUnitID =" + unitid, null);
        name = new String[cursor.getCount() + 2];
        Log.d("mobilenumber mon", cursor.getCount() + " " + unitid);

        name[0] = "123";
        name[1] = getPhone_byMember_UnitID(unitid);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                int i = 2;
                do {
                    name[i] = cursor.getString(cursor.getColumnIndex("MobileNumber")) == null ? "hi" : cursor.getString(cursor.getColumnIndex("MobileNumber"));
                    Log.d("mobilenumber3997", name[i]);
                    i++;
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return name;

    }

    public long insertSecurityGuard(int GuardID, Integer AssociationID, //Integer accountID,
                                    Integer OYEMemberID, Integer OYEMemberRoleID,
                                    String Name,
                                    Integer GuardRoleID, String MobileNumber, String Status
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", AssociationID);
//        initialValues.put("accountID",accountID);
        initialValues.put("OYEMemberID", OYEMemberID);
        initialValues.put("OYEMemberRoleID", OYEMemberRoleID);
        initialValues.put("GuardRoleID", GuardRoleID);
        initialValues.put("Name", Name);
//        initialValues.put("PatrollingTrackerID",0);
        initialValues.put("MobileNumber", MobileNumber);
        initialValues.put("GuardID", GuardID);
        initialValues.put("Status", Status);

        Cursor cursor = db.rawQuery("SELECT * FROM SecurityGuard where GuardID=" + GuardID
                + "  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + GuardID + " " + AssociationID);
            db.update("SecurityGuard ", initialValues, " GuardID=" + GuardID + "  ", null);

            cursor.close();
            return -1;
        } else {
            Log.d(" value", " inserted " + GuardID + " " + AssociationID + " ");
            cursor.close();
            return db.insert("SecurityGuard", null, initialValues);
        }

    }

    public String getMemberType_by(String membername) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MemberType FROM userdetails where username=" + membername;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
//            name=true;
            name = cur.getString(cur.getColumnIndex("MemberType"));
            Log.d("checkit", String.valueOf(name));
        }
        cur.close();
        return name;
    }

    public String getGuardName(int GuardID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = "Issue raised by Guard " + cur.getString(0);
        }
        cur.close();
        return name;

    }

    public String getGuardCreatedMemID(int GuardID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT OYEMemberID FROM SecurityGuard where GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", "  getGuardCreatedMemID " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = getOYEMemberName(cur.getInt(0));
        }
        cur.close();
        return name;

    }

    public String getGuardName1(int GuardID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(0) + "(Guard)";
        }
        cur.close();
        return name;

    }

    public int getGuardIDByName(String name, Integer assid) {
        int gid = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT GuardID FROM SecurityGuard where AssociationID=" + assid + " and Name=trim('" + name + "')";
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            gid = cur.getInt(0);
        }
        cur.close();
        return gid;

    }

    public ArrayList<String> getmemberName(Integer aasid) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT distinct Name FROM OyeMembers where AssociationID=" + aasid + "";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" suvarna 1860", " getOYEMemberName " + cur.getCount() + " " + sql);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                list.add(cur.getString(cur.getColumnIndex("Name")));
            } while (cur.moveToNext());


        }
        return list;

    }

    public String getGuardNameOnly(int GuardID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(0) + "";
        }
        cur.close();
        return name;

    }

    public String getGuardMobile(int GuardID) {
        String mob = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MobileNumber FROM SecurityGuard where GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            mob = cur.getString(0);
        }
        cur.close();
        return mob;

    }

    public Cursor getSecurityGuards(int AssociationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM SecurityGuard where AssociationID=" + AssociationId;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;
    }

    public int getGuardID(int AssociationId) {
        int guardId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM SecurityGuard where AssociationID=" + AssociationId;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            guardId = cur.getInt(cur.getColumnIndex("GuardID"));
        }
        cur.close();

        return guardId;

    }

//    public Cursor getData(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "SELECT * FROM StaffWorker where  StaffId="+id+"", null );
//        return res;
//    }

    public int getAssociationIDbyGuardID(int GuardID) {
        int guardId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM SecurityGuard where GuardID=" + GuardID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            guardId = cur.getInt(cur.getColumnIndex("AssociationID"));
        }
        cur.close();

        return guardId;

    }


//    public long insertStaffWorker(int associationID,int memberId, int staffId, int unitID , String mobileNumber,String name, String designation,String workerType,String unitName, int visitorCount, String visitorEntryTime, String visitorExitTime)
//    {
//        SQLiteDatabase db=this.getWritableDatabase();
//
//        ContentValues initialValues = new ContentValues();
//        initialValues.put("AssociationID", 273);
//        initialValues.put("MemberId",memberId);
//        initialValues.put("StaffId",staffId);
//        initialValues.put("UnitID",unitID);
//        initialValues.put("MobileNumber",mobileNumber);
//        initialValues.put("Name",name);
//        initialValues.put("Designation",designation);
//        initialValues.put("WorkerType",workerType);
//        initialValues.put("UnitName",unitName);
//        initialValues.put("VisitorCount",visitorCount);
//        initialValues.put("VisitorEntryTime",visitorEntryTime);
//        initialValues.put("VisitorExitTime",visitorExitTime);
//
//
//
//        Toast.makeText(context,initialValues.toString(),Toast.LENGTH_LONG).show();
//
//        Cursor cur = db.rawQuery(sql, null);
//        Log.d(" value315"," all count "+cur.getCount()+" ");
//        if(cur.getCount()>0){
//            cur.moveToFirst();
//            associationID =cur.getInt(cur.getColumnIndex("AssociationID"));
//        }
//        cur.close();
//
//
//            return db.insert("autoincrement", null, initialValues);
//
//
//
//        }
//
//    }

    public long insertVisitorData(String unitName, String associationID, String name, int memberId, int staffId, int unitID, String mobileNumber, String designation, String workerType, int visitorCount, String visitorEntryTime, String visitorExitTime) {

        System.out.println("DATA DATA" + unitName + ".." + associationID + ".." + name);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("UnitName", unitName);
        initialValues.put("AssociationID", associationID);
        initialValues.put("Name", name);
        initialValues.put("MemberId", memberId);
        initialValues.put("StaffId", staffId);
        initialValues.put("UnitID", unitID);
        initialValues.put("MobileNumber", mobileNumber);
        initialValues.put("Designation", designation);
        initialValues.put("WorkerType", workerType);
        initialValues.put("VisitorCount", visitorCount);
        initialValues.put("VisitorEntryTime", visitorEntryTime);
        initialValues.put("VisitorExitTime", visitorExitTime);


        Cursor cursor = db.rawQuery("SELECT * FROM VisitorData where Name=trim('" + name + "') ", null);

        if (cursor.getCount() > 0) {
            return -1;
        } else {
            cursor.close();


            return db.insert("VisitorData", null, initialValues);
        }

    }

    public long insertUserDetails(String uname, String finger_type, byte[] photo1, byte[] photo2, byte[] photo3, String MemberType, int aid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP1", photo1);
        initialValues.put("photo_FP2", photo2);
        initialValues.put("photo_FP3", photo3);
        initialValues.put("MemberType", MemberType);
        initialValues.put("AssociationID", aid);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" Dgddfd 2615", " updated " + uname + " " + finger_type + " " + MemberType);
            cursor.close();
            return -1;
        } else {
            Log.d(" Dgddfd 2618", " inserted " + uname + " " + finger_type + " " + MemberType);
            cursor.close();
            return db.insert("userdetails", null, initialValues);
        }

    }

    public long insertTempFInger(String uname, String finger_type, byte[] photo1, byte[] photo2, byte[] photo3, String MemberType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP1", photo1);
        initialValues.put("photo_FP2", photo2);
        initialValues.put("photo_FP3", photo3);
        initialValues.put("MemberType", MemberType);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" Dgddfd TempFinger 2615", " updated " + uname + " " + finger_type + " " + MemberType);
            cursor.close();
            return -1;
        } else {
            Log.d(" Dgddfd TempFinger 2618", " inserted " + uname + " " + finger_type + " " + MemberType);
            cursor.close();
            return db.insert("TempFinger", null, initialValues);
        }

    }

    public long insertUserFinger1(String uname, String finger_type, byte[] photo1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP1", photo1);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + uname + " " + finger_type);
            cursor.close();
//            db.update("userdetails ", initialValues," username=trim("+uname+") and finger_type=trim("+finger_type+") ", null);
            return -1;
        } else {
            Log.d(" value", " inserted " + uname + " " + finger_type);
            cursor.close();
            return db.insert("userdetails", null, initialValues);
        }

    }

    public long insertUserFinger2(String uname, String finger_type, byte[] photo2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP2", photo2);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "') and photo_FP2 is null ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + uname + " " + finger_type);
            cursor.close();
//            db.update("userdetails ", initialValues," username=trim("+uname+") and finger_type=trim("+finger_type+") ", null);
            return -1;
        } else {
            Log.d(" value", " inserted " + uname + " " + finger_type);
            cursor.close();
            return db.insert("userdetails", null, initialValues);
        }

    }

    public long insertUserFinger3(String uname, String finger_type, byte[] photo3) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP3", photo3);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "') and photo_FP3 is null ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + uname + " " + finger_type);
            cursor.close();
//            db.update("userdetails ", initialValues," username=trim("+uname+") and finger_type=trim("+finger_type+") ", null);
            return -1;
        } else {
            Log.d(" value", " inserted " + uname + " " + finger_type);
            cursor.close();
            return db.insert("userdetails", null, initialValues);
        }

    }

    public int getSgAttendID(Integer guardId, String EntryDateTime) {
        int RegVisitorLogID = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Attendance where GuardID=" + guardId + " " +
                " and StartTime like '%" + EntryDateTime + "%' and EndTime='0001-01-01T00:00:00' ", null);
        Log.d("count450", cursor.getCount() + "");
        Log.d("count451", +guardId + " " + EntryDateTime);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            RegVisitorLogID = cursor.getInt(cursor.getColumnIndex("AttendanceID"));
        }
        cursor.close();
        return RegVisitorLogID;
    }

    public List<String> mangers_IDs1(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT GuardID FROM SecurityGuard where AssociationID=trim('" + AssociationID + "')  ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public List<String> mangers_RoleIDs(Integer AssociationID) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT Name FROM SecurityGuard where AssociationID=trim('" + AssociationID + "')  ";

        Cursor cur = db.rawQuery(sql, null);
        cur.moveToFirst();
        if (cur.getCount() > 0) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return list;
    }

    public String getAdminNameID(int AssociationID, int memid) {
        String name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM OyeMembers where AssociationID =" + AssociationID + " and MemberID=" + memid + " ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" suvarna db", " MemberID " + cur.getCount() + " " + sql);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex("Name"));
        }
        cur.close();
        return name;
    }

    public int geUnitIDByName(String name, Integer assid) {
        int gid = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT distinct UnitID FROM OyeMembers where AssociationID=" + assid + " and Name=trim('" + name + "')";
        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            gid = cur.getInt(cur.getColumnIndex("UnitID"));
            Log.d("ravi db", String.valueOf(gid));

        }
        cur.close();
        return gid;

    }

    public Cursor getSecurityGuardById(int guardId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM Attendance where GuardID=" + guardId;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        return cur;

    }

    /*userdetails(usersno integer primary key autoincrement," +
                " username text not null, finger_type text not null , photo_FP1 BLOB, photo_FP2 BLOB, photo_FP3 BLOB*/
    public int[][] getNullBytes_arr(int AssociationID) {
        try {
            int[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT OYEFamilyMemberID FROM FamilyMembers where AssociationID=" + AssociationID + " " +
                    " ";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count sfsffs sl", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new int[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getInt(0);
//                        arrData[i][1] =cursor.getString(1);
//                        arrData[i][2] =  cursor.getString(2);
//                        arrData[i][3] =cursor.getString(3);
//                        arrData[i][4] =cursor.getString(4);
                        Log.d("data sfsffs", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)
                        );
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getName(String uname) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT username FROM userdetails where  username=trim('" + uname + "')  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value img", " exist " + cur.getCount() + " ");
        cur.close();

    }

    public Cursor getAllData2() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getAllData171() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails order by username desc  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" userdetails  ", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getAllData1(String username, String FingerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where username=trim('" + username + "')  and finger_type=trim('" + FingerName + "') ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value19522", " all count " + cur.getCount() + " ");
        Log.d(" value1953", username);
        Log.d(" value1954", FingerName);
        return cur;
    }

    public int getRVLogID_New(Integer VirtualID, String EntryDateTime) {
        int RegVisitorLogID = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RegularVisitorsLog where VirtualID=" + VirtualID + " " +
                " and ( EntryDateTime like '%" + EntryDateTime + "%' and ExitDateTime='0001-01-01T00:00:00'" +
                " or ExitDateTime='0001-01-01T00:00:00' or ExitDateTime is null) and ExitGuardID=0 ", null);
        Log.d("count450", cursor.getCount() + "");
        Log.d("count451", +VirtualID + " " + EntryDateTime);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            RegVisitorLogID = cursor.getInt(cursor.getColumnIndex("RegVisitorLogID"));
        }
        cursor.close();
        return RegVisitorLogID;
    }

    public boolean fingerExist_byMemberID(int MemberID) {
        boolean available = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where username=trim('" + MemberID + "') ";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            available = true;
        }
        Log.d(" value19522", " all count " + cur.getCount() + " " + MemberID + " ");
        cur.close();
        return available;
    }

    public boolean getMemberFingerExists(String username, String FingerName) {
        boolean available = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where username=trim('" + username + "')  and finger_type=trim('" + FingerName + "') and photo_FP1 not null ";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            available = true;
        }
        Log.d(" value19522", " all count " + cur.getCount() + " " + username + " " + FingerName);
        cur.close();
        return available;
    }

    public String getMemName(Integer OYEFamilyMemberID) {
        String accountName = "";
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM FamilyMembers where OYEFamilyMemberID=" + OYEFamilyMemberID + " ";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            accountName = cursor.getString(3) + " " + cursor.getString(4);
        }
        cursor.close();

        return accountName;
    }

    public int fingercount(int MemberID) {
        int available = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where username=trim('" + MemberID + "') ";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            available = cur.getCount();
        }
        Log.d(" value073", " all count " + cur.getCount() + " " + MemberID + " ");
        cur.close();
        return available;
    }

    public int[][] getMyAssociationIDs_arr() {
        try {
            int[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT distinct AssociationID FROM MyMembership ";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count dssrere sl", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new int[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getInt(0);
                        Log.d("data dssrere", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)
                        );
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateAnswer(String quebBank_id, int que_num, String ansGiven, String attempted) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            ContentValues args = new ContentValues();
//            args.put("corAns", corAns);
            args.put("ansGiven", ansGiven);
            args.put("attempted", attempted.substring(0, 1));

            db.update("testData ", args, " queBankId=trim(" + quebBank_id + ") and que_num=trim(" + que_num + ") ", null);
            Log.d(" value", " updated " + quebBank_id + " " + que_num);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Select All Data
    public String[][] getAllArrayFromLeaderBoard() {
        // TODO Auto-generated method stub

        try {
            String[][] arrData = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            Cursor cursor = db.rawQuery("SELECT * FROM LeaderBoard ", null);
            Log.d("count", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()][cursor.getColumnCount() + 1];

                    int i = 0;
                    do {
                        arrData[i][0] = String.valueOf(i + 1);
                        arrData[i][1] = cursor.getString(0);
                        arrData[i][2] = String.valueOf(cursor.getInt(1));
                        Log.d("data", cursor.getString(0) + " " + cursor.getString(1));
                        i++;
                    } while (cursor.moveToNext());

                }
            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Select All Data
    public String[][] getAllArrayFromTitles() {
        // TODO Auto-generated method stub

        try {
            String[][] arrData = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            Cursor cursor = db.rawQuery("SELECT titleid , titlename, bet, noofplayers, min_players, timeplay FROM viewTitle ", null);
            Log.d("count", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()][cursor.getColumnCount() + 1];

                    int i = 0;
                    do {
                        arrData[i][0] = String.valueOf(i + 1);
                        arrData[i][1] = String.valueOf(cursor.getInt(0));
                        arrData[i][2] = cursor.getString(1);
                        arrData[i][3] = String.valueOf(cursor.getInt(2));
                        arrData[i][4] = String.valueOf(cursor.getInt(3));
                        arrData[i][5] = String.valueOf(cursor.getInt(4));
                        arrData[i][6] = String.valueOf(cursor.getInt(5));

                        Log.d("data", cursor.getString(0) + " " + cursor.getString(1));
                        i++;
                    } while (cursor.moveToNext());

                }
            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long insertResidentVehicles(int vehicleid, int AssociationID, int OYEUnitID, int OYEMemberID,
                                       String VehicleNo, String VehicleType, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put("OYEVehicleId", vehicleid);
        initialValues.put("AssociationID", AssociationID);
        initialValues.put("OYEUnitID", OYEUnitID);
        initialValues.put("OYEMemberID", OYEMemberID);
        initialValues.put("VehicleNo", VehicleNo);
        initialValues.put("VehicleType", VehicleType);
        initialValues.put("Status", status);
        Log.d("getResidentsVehicles", " getResidentsLogVehicles count " + OYEMemberID + " " + VehicleNo);

        Cursor cursor = db.rawQuery("SELECT * FROM ResidentVehicles where OYEVehicleId= trim('" + vehicleid
                + "') and   OYEMemberID= trim('" + OYEMemberID + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", "vehicle updated " + VehicleNo + " " + VehicleType);
            db.update("ResidentVehicles ", initialValues, " OYEVehicleId= trim('" + vehicleid + "') and OYEMemberID= trim('" + OYEMemberID + "') ", null);
            return -1;
        } else {
            Log.d(" value", "vehicle inserted " + VehicleNo + " " + VehicleType);
            return db.insert("ResidentVehicles", null, initialValues);
        }
    }

    public Cursor updateResident_Vehiclestatus(int vehcileid, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE ResidentVehicles SET Status=trim('" + status + "') where OYEVehicleId=trim('" + vehcileid + "')";
        Cursor cur = db.rawQuery(sql, null);
        Log.d("thor", String.valueOf(cur.getCount()));
        return cur;

    }

    public void deleteAll_Vehicles(int UnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete  FROM ResidentVehicles where OYEUnitID=" + UnitID + " ";

        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" Dgddfdf  ", "Vehicles deleted ");
    }

    public int getVehicleCount(int UnitID) {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM ResidentVehicles where OYEUnitID=" + UnitID + " and Status='Active'";
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(" value319", " all count " + cursor.getCount() + " " + UnitID + " " + sql);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Cursor getResidentsLogVehicles() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM ResidentVehicles  ";//where EntryDateTime like ('%"+DateYMD+"%')

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value318", " getResidentsVehicles count " + cur.getCount() + " ");
        return cur;
    }

    public int[][] getRegularNullBytes_arr(int AssociationID) {
        try {
            int[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT OYEFamilyMemberID FROM FamilyMembers where AssociationID=" + AssociationID;
//                    " and VisitorType='"+DAILY_HELP+"'";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count sfsffs sl", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new int[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getInt(0);
//                        arrData[i][1] =cursor.getString(1);
//                        arrData[i][2] =  cursor.getString(2);
//                        arrData[i][3] =cursor.getString(3);
//                        arrData[i][4] =cursor.getString(4);
                        Log.d("data sfsffs", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)
                        );
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[][] getGuardNullBytes_arr(int AssociationID) {
        try {
            int[][] arrData = null;
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT GuardID FROM SecurityGuard where AssociationID=" + AssociationID;
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("count sfsffs sl", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new int[cursor.getCount()][cursor.getColumnCount()];

                    int i = 0;
                    do {
                        arrData[i][0] = cursor.getInt(0);
                        Log.d("data sfsffs", cursor.getString(0)//+" "+cursor.getString(2)+ " "+cursor.getString(3)
                        );
                        i++;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long insertUserFinger1(String uname, String finger_type, byte[] photo1, String MemberType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP1", photo1);
        initialValues.put("MemberType", MemberType);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "')  ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", "figer1 updated " + uname + " " + finger_type);
            db.update("userdetails ", initialValues, " username=trim(" + uname + ") and finger_type=trim(" + finger_type + ") ", null);
            return -1;
        } else {
            Log.d(" value", "figer1 inserted " + uname + " " + finger_type);
            return db.insert("userdetails", null, initialValues);
        }

    }

    public long insertUserFinger2(String uname, String finger_type, byte[] photo2, String MemberType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP2", photo2);
        initialValues.put("MemberType", MemberType);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "') and photo_FP2 is null ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + uname + " " + finger_type);
            db.update("userdetails ", initialValues, " username=trim(" + uname + ") and finger_type=trim(" + finger_type + ") ", null);
            return -1;
        } else {
            Log.d(" value", " inserted " + uname + " " + finger_type);
            return db.insert("userdetails", null, initialValues);
        }

    }

    public long insertUserFinger3(String uname, String finger_type, byte[] photo3, String MemberType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("username", uname);
        initialValues.put("finger_type", finger_type);
        initialValues.put("photo_FP3", photo3);
        initialValues.put("MemberType", MemberType);

        Cursor cursor = db.rawQuery("SELECT * FROM userdetails where username=trim('" + uname
                + "') and   finger_type=trim('" + finger_type + "') and photo_FP3 is null ", null);
        Log.d("count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            Log.d(" value", " updated " + uname + " " + finger_type);
            db.update("userdetails ", initialValues, " username=trim(" + uname + ") and finger_type=trim(" + finger_type + ") ", null);
            return -1;
        } else {
            Log.d(" value", " inserted " + uname + " " + finger_type);
            return db.insert("userdetails", null, initialValues);
        }

    }

    public String getCheckPointNameOnly(int CheckPointID) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT CheckPointName FROM RouteCheckPoints  where CheckPointsID=" + CheckPointID;

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value315", " all count " + cur.getCount() + " ");
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(0) + "";
        }
        cur.close();
        return name;

    }

    public String[][] getMobileNumberAndrelation(int unitid) {
        // TODO Auto-generated method stub

        try {
            String[][] arrData = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data


            Cursor cursor = db.rawQuery("SELECT * FROM FamilyMembers where VisitorType ='Family' and OYEUnitID =" + unitid, null);
            Log.d("count", cursor.getCount() + "");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount() + 2][cursor.getColumnCount() + 1];

                    int i = 2;
                    arrData[0][0] = "";
                    arrData[0][1] = "Select";

                    arrData[1][0] = getPhone_byMember_UnitID(unitid);
                    arrData[1][1] = "Primary";
                    do {
                        arrData[i][0] = cursor.getString(cursor.getColumnIndex("MobileNumber"));
                        arrData[i][1] = getFamilyMemberTypebyid(cursor.getInt(0));
//                        arrData[i][2] = String.valueOf( cursor.getInt(1));
                        Log.d("checkit 3527", cursor.getString(5) + " " + arrData[i][1]);
                        i++;
                    } while (cursor.moveToNext());

                }

            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("checkit 3527", "rajesh" + unitid);
            return null;
        }
    }

    public String getPhone_byMember_UnitID(int OyeUnitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MobileNumber FROM OyeMembers where UnitID=" + OyeUnitID + "";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();

            return cur.getString(cur.getColumnIndex("MobileNumber"));
        }
        Log.d(" 1634", " all count " + cur.getCount() + " ");
        cur.close();
        return "";
    }

    public Cursor getGuardCurPos(String date_YMD) {
        SQLiteDatabase db = this.getReadableDatabase();
//        String sql ="SELECT max(Time), guardID  FROM RouteTracker group by guardID ";
        String sql = "SELECT B.* From (select max(Time) Time, GuardID  FROM RouteTracker group by GuardID ) " +
                " A inner join RouteTracker B using (Time,GuardID) ";
        Cursor cur = db.rawQuery(sql, null);
        Log.d("GuardCurPos", " getGuardCurPos count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getGuardFingerPrint() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails  order by finger_type";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getGuards_byId(String Memid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where   username='" + Memid + "' order by finger_type";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" regvis_fing", " old count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getRegularVisitors_byId(String Memid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where username='" + Memid + "' order by finger_type";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" regvis_fing", " old count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getAllRegularVisitors_byId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails  ";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" regvis_fing", " old count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getRegularVisitorsFinger() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT u.*,r.EntryDateTime FROM userdetails u left join RegularVisitorsLog r on u.username==r.VirtualID where  " +
                "  u.photo_FP1 not null order by r.EntryDateTime desc  , u.finger_type ";
//        String sql ="SELECT r.EntryDateTime FROM  RegularVisitorsLog r  where " +
//                "  r.EntryDateTime like '%"+EntryDateTimeYMD+"%'  ";//and r.EntryDateTime like '%"+EntryDateTimeYMD+"%' group by u.username

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" regvis_fing", "datafps count " + cur.getCount() + " ");
        return cur;
    }

    public Cursor getRegularVisitorsFingerFiltered(String[] ids) {
        Log.d(" regvis_fing", "datafps count filtered bf  " + ids.length);
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT u.*,r.EntryDateTime FROM userdetails u left join RegularVisitorsLog r on u.username==r.VirtualID where  " +
                "  u.photo_FP1 not null and u.username IN (" + makePlaceholders(ids.length) + ") order by  u.finger_type , r.EntryDateTime desc  ";
//        String sql ="SELECT r.EntryDateTime FROM  RegularVisitorsLog r  where " +
//                "  r.EntryDateTime like '%"+EntryDateTimeYMD+"%'  ";//and r.EntryDateTime like '%"+EntryDateTimeYMD+"%' group by u.username

        Cursor cur = db.rawQuery(sql, ids);
        Log.d(" regvis_fing", "datafps count filtered " + cur.getCount() + " " + ids.length);
        return cur;
    }

    public Cursor getRegularVisitorsFingerFiltered(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM userdetails where  username='" + id + "' order by finger_type";

        Cursor cur = db.rawQuery(sql, null);
        Log.d(" regvis_fing", " old count " + cur.getCount() + " ");
        return cur;
    }

    String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
//            throw new RuntimeException("No placeholders");
            return "pp" + len;
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public Cursor getAllVehicle_byUnitID(Integer unitID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM ResidentVehicles where OYEUnitID=" + unitID + " and Status= 'Active'";

        Cursor cur = db.rawQuery(sql, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                Log.d("AAA", " ID" + cur.getInt(0));
                Log.d("AAA", "Assid" + cur.getInt(1));
                Log.d("AAA", " UnitID" + cur.getInt(2));
                Log.d("AAA", "Memid" + cur.getInt(3));
                Log.d("AAA", " Name" + cur.getString(4));
                Log.d("AAA", "Type" + cur.getString(5));
                Log.d("AAA", "Status" + cur.getString(6));
            } while (cur.moveToNext());
        }
        Log.d(" value", " all count " + cur.getCount() + " ");
        return cur;
    }

    public void deleteoldVehicles(int delete_count, int memid) {
        getAllVehicle_byUnitID(memid);
        //  Delete from table_name where rowid IN (Select rowid from table_name limit X);
        SQLiteDatabase db = this.getReadableDatabase();
        //   String sql="DELETE FROM ResidentVehicles where id IN(SELECT OYEVehicleId from ResidentVehicles ORDER BY OYEVehicleId ASC) LIMIT 5";

        String sql = "DELETE FROM ResidentVehicles WHERE OYEVehicleId IN ( SELECT OYEVehicleId FROM ResidentVehicles ORDER BY OYEVehicleId DESC LIMIT '" + delete_count + "' )";

        //   String ALTER_TBL ="delete from " + "ResidentVehicles" + " where OYEVehicleId IN (Select TOP  OYEVehicleId from " + "ResidentVehicles" + ")";
        SQLiteStatement st1 = db.compileStatement(sql);
        st1.executeInsert();
        Log.d(" vehicle123  ", "oyevehicles deleted ");
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
        }else{
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

    public int getTotalFingerPrints(){
        int available=0;
        Realm realm = Realm.getDefaultInstance();
        long avail = realm.where(FingerPrint.class).count();
        available = (int) avail;
        return available;
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

    public ArrayList<FingerPrint> getRegularVisitorsFingerPrint(int AssociationID) {

        Realm realm = Realm.getDefaultInstance();
        ArrayList<FingerPrint> fingerPrints = new ArrayList<>();
        fingerPrints.addAll(realm.where(FingerPrint.class).equalTo("ASAssnID", AssociationID).findAll());
        return fingerPrints;
    }

    public void saveVisitors(RealmList<VisitorLog> visitorsList) {
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

    public static ArrayList<VisitorLog> getVisitorEnteredLog(){
        Realm realm = Realm.getDefaultInstance();
        ArrayList<VisitorLog> list = new ArrayList<>();
        list.addAll(realm.where(VisitorLog.class).findAll().sort("vlVisLgID", Sort.DESCENDING));

//        Collections.sort(list, new Comparator<VisitorLog>() {
//            @Override
//            public int compare(VisitorLog o1, VisitorLog o2) {
//                return 0;
//            }
//        });
        realm.close();
        return list;
    }

	/*
	 // this code goes in mainactivity where database has to be created(ex in NBA app NBALoginAct.java)
	  DataBaseHelper dbh;
	 //DataBaseHelper object created...
		dbh = new DataBaseHelper(this);

		//creating DB.
		try
		{
			dbh.createDataBase();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		//CREATING NBAIMAGE DIRECTORY...
		File fDir= new File(this.getFilesDir()+"/sdcard/NBAIMG");
		if (!fDir.exists())
		{
			fDir.mkdir();
		}
		//CREATING NBA_DATABASE DIRECTORY...
		File DB_Dir= new File(this.getFilesDir()+"/sdcard/NBA_DATABASE");
		if (!DB_Dir.exists())
		{
			DB_Dir.mkdir();
		}
		//CREATING NBAI_XML DIRECTORY...
		File sdcard = Environment.getExternalStorageDirectory();
		File f=new File(sdcard+"/NbaXML");
		if (!f.exists())
		{
			f.mkdir();
		}
		try
		{
			file = new File(f,"VillageXML.xml");
			file.createNewFile();
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
		}
	 */

}
