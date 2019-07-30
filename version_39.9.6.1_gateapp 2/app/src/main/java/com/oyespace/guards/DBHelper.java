package com.oyespace.guards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DASHBOARDCOUNT.db";
    public static final String CONTACTS_TABLE_NAME = "referencecount";
    public static final String COUNT_COLUMN_ID = "id";
    public static final String COUNT_COLUMN_NAME = "name";
    private final Context context;



    public DBHelper(Context context) {


        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
       db.execSQL(
                "create table referencecount " +
                        "(id integer primary key, name text)"
        );

        String CREATE_StaffWorker_TABLE = " create table IF NOT EXISTS StaffWorker(StaffWorkeID integer primary key autoincrement,AssociationID integer ," +
                " MemberId integer, StaffId integer, UnitID integer , MobileNumber text not null , Name VARCHAR(40), Designation VARCHAR(50), WorkerType VARCHAR(50),UnitName VARCHAR(50),VisitorCount integer, VisitorEntryTime DateTime2(7), VisitorExitTime DateTime2(7)) ";
        db.execSQL(CREATE_StaffWorker_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS referencecount");
        onCreate(db);
    }

    public boolean insertContact (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        db.insert("referencecount", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
       // Cursor res =  db.rawQuery( "select * from referencecount where id="+id+"", null );
        Cursor res =  db.rawQuery( "select * from referencecount where id="+id+"", null );

        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public long insertStaffWorker(int associationID,int memberId, int staffId, int unitID , String mobileNumber,String name, String designation,String workerType,String unitName, int visitorCount, String visitorEntryTime, String visitorExitTime)
    {
        SQLiteDatabase db=this.getWritableDatabase();

//        ContentValues initialValues = new ContentValues();
//        initialValues.put("AssociationID", associationID);
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


        ContentValues initialValues = new ContentValues();
        initialValues.put("AssociationID", 273);
        initialValues.put("MemberId",410);
        initialValues.put("StaffId",11887);
        initialValues.put("UnitID",6885);
        initialValues.put("MobileNumber",mobileNumber);
        initialValues.put("Name","Lucky");
        initialValues.put("Designation","Maid");
        initialValues.put("WorkerType","Staff");
        initialValues.put("UnitName","A101");
        initialValues.put("VisitorCount",1);
        initialValues.put("VisitorEntryTime",visitorEntryTime);
        initialValues.put("VisitorExitTime",visitorExitTime);

        Toast.makeText(context,initialValues.toString(),Toast.LENGTH_LONG).show();


//
//       // SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.insert("contacts", null, contentValues);
//        return true;
//    }


        //  Toast.makeText(context,associationID+".."+memberId+".."+staffId+".."+unitID+".."+mobileNumber+".."+name+".."+designation+".."+workerType+".."+unitName+".."+visitorCount+".."+visitorEntryTime+".."+visitorExitTime,Toast.LENGTH_LONG).show();

//        Cursor cursor = db.rawQuery("SELECT * FROM StaffWorker where StaffId=trim('"+staffId
//                +"') and   Name=trim('"+name+"')  ", null);

//        String sql ="SELECT * FROM StaffWorker";
//
//        Cursor cursor = db.rawQuery(sql, null);
//
//      //  Log.d("count",cursor.getCount()+"");
//
//       // Toast.makeText(context,cursor.getCount()+"",Toast.LENGTH_LONG).show();
//
//        if(cursor.getCount() >0)
//        {
//
//           // Toast.makeText(context,"coming1",Toast.LENGTH_LONG).show();
//
//            cursor.close();
//            return -1;
//        }else{
//           // Toast.makeText(context,"coming2",Toast.LENGTH_LONG).show();
//            cursor.close();
        return db.insert("StaffWorker", null, initialValues);


        // }

    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from referencecount", null );
        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//            res.moveToNext();
//        }
        return array_list;
    }
}
