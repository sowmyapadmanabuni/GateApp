package com.oyespace.guards.utils

import android.os.CountDownTimer

class TimerUtil(
    millisInFuture: Long,
    var onFinishCallback: () -> Unit = {},
    var onTickcallback: () -> Unit = {}
) : CountDownTimer(millisInFuture, 1000) {

    override fun onFinish() {
        onFinishCallback()
    }

    override fun onTick(millisUntilFinished: Long) {
        onTickcallback()
    }

}