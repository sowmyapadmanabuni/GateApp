package com.oyespace.guards;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.oyespace.guards.utils.ConstantUtils.Emergency;


public class DataBaseHelper extends SQLiteOpenHelper {

    static final String dbName = "ghtest43.db";
    private static final int DATABASE_VERSION = 2;
    private static String DB_PATH;

    public DataBaseHelper(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        ContextWrapper cw = new ContextWrapper(context);

        //DB_PATH =cw.getFilesDir().getAbsolutePath()+ "/databases/";
        //  DB_PATH = "/data/data/" + context.getPackageName() + "/";
        DB_PATH = "/data/data/\" + context.getPackageName() + \"/databases/";
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

    public Cursor updatesecuritynotification_setNotified(int notificationID) {
        String value = "true";
        String lvalue = "false";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE SecurityNotification SET notified=trim('" + value + "') where Nid=trim('" + notificationID + "')";
        Cursor cur = db.rawQuery(sql, null);
        Log.d("thor", String.valueOf(cur.getCount()));
        return cur;

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


}
