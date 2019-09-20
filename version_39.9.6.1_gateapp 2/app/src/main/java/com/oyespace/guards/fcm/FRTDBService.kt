package com.oyespace.guards.com.oyespace.guards.fcm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.*
import com.oyespace.guards.activity.SosGateAppActivity
import com.oyespace.guards.com.oyespace.guards.pojo.PassesSOSGuards
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel
import com.oyespace.guards.models.GetGuardsListResponse
import com.oyespace.guards.models.GuardsList
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where

class FRTDBService : Service() {

    private var mDatabase: DatabaseReference? = null
    private var mSosReference: DatabaseReference? = null
    private var mSosPath: String = "SOS"
    private val TAG = "SOS_SERVICE"

    override fun onCreate() {
        Log.e(TAG, "Started");
        super.onCreate()
        this.getGuardsList()
        this.initFRTDB()
        this.initSOSListener()

    }

    private fun initFRTDB() {
        mSosPath += "/" + LocalDb.getAssociation()!!.asAssnID
        Log.e(TAG, "" + mSosPath)
        mDatabase = FirebaseDatabase.getInstance().reference
        mSosReference = FirebaseDatabase.getInstance().getReference(mSosPath)
    }

    private fun getGuardsList() {
        RetrofitClinet.instance
            .getGuardsList(
                ConstantUtils.OYE247TOKEN,
                AppUtils.intToString(Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)),
                "Guard"
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetGuardsListResponse<GuardsList>>() {

                override fun onSuccessResponse(workerListResponse: GetGuardsListResponse<GuardsList>) {
                    Log.e("getGuardsList", "" + workerListResponse)
                    if (workerListResponse.data.workers != null) {
                        Prefs.putInt("TOTAL_GUARDS", workerListResponse.data.workers.size)
                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    Log.e("getGuardsList", "Err" + e)
                }

                override fun noNetowork() {

                }
            })

    }

    private fun initSOSListener() {
        val sosListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("SOS_LISTEN", "" + dataSnapshot)
                if (dataSnapshot.exists()) {

                    var realm: Realm = Realm.getDefaultInstance()
                    if (!realm.isInTransaction) {
                        realm.beginTransaction()
                    }
                    realm.delete(SOSModel::class.java)
                    realm.delete(PassesSOSGuards::class.java)
                    realm.commitTransaction()

                    var isGuardPassed: Boolean = false

                    try {
                        dataSnapshot.children.forEach {

                            isGuardPassed = false

                            val user_id = it.key;
                            //val sos = it.getValue()
                            val isActive = it.child("isActive").getValue(Boolean::class.java)
                            var unitName = ""
                            var unitId: Int = 0
                            var userName: String = ""
                            var userMobile: String = ""
                            var sosImage: String = ""
                            var latitude: String = ""
                            var longitude: String = ""
                            var id: Int = 0
                            var userId: Int = 0
                            var passedBy: HashMap<String, String> = HashMap()
                            var passedGuards: RealmList<PassesSOSGuards> = RealmList()
                            var attendedBy: String = ""

                            if (it.hasChild("unitName") && it.hasChild("unitName") != null) {
                                unitName = it.child("unitName").getValue(String::class.java)!!
                            }
                            if (it.hasChild("unitId") && it.hasChild("unitId") != null) {
                                unitId = it.child("unitId").getValue(Int::class.java)!!
                            }
                            if (it.hasChild("userName") && it.hasChild("userName") != null) {
                                userName = it.child("userName").getValue(String::class.java)!!
                            }
                            if (it.hasChild("userMobile") && it.hasChild("userMobile") != null) {
                                userMobile = it.child("userMobile").getValue(String::class.java)!!
                            }

                            Log.e("LATITUDE", "" + it.child("latitude").getValue())

                            if (it.hasChild("latitude")) {
                                latitude = it.child("latitude").getValue().toString()
                            }
                            if (it.hasChild("longitude")) {
                                longitude = it.child("longitude").getValue().toString()
                            }
                            if (it.hasChild("sosImage")) {
                                sosImage = it.child("sosImage").getValue(String::class.java)!!
                            }
                            if (it.hasChild("userId")) {
                                userId = it.child("userId").getValue(Int::class.java)!!
                            }

                            if (it.hasChild("attendedBy")) {
                                attendedBy = it.child("attendedBy").getValue(String::class.java)!!
                            }


                            val currentGate = Prefs.getString(ConstantUtils.GATE_NO, "");

                            if (isActive != null && isActive && userId != 0) {

                                if (it.hasChild("passedby")) {
                                    val type =
                                        object : GenericTypeIndicator<HashMap<String, String>?>() {}
                                    passedBy = HashMap()
                                    passedBy = it.child("passedby").getValue(type)!!


                                    var gates = passedBy.keys;
                                    for (gate in gates) {
                                        realm.executeTransaction {
                                            val passedGuard =
                                                it.createObject(PassesSOSGuards::class.java)

                                            // var passedGuard:PassesSOSGuards = PassesSOSGuards()
                                            passedGuard.gateName = gate
                                            passedGuard.passedTime = passedBy[gate]!!
                                            passedGuards.add(passedGuard)

                                            if (currentGate.equals(gate)) {
                                                isGuardPassed = true
                                            }
                                        }
                                    }

                                    Log.e("passedby", "" + passedBy);
                                }

                                Log.e(
                                    "ISACTIVE_",
                                    "" + isGuardPassed + " " + attendedBy + " " + currentGate + " - " + attendedBy.equals(
                                        currentGate,
                                        true
                                    ) + " " + attendedBy.trim().length + " " + currentGate.trim().length
                                );
                                if (!isGuardPassed) {
                                    Log.e("INSIDE", "GUARDPASSED")
                                    if (attendedBy == "" || attendedBy.trim().equals(currentGate.trim())) {
                                        Log.e("INSIDE", "attendedBy")
                                        realm.executeTransaction {

                                            val sosObj =
                                                it.createObject(SOSModel::class.java, userId)
                                            sosObj.isActive = isActive
                                            sosObj.unitName = unitName
                                            sosObj.unitId = unitId
                                            sosObj.userName = userName
                                            sosObj.userMobile = userMobile
                                            sosObj.latitude = latitude
                                            sosObj.longitude = longitude
                                            sosObj.sosImage = sosImage
                                            sosObj.passedBY = passedGuards
                                            sosObj.attendedBy = attendedBy
                                        }
                                    }
                                }


                            }

                            Log.e("CHILD", "" + isActive);
                        }

                        val totalSOS = realm.where<SOSModel>().count()
                        Log.e("totalSOS", "" + totalSOS);
                        val isSOSActive = Prefs.getBoolean("ACTIVE_SOS", false);

                        Log.e("ANY_SOS?", "" + isSOSActive)



                        if (totalSOS > 0 && !isSOSActive) {
                            Log.e("STARTING", "SOS>STSRTSTS");
                            val i_vehicle =
                                Intent(applicationContext, SosGateAppActivity::class.java)
                            i_vehicle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i_vehicle)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SOS_LISTEN", "Error")
            }
        }
        mSosReference!!.addValueEventListener(sosListener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

}