package com.oyespace.guards.resident

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.oyespace.guards.BuildConfig
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.com.oyespace.guards.resident.ResidentChecker
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.databinding.ActivityMobileNumberBinding
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils
import kotlinx.android.synthetic.main.activity_mobile_number.*

class ResidentMissedCallActivity : BaseKotlinActivity() {

    lateinit var b: ActivityMobileNumberBinding
    lateinit var timer: CountDownTimer
    lateinit var receiver: BroadcastReceiver

    //    lateinit var countryCode: String
//    lateinit var residentMobileNumber: String
    var mobileNumberString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = DataBindingUtil.setContentView<ActivityMobileNumberBinding>(
            this,
            R.layout.activity_mobile_number
        )
        setupUI()

        timer = object : CountDownTimer(60_000, 1000) {
            override fun onFinish() {
                finish()
            }

            override fun onTick(ms: Long) {
                val remainedSecs: Int = (ms / 1000).toInt()
                val secs = (remainedSecs % 60)
                b.timer.text = (" 00:$secs")
            }

        }
        timer.start()

        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?) {
                val telephony =
                    context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephony.listen(object : PhoneStateListener() {

                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        super.onCallStateChanged(state, phoneNumber)
                        if (state == TelephonyManager.CALL_STATE_RINGING) {

                            val bundle = intent?.extras
                            val number = bundle?.getString("incoming_number")

                            if (textview != null && number != null) {

                                mobileNumberString = number
                                textview.text = number
//                                countryCode = number.substring(0, 3)
//                                residentMobileNumber = number.substring(3, 13)

                            }
                        }
                    }

                }, PhoneStateListener.LISTEN_CALL_STATE)

                //
            }
        }

        registerReceiver(receiver, IntentFilter("android.intent.action.PHONE_STATE"))


    }

    private fun setupUI() {

        buttonNext.setText(R.string.textdone)
        buttonSkip.visibility = View.GONE

        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, "")
        try {
            val appVersion = BuildConfig.VERSION_NAME
            txt_device_name.text = "V: $appVersion"
        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name.text = " "

        }

        val input = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_guardnumber.text = resources.getString(R.string.textgivemissedcall) + " +" + number


    }

    fun onClick(v: View) {

        if (mobileNumberString == null) {
            return
        }

        val ascId = LocalDb.getAssociation().asAssnID

        ResidentChecker().isResident(
            mobileNumberString!!,
            ascId,
            object : ResidentChecker.ResponseListener {

                override fun onResult(isResident: Boolean) {

                    if (isResident) {
                        Utils.getAlertDialog(this@ResidentMissedCallActivity, getString(R.string.valid), -1) {
                            finish()
                        }.show()
                    } else {
                        Utils.getAlertDialog(this@ResidentMissedCallActivity, getString(R.string.invalid), R.drawable.invalid_invi) {
                            finish()
                        }.show()
                    }
                    Log.d("check response", "resident: " + isResident)

                }

                override fun onError(error: String) {

                    Utils.getAlertDialog(
                        this@ResidentMissedCallActivity,
                        getString(R.string.invalid), R.drawable.invalid_invi
                    ) {
                        finish()
                    }.show()

                }

            })
    }

}
