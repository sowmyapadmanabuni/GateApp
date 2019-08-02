package com.oyespace.guards.com.oyespace.guards.fcm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.*
import com.oyespace.guards.com.oyespace.guards.activity.SosGateAppActivity
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel
import com.oyespace.guards.utils.LocalDb
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.delete
import io.realm.kotlin.where

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

                    var realm:Realm = Realm.getDefaultInstance()
                    realm.beginTransaction()
                    realm.delete(SOSModel::class.java)
                    realm.commitTransaction()

                    try {
                        dataSnapshot.children.forEach {
                            val user_id = it.key;
                            //val sos = it.getValue()
                            val isActive = it.child("isActive").getValue(Boolean::class.java)
                            var unitName = ""
                            var unitId: Int = 0
                            var userName:String = ""
                            var userMobile:String = ""
                            var sosImage:String = ""
                            var latitude:String = ""
                            var longitude:String = ""
                            var id:Int = 0
                            var userId: Int = 0

                            if(it.hasChild("unitName") && it.hasChild("unitName")!=null){
                                unitName = it.child("unitName").getValue(String::class.java)!!
                            }
                            if(it.hasChild("unitId") && it.hasChild("unitId")!=null){
                                unitId = it.child("unitId").getValue(Int::class.java)!!
                            }
                            if(it.hasChild("userName") && it.hasChild("userName")!=null){
                                userName = it.child("userName").getValue(String::class.java)!!
                            }
                            if(it.hasChild("userMobile") && it.hasChild("userMobile")!=null){
                                userMobile = it.child("userMobile").getValue(String::class.java)!!
                            }

                            Log.e("LATITUDE",""+it.child("latitude").getValue())

                            if(it.hasChild("latitude")){
                                latitude = it.child("latitude").getValue().toString()
                            }
                            if(it.hasChild("longitude")){
                                longitude = it.child("longitude").getValue().toString()
                            }
                            if(it.hasChild("sosImage")){
                                sosImage = it.child("sosImage").getValue(String::class.java)!!
                            }
                            if(it.hasChild("userId")){
                                userId = it.child("userId").getValue(Int::class.java)!!
                            }

                            if (isActive != null && isActive && userId != 0) {
                                realm.executeTransaction {

                                    val sosObj = it.createObject(SOSModel::class.java,userId)
                                    sosObj.isActive = isActive
                                    sosObj.unitName = unitName
                                    sosObj.unitId = unitId
                                    sosObj.userName = userName
                                    sosObj.userMobile = userMobile
                                    sosObj.latitude = latitude
                                    sosObj.longitude = longitude
                                    sosObj.sosImage = sosImage
                                }
                            }

                            Log.e("CHILD", "" + isActive);
                        }

                        val totalSOS = realm.where<SOSModel>().count()
                        Log.e("totalSOS",""+totalSOS);
                        if(totalSOS > 0){
                            val i_vehicle = Intent(applicationContext, SosGateAppActivity::class.java)
                            i_vehicle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i_vehicle)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
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