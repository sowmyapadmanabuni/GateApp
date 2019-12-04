package com.oyespace.guards.repo

import android.content.Context
import android.content.Intent
import android.util.Log
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.models.*
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.VisitorExitReq
import com.oyespace.guards.pojo.VisitorExitResp
import com.oyespace.guards.pojo.VisitorLogResponse
import com.oyespace.guards.realm.VisitorEntryLogRealm
import com.oyespace.guards.realm.VisitorExitLogRealm
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.removeFBNotificationSyncEntry
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class VisitorLogRepo {

    companion object {

        // NOTE: if  fetching from backend, the function returns null. Use the listener instead to get the log
        fun get_IN_VisitorLog(
            updatedFromBackend: Boolean = false,
            listener: VisitorLogFetchListener? = null
        ): ArrayList<VisitorLog>? {

            if (updatedFromBackend) {
                RetrofitClinet.instance
                    .getVisitorLogEntryList(ConstantUtils.OYE247TOKEN, Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :
                        CommonDisposable<GetVisitorsResponse<ArrayList<VisitorLogResponse>>>() {

                        override fun onSuccessResponse(response: GetVisitorsResponse<ArrayList<VisitorLogResponse>>) {

                            Log.e("get_IN_VisitorLog", "" + response)
                            if (response.success) {
                                val visitorsList = response.data.visitorLog
                                if (visitorsList == null) {
                                    VisitorEntryLogRealm.deleteAllVisitorLogs()
                                    listener?.onFetch(null, "no entries found")
                                } else {
                                    VisitorEntryLogRealm.updateVisitorLogs(visitorsList)
                                    listener?.onFetch(getOverstaySortedList())
                                }
                            } else {
                                listener?.onFetch(null)
                            }

                        }

                        override fun onErrorResponse(e: Throwable) {
                            listener?.onFetch(null, e.message)
                        }

                        override fun noNetowork() {
                            listener?.onFetch(null, "no network")
                        }
                    })
                return null
            } else {

                val logs = getOverstaySortedList()
                listener?.onFetch(logs)
                return logs

            }

        }

        fun get_IN_VisitorForId(id: Int): VisitorLog? {
            return VisitorEntryLogRealm.getVisitorForId(id)
        }

        fun get_IN_VisitorForVisitorId(id: String, fromBackend: Boolean = false, callback: (visitor: VisitorLog?) -> Unit = {}): VisitorLog? {
            if (fromBackend) {
                RetrofitClinet.instance
                    .getVisitorEntryForId(OYE247TOKEN, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :
                        CommonDisposable<GetVisitorForIdResponse>() {
                        override fun onSuccessResponse(response: GetVisitorForIdResponse) {

                            Log.i("taaag", "updated visitor from backend: ${response.data}, ${response.data.visitorLog.vlVenImg}, ${response.data.visitorLog.vlVoiceNote}")
                            VisitorEntryLogRealm.updateVisitorLog(response.data.visitorLog)
                            callback(VisitorEntryLogRealm.getVisitorForVisitorId(id.toInt()))

                        }

                        override fun onErrorResponse(e: Throwable) {
                            callback(null)
                        }

                        override fun noNetowork() {
                            callback(null)
                        }
                    })
                return null
            }
            return VisitorEntryLogRealm.getVisitorForVisitorId(id.toInt())
        }

        fun get_IN_VisitorsForPhone(phone: String): ArrayList<VisitorLog>? {
            return VisitorEntryLogRealm.getVisitorsForMobile(phone)
        }

        fun get_IN_PendingVisitorsForName(name: String): ArrayList<VisitorLog>? {
            return VisitorEntryLogRealm.getPendingVisitorsForName(name)
        }

        fun get_IN_VisitorsForTimeTime(time: String): ArrayList<VisitorLog>? {
            val str = time.split("T")
            return VisitorEntryLogRealm.getVisitorsForDateTime(str[0], str[1])
        }

        fun delete_IN_Visitor(lgid: Int) {
            VisitorEntryLogRealm.deleteVisitor(lgid)
        }

        fun check_IN_VisitorByPhone(phone: String): Boolean {
            return VisitorEntryLogRealm.entryExists(phone)

        }

        fun check_IN_StaffVisitorByPhone(phone: String?): Boolean {
            return if (phone != null)
                VisitorEntryLogRealm.staffEntryExists(phone)
            else {
                false
            }

        }

        fun search_IN_Visitors(searchStr: String): ArrayList<VisitorLog>? {
            if (searchStr.isEmpty()) {
                return getOverstaySortedList()
            } else {
                return VisitorEntryLogRealm.searchVisitorLog(searchStr)
            }
        }

        fun getOverstaySortedList(): ArrayList<VisitorLog>? {

            Log.i("taaag", "getting overstaying sorted list")

            val listFromRealm = VisitorEntryLogRealm.getVisitorEntryLog()

            val overStaying = ArrayList<VisitorLog>()
            val underStaying = ArrayList<VisitorLog>()

            for (vl in listFromRealm) {

                val actTime = vl.vlsActTm
                val status = vl.vlApprStat

                if (status.equals(APPROVED, true)) {

                    val msLeft = DateTimeUtils.msLeft(actTime, MAX_DELIVERY_ALLOWED_SEC)
                    Log.v("taaag", "vlID: ${vl.vlVisLgID} actTIme: ${actTime} entryTime: ${vl.vlEntryT} msLeft: $msLeft")
                    if (msLeft <= 0) {
                        if (status.equals(APPROVED, true)) {
                            overStaying.add(vl)
                            Log.v("taaag", "overstaying: ${vl.vlVisLgID} actIme: ${actTime} entryTime: ${vl.vlEntryT} msLeft: $msLeft")
                        }
                    } else {
                        underStaying.add(vl)
                    }

                } else {
                    underStaying.add(vl)
                }

            }
            Log.d("taaag", "got visitorLog from realm with ${overStaying.size} overtaying and ${underStaying.size} understaying")
            overStaying.addAll(underStaying)
            return overStaying

        }

        fun getUnitCountForVisitor(phone: String): Int {
            return VisitorEntryLogRealm.getUnitCountForVisitor(phone)
        }

        fun updateVisitorStatus(context: Context, visitor: VisitorLog, status: String, onlyLocalUpdate: Boolean = false) {

            val vLogId = visitor.vlVisLgID

            if (onlyLocalUpdate) {
                VisitorEntryLogRealm.updateVisitorStatus(visitor, status)
                return
            }

            val req = VisitorExitReq(0, vLogId, Prefs.getString(ConstantUtils.GATE_NO, ""), status)
            CompositeDisposable().add(
                RetrofitClinet.instance.visitorExitCall("7470AD35-D51C-42AC-BC21-F45685805BBE", req)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                        override fun onSuccessResponse(globalApiObject: VisitorExitResp) {

                            if (globalApiObject.success) {

                                removeFBNotificationSyncEntry(vLogId)

                                var s = Prefs.getString(ConstantUtils.SP_DEL_FB_IDs, "")
                                s += "$vLogId,"
                                Prefs.putString(ConstantUtils.SP_DEL_FB_IDs, s)

//                                val intentAction1 = Intent(context, BackgroundSyncReceiver::class.java)
//                                intentAction1.putExtra(ConstantUtils.BSR_Action, ConstantUtils.SENDFCM_toSYNC_VISITORENTRY)
//                                context.sendBroadcast(intentAction1)
                                val assName = LocalDb.getAssociation()!!.asAsnName
                                val gateName = Prefs.getString(GATE_NO, null)
                                var message = "${visitor.vlfName} from ${visitor.vlComName} has exited ${assName} from $gateName"
                                if (visitor.vlVisType.contains(STAFF)) {
                                    message = "${visitor.vlComName} ${visitor.vlfName} from  has exited ${assName} from $gateName"
                                }

                                if (status == ConstantUtils.EXITED) {
                                    try {
                                        if (visitor.isValid) {
                                            val d = Intent(context, BackgroundSyncReceiver::class.java)
                                            d.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
                                            d.putExtra("msg", message)
                                            d.putExtra("mobNum", visitor.vlMobile)
                                            d.putExtra("name", visitor.vlfName)
                                            d.putExtra("nr_id", visitor.vlVisLgID.toString())
                                            d.putExtra("unitname", visitor.unUniName)
                                            d.putExtra("memType", "Owner")
                                            d.putExtra(ConstantUtils.UNITID, visitor.unUnitID)
                                            d.putExtra(ConstantUtils.COMPANY_NAME, visitor.vlComName)
                                            d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID, visitor.unUnitID)
                                            d.putExtra("VLVisLgID", visitor.vlVisLgID)
                                            d.putExtra(ConstantUtils.VISITOR_TYPE, visitor.vlVisType)
                                            d.putExtra(ConstantUtils.SEND_NOTIFICATION, false)
                                            context.sendBroadcast(d)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }


                            }
                        }

                        override fun onErrorResponse(e: Throwable) {

                        }

                        override fun noNetowork() {

                        }

                        override fun onShowProgress() {
//                        showProgress()
                        }

                        override fun onDismissProgress() {
//                        dismissProgress()
                        }
                    })

            )

        }

        fun get_OUT_VisitorLog(
            updatedFromBackend: Boolean = false,
            listener: ExitVisitorLogFetchListener? = null
        ): ArrayList<ExitVisitorLog>? {

            if (updatedFromBackend) {

                RetrofitClinet.instance
                    .getVisitorLogExitList(ConstantUtils.OYE247TOKEN, Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CommonDisposable<GetExitVisitorsResponse<ArrayList<VisitorLogResponse>>>() {
                        override fun onSuccessResponse(response: GetExitVisitorsResponse<ArrayList<VisitorLogResponse>>) {

                            if (response.success) {
                                val visitorlog = response.data.visitorLog
                                if (visitorlog == null) {
                                    VisitorExitLogRealm.deleteVisitorLogs()
                                    listener?.onFetch(null, "no entries found")
                                } else {
                                    if (listener == null) {
                                        VisitorExitLogRealm.updateVisitorLogs(visitorlog, null)
                                    } else {
                                        VisitorExitLogRealm.updateVisitorLogs(visitorlog, object : VisitorExitLogRealm.ExitLogUpdateListener {
                                            override fun onUpdateFinish(exitLogs: java.util.ArrayList<ExitVisitorLog>?) {
                                                listener.onFetch(exitLogs)
                                            }

                                        })
                                    }
                                }
                            } else {
                                listener?.onFetch(null)
                            }

                        }

                        override fun onErrorResponse(e: Throwable) {
                            listener?.onFetch(null, e.message)
                        }

                        override fun noNetowork() {
                            listener?.onFetch(null, "no network")
                        }

                    })

                return null

            } else {

                val logs = VisitorExitLogRealm.getVisitorExitLog()
                listener?.onFetch(logs)
                return logs

            }

        }

        fun search_OUT_Visitors(search: String): ArrayList<ExitVisitorLog>? {

            val list = VisitorExitLogRealm.searchVisitorLog(search)
            return list


        }

        fun exitYesterdaysINEntries() {

            // TODO work on this
//            get_IN_VisitorLog(true, object : VisitorLogFetchListener{
//                override fun onFetch(visitorLog: ArrayList<VisitorLog>?, error: String?) {
//
//                    if()
//
//                }
//            })


        }

        fun allowEntry(ccd: String?, mobileNumber: String?, ignoreType: Boolean = false): Boolean {


            val entryExists = check_IN_VisitorByPhone(ccd + mobileNumber)

            if (entryExists) {

                if (mobileNumber != null) {

                    val visitorLog = get_IN_VisitorsForPhone(mobileNumber)

                    if (visitorLog != null) {
                        for (v in visitorLog) {

                            val apprStat = v.vlApprStat
                            Log.d("taaag", "approval status: $apprStat for $mobileNumber, ${v.vlVisType}, ${v.unUniName}, ${v.vlVisType}")
                            if (v.vlVisType.contains(DELIVERY, true) || ignoreType) {
                                if (apprStat.equals(APPROVED, true)) {
                                    return false
                                }
                            } else {
                                return false
                            }

                        }
                    }

                }

            }

            return true

        }

    }

    interface VisitorLogFetchListener {

        fun onFetch(visitorLog: ArrayList<VisitorLog>?, error: String? = "")

    }

    interface ExitVisitorLogFetchListener {

        fun onFetch(visitorLog: ArrayList<ExitVisitorLog>?, error: String? = "")

    }
}

