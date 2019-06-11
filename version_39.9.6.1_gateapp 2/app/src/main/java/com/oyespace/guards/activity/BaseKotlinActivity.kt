package com.oyespace.guards.activity

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import com.oyespace.guards.R
import com.oyespace.guards.listeners.PermissionCallback
import io.reactivex.disposables.CompositeDisposable
import java.util.*


/**
 * Created by Kalyan on 21-Oct-17.
 */

open class BaseKotlinActivity : AppCompatActivity(){

    private var progressDialog: ProgressDialog? = null

    val LOCATION_REQ = 7446

    private var callback: PermissionCallback? = null
    private var requestcode: Int = 0


    companion object {
        val TAG: String = javaClass.name
        val LOCATION_REQ: Int=10

    }

    protected val compositeDisposable = CompositeDisposable()

    open fun setUpToolbar(text: String, isBackEnabled: Boolean = true) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = text
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(isBackEnabled)
        supportActionBar?.setDisplayShowHomeEnabled(isBackEnabled)
    }

    open fun setUpToolbar(
        textAppVersion: String,
        textMobileNo: String,
        textAssociationName: String,
        textEndDate: String,
        isBackEnabled: Boolean = true
    ) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false);
        val tv_appversion = findViewById<TextView>(R.id.tv_appversion)
        val tv_mobileno = findViewById<TextView>(R.id.tv_mobileno)
        val tv_association = findViewById<TextView>(R.id.tv_association)
        val tv_enddate = findViewById<TextView>(R.id.tv_enddate)
        tv_appversion.text = textAppVersion
        tv_association.text = textAssociationName
        tv_enddate.text = textEndDate
        tv_mobileno.text = textMobileNo

        supportActionBar?.setDisplayHomeAsUpEnabled(isBackEnabled)
        supportActionBar?.setDisplayShowHomeEnabled(isBackEnabled)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun showProgressrefresh() {
        progressDialog = ProgressDialog(this)
        progressDialog?.isIndeterminate = true
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Refreshing")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    fun dismissProgressrefresh() {
        progressDialog?.isShowing.let {
            progressDialog?.dismiss()
        }
    }
    fun showProgress() {
        progressDialog = ProgressDialog(this)
        progressDialog?.isIndeterminate = true
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Saving")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    fun dismissProgress() {
        progressDialog?.isShowing.let {
            progressDialog?.dismiss()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (callback != null) {
            if (requestCode == this.requestcode) {
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

    fun isPemissionAllowed(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext,
                permission) == PackageManager.PERMISSION_GRANTED
    }



    fun requestPermission(permission: String, requestcode: Int, callback: PermissionCallback) {
        this.requestcode = requestcode
        if (isPemissionAllowed(permission)) {
            callback.onPermissionStatus(true)
        } else {
            this.requestcode = requestcode
            this.callback = callback
            ActivityCompat.requestPermissions(this,
                    arrayOf(permission),
                    requestcode)
        }
    }

    fun requestPermission(permission: Array<String>, requestcode: Int, callback: PermissionCallback) {
        try {
            val list = isPemissionAllowed(permission)
            if (list.size == 0) {
                callback.onPermissionStatus(true)
            } else {
                val permissionList = arrayOfNulls<String>(list.size)
                for (i in permissionList.indices) {
                    permissionList[i] = list[i]
                }
                this.requestcode = requestcode
                this.callback = callback
                ActivityCompat.requestPermissions(this,
                        permissionList,
                        requestcode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isPemissionAllowed(permission: Array<String>): ArrayList<String> {
        val list = ArrayList<String>()
        try {
            for (permssion in permission) {
                val isGranted = ContextCompat.checkSelfPermission(applicationContext,
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




}