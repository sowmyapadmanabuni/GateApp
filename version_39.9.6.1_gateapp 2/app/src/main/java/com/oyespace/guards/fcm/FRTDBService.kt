package com.oyespace.guards.com.oyespace.guards.fcm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.*
import com.oyespace.guards.com.oyespace.guards.activity.SosGateAppActivity
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel
import com.oyespace.guards.utils.LocalDb

class FRTDBService: Service() {

    private var mDatabase: DatabaseReference? = null
    private var mSosReference: DatabaseReference? = null
    private var mSosPath: String = "SOS"
    private val TAG = "SOS_SERVICE"

    override fun onCreate() {
        Log.e(TAG,"Started");
        super.onCreate()
        this.initFRTDB()
        this.initSOSListener()
    }

    private fun initFRTDB(){
        mSosPath+="/"+ LocalDb.getAssociation()!!.asAssnID
        Log.e(TAG,""+mSosPath)
        mDatabase = FirebaseDatabase.getInstance().reference
        mSosReference = FirebaseDatabase.getInstance().getReference(mSosPath)
    }

    private fun initSOSListener(){
        val sosListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("SOS_LISTEN",""+dataSnapshot)
                if (dataSnapshot.exists()) {

                   val child = dataSnapshot.hasChild(""+23)


                    dataSnapshot.children.forEach{
                        val user_id = it.key;
                        //val sos = it.getValue()
                        val isActive = it.child("isActive").getValue(Boolean::class.java)


                        Log.e("CHILD",""+isActive);
                    }


                    //val user = dataSnapshot.getValue(SOSModel::class.java)

                    //Log.e("SOS_OBJ",""+user)
                    //val i_vehicle = Intent(applicationContext, SosGateAppActivity::class.java)
                    //i_vehicle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(i_vehicle)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SOS_LISTEN","Error")
            }
        }
        mSosReference!!.addValueEventListener(sosListener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

}