/*
package com.oyespace.guards.snippet

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.goohungrry.ecode.R
import com.goohungrry.ecode.network.CommonDisposable
import com.goohungrry.ecode.network.RetrofitClinet
import com.goohungrry.ecode.pojo.GlobalApiObject
import com.goohungrry.ecode.pojo.SignUpReq
import com.goohungrry.ecode.utils.ConstantUtils
import com.goohungrry.ecode.utils.Utils
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_verify_otp.*

class VerifyOtpActivity : BaseKotlinActivity(), View.OnClickListener {

    private lateinit var signUpReq: SignUpReq

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        setUpToolbar("Verify otp", true)
        verifyBtn.setOnClickListener(this)
        signUpReq = intent.getParcelableExtra<SignUpReq>(ConstantUtils.DATA)
        if (signUpReq == null) {
            Utils.showToast(applicationContext, "Otp not sent")
            finish()
        }
        initializeListeners()
    }

    private fun initializeListeners() {
        compositeDisposable.add(RxTextView.textChanges(txt_otp).subscribe({ input_otp.error = null }))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.verifyBtn -> {
                val data = txt_otp.text.toString().trim()
                if (TextUtils.isEmpty(data)) {
                    input_otp.error = "Otp should not be empty"
                } else if (!data.equals(signUpReq.otp, true)) {
                    input_otp.error = "Entered wrong otp"
                } else {
                    createAccount(signUpReq)
                }
            }
        }
    }

    private fun createAccount(req: SignUpReq) {
        compositeDisposable.add(RetrofitClinet.instance.register(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GlobalApiObject<SignUpReq>>() {
                    override fun onSuccessResponse(globalApiObject: GlobalApiObject<SignUpReq>) {
                        if (globalApiObject.statuscode == 1) {
                            Utils.showToast(applicationContext, globalApiObject.statusMessage)
                            finish()
                        } else {
                            Utils.showToast(applicationContext, globalApiObject.statusMessage)
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Utils.showToast(applicationContext, getString(R.string.some_wrng))
                    }

                    override fun noNetowork() {
                        Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                        showProgress()
                    }

                    override fun onDismissProgress() {
                        dismissProgress()
                    }
                }))
    }

}
*/
