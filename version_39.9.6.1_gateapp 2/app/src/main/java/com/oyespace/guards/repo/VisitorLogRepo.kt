package com.oyespace.guards.repo

import android.content.Context
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.models.ExitVisitorLog
import com.oyespace.guards.models.GetExitVisitorsResponse
import com.oyespace.guards.models.GetVisitorsResponse
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.VisitorExitReq
import com.oyespace.guards.pojo.VisitorExitResp
import com.oyespace.guards.pojo.VisitorLogResponse
import com.oyespace.guards.realm.VisitorEntryLogRealm
import com.oyespace.guards.realm.VisitorExitLogRealm
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.DateTimeUtils
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
        ): ArrayList<com.oyespace.guards.models.VisitorLog>? {

            if (updatedFromBackend) {
                RetrofitClinet.instance
                    .getVisitorLogEntryList(ConstantUtils.OYE247TOKEN, Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :
                        CommonDisposable<GetVisitorsResponse<ArrayList<VisitorLogResponse>>>() {

                        override fun onSuccessResponse(response: GetVisitorsResponse<ArrayList<VisitorLogResponse>>) {


                            if (response.success) {
                                val visitorsList = response.data.visitorLog
                                if (visitorsList == null) {
                                    VisitorEntryLogRealm.deleteAllVisitorLogs()
                                    listener?.onFetch(null, "no entries found")
                                } else {
                                    VisitorEntryLogRealm.updateVisitorLogs(visitorsList)
                                    listener?.onFetch(VisitorEntryLogRealm.getVisitorEntryLog())
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

                val logs = VisitorEntryLogRealm.getVisitorEntryLog()
                listener?.onFetch(logs)
                return logs

            }

        }

        fun get_IN_VisitorForId(id: Int): com.oyespace.guards.models.VisitorLog? {
            return VisitorEntryLogRealm.getVisitorForId(id)
        }

        fun get_IN_VisitorsForPhone(phone: String): ArrayList<com.oyespace.guards.models.VisitorLog>? {
            return VisitorEntryLogRealm.getVisitorsForMobile(phone)
        }

        fun delete_IN_Visitor(lgid: Int) {
            VisitorEntryLogRealm.deleteVisitor(lgid)
        }

        fun check_IN_VisitorByPhone(phone: String): Boolean {
            return VisitorEntryLogRealm.entryExists(phone)
        }

        fun search_IN_Visitors(search: String): ArrayList<com.oyespace.guards.models.VisitorLog>? {
            return VisitorEntryLogRealm.searchVisitorLog(search)
        }

        fun getUnitCountForVisitor(phone: String): Int {
            return VisitorEntryLogRealm.getUnitCountForVisitor(phone)
        }

        fun exitVisitor(context: Context, vLogId: Int) {

            val req = VisitorExitReq(DateTimeUtils.getCurrentTimeLocal(), 0, vLogId, Prefs.getString(ConstantUtils.GATE_NO, ""))
            CompositeDisposable().add(
                RetrofitClinet.instance.visitorExitCall("7470AD35-D51C-42AC-BC21-F45685805BBE", req)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                        override fun onSuccessResponse(globalApiObject: VisitorExitResp) {

                            if (globalApiObject.success) {

                                // update exit log from backend
                                delete_IN_Visitor(vLogId)

                                val ref = FirebaseDatabase.getInstance().getReference("NotificationSync")
                                ref.child(vLogId.toString()).removeValue()

                                val intentAction1 = Intent(context, BackgroundSyncReceiver::class.java)
                                intentAction1.putExtra(ConstantUtils.BSR_Action, ConstantUtils.SENDFCM_toSYNC_VISITORENTRY)
                                context.sendBroadcast(intentAction1)

                            } else {

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

    }

    interface VisitorLogFetchListener {

        fun onFetch(visitorLog: ArrayList<com.oyespace.guards.models.VisitorLog>?, error: String? = "")

    }

    interface ExitVisitorLogFetchListener {

        fun onFetch(visitorLog: ArrayList<ExitVisitorLog>?, error: String? = "")

    }
}

