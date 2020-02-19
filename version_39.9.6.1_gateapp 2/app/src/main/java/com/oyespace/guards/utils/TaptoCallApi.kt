package com.oyespace.guards.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.pojo.GetCallResponse
import com.oyespace.guards.zeotelapi.ZeotelRetrofitClinet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


class TaptoCallApi {

    companion object{

        var working_dialog: ProgressDialog? = null

        fun taptocallApi(gateMobileNumber: String, agentMobileNumber: String,context: Context) {

            showWorkingDialog(context)
            Handler().postDelayed(Runnable { removeWorkingDialog() }, 5000)

            val progressDialog = ProgressDialog(context)
            progressDialog.isIndeterminate = true
            progressDialog.setCancelable(true)
            progressDialog.setMessage("Please Wait!!")
            progressDialog.setCanceledOnTouchOutside(true)

            ZeotelRetrofitClinet.instance.getCall("KI_3t1wBwDQ2odmnvIclEdg-1391508276", "4000299", gateMobileNumber, agentMobileNumber, "120", "json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<String>() {

                    override fun onSuccessResponse(getdata: String) {
                        progressDialog.dismiss()

                    }

                    override fun onErrorResponse(e: Throwable) {
                        progressDialog.dismiss()
                    }

                    override fun noNetowork() {
                       // Toast.makeText(mcontext, "No network call ", Toast.LENGTH_LONG).show()
                    }
                })
        }

        private fun showWorkingDialog(context: Context) {
            working_dialog = ProgressDialog.show(context, "", "Please Wait!!...Call is initiating", true)
        }

        private fun removeWorkingDialog() {
            if (working_dialog != null) {
                working_dialog!!.dismiss()
                working_dialog = null
            }
        }
    }
}