package com.oyespace.guards.network



import com.oyespace.guards.Myapp
import com.oyespace.guards.utils.Utils
import io.reactivex.observers.DisposableSingleObserver

/**
 * Created by Kalyan on 10/14/2017.
 */
abstract class CommonDisposable<T> : DisposableSingleObserver<T>() {


    init {
        if (!Utils.isOnline(Myapp.getContext())) {
            noNetowork()
        }
        onShowProgress()
    }

    override fun onError(e: Throwable) {
        onErrorResponse(e)
        onDismissProgress()
    }

    override fun onSuccess(t: T) {
        onSuccessResponse(t)
        onDismissProgress()
    }

    open fun onDismissProgress() {

    }

    open fun onShowProgress() {

    }

    abstract fun onSuccessResponse(t: T)
    abstract fun onErrorResponse(e: Throwable)
    abstract fun noNetowork()
}