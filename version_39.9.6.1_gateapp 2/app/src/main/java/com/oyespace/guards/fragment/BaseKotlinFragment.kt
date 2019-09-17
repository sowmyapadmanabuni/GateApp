package com.oyespace.guards.fragment

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat

import android.util.Log
import com.oyespace.guards.listeners.PermissionCallback
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * Created by Kalyan on 21-Oct-17.
 */
open class BaseKotlinFragment : Fragment() {

    private var callback: PermissionCallback? = null
    private var mRequestcode: Int = 0

    protected val compositeDisposable = CompositeDisposable()

    companion object {
        val TAG = "BaseKotlinFragment"
    }


    fun isPemissionAllowed(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!.applicationContext,
                permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(permission: String, requestcode: Int, callback: PermissionCallback) {
        mRequestcode = requestcode
        if (isPemissionAllowed(permission)) {
            callback.onPermissionStatus(true)
        } else {
            this.callback = callback
            requestPermissions(arrayOf(permission), requestcode)
        }
    }


    fun requestPermission(permission: Array<String>, requestcode: Int, callback: PermissionCallback) {
        mRequestcode = requestcode
        try {
            val list = isPemissionAllowed(permission)
            if (list.size == 0) {
                callback.onPermissionStatus(true)
            } else {
                val permissionList = arrayOfNulls<String>(list.size)
                for (i in permissionList.indices) {
                    permissionList[i] = list.get(i)
                }
                this.callback = callback
                requestPermissions(permissionList, requestcode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isPemissionAllowed(permission: Array<String>): ArrayList<String> {
        val list = ArrayList<String>()
        try {
            for (permssion in permission) {
                val isGranted = ContextCompat.checkSelfPermission(activity!!.applicationContext,
                        permssion) == PackageManager.PERMISSION_GRANTED
                if (!isGranted) {
                    list.add(permssion)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d("BaseFragment", "onRequestPermissionsResult (line 97): ")
        callback?.let {
            if (requestCode == this.mRequestcode) {
                if (grantResults.size > 0) {
                    for (i in grantResults.indices) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            callback?.onPermissionStatus(false)
                            break
                        }
                    }
                    callback?.onPermissionStatus(true)

                } else {
                    callback?.onPermissionStatus(false)
                }
            } else {
                callback?.onPermissionStatus(true)
            }
        }

    }

}
