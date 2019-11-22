package com.oyespace.guards.repo

import android.util.Log
import com.oyespace.guards.models.GetWorkersResponse
import com.oyespace.guards.models.Worker
import com.oyespace.guards.models.WorkersList
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.realm.RealmDB
import com.oyespace.guards.realm.StaffRealm
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults


class StaffRepo {

    companion object {

        fun getStaffList(
            updateFrombackend: Boolean = false,
            listener: StaffFetchListener? = null
        ): ArrayList<Worker>? {

            if (updateFrombackend) {

                RetrofitClinet.instance
                    .workerList(ConstantUtils.OYE247TOKEN, AppUtils.intToString(LocalDb.getAssociation().asAssnID))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CommonDisposable<GetWorkersResponse<WorkersList>>() {

                        override fun onSuccessResponse(workerListResponse: GetWorkersResponse<WorkersList>) {


                            if (workerListResponse.data.worker != null) {
                                val realm = Realm.getDefaultInstance()
                                val arrayList = workerListResponse.data.worker

                             //   val result: RealmResults<Worker> = realm.where(Worker::class.java).findAllSorted("wkfName")
                                if (arrayList == null) {
                                    listener?.onFetch(null)
                                } else {
                                    StaffRealm.updateStaffsList(arrayList)
                                    listener?.onFetch(StaffRealm.getStaff())
                                }

                            } else {
                                listener?.onFetch(null)
                            }
                        }

                        override fun onErrorResponse(e: Throwable) {
                            Log.d("Error WorkerList", e.toString())
                            listener?.onFetch(null)
                        }

                        override fun noNetowork() {
                            listener?.onFetch(null)
                        }
                    })

                return null

            } else {

                val staff = StaffRealm.getStaff()
                listener?.onFetch(staff)
                return staff

            }

        }

        fun getStaffForId(id: Int): Worker {
            return StaffRealm.getStaffForId(id)
        }

        fun checkExistingStaffForPhone(phone: String): Boolean {
            return StaffRealm.staffForPhoneExists(phone)
        }

        fun getFingersForStaff(staffID: Int) = RealmDB.fingercount(staffID)



        fun search_Staff(search: String): ArrayList<Worker>? {
            if (search.isEmpty()) {
                return getStaffList()
            } else {
                return StaffRealm.searchVisitorLog(search)
            }
        }

    }

    interface StaffFetchListener {

        fun onFetch(staff: ArrayList<Worker>?)

    }


}