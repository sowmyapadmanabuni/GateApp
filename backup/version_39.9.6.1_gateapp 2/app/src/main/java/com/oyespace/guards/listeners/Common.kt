package com.oyespace.guards.listeners

interface Common {

    interface OTPListener {
        fun onOTPReceived(otp:String)
    }
}