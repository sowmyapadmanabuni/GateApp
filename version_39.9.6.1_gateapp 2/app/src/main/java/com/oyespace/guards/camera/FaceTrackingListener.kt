package com.oyespace.guards.camera


interface FaceTrackingListener {
    fun onFaceLeftMove()
    fun onFaceRightMove()
    fun onFaceUpMove()
    fun onFaceDownMove()
    fun onGoodSmile()
    fun onEyeCloseError()
    fun onMouthOpenError()
    fun onMultipleFaceError()

}
