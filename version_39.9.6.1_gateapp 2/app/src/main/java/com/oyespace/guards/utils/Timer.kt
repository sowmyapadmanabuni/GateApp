package com.oyespace.guards.utils

import android.os.CountDownTimer

class TimerUtil(millisInFuture: Long, val onFinishCallback: OnFinishCallback?) : CountDownTimer(millisInFuture, 1000) {

    override fun onFinish() {
        onFinishCallback?.onFinish()
    }

    override fun onTick(millisUntilFinished: Long) {

    }

    interface OnFinishCallback {

        fun onFinish()

    }

}