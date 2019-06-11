package com.oyespace.guards

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.activity.RegisterFingerPrint
import com.oyespace.guards.activity.ServiceProviderListActivity
import com.oyespace.guards.activity.StaffListActivity
import com.oyespace.guards.adapter.UnitListAdapter
import com.oyespace.guards.guest.GuestCustomViewFinderScannerActivity
import com.oyespace.guards.guest.GuestUnitScreen
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocalYMD
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard_kt.*
import java.util.*

class DashboardActivity : BaseKotlinActivity() , View.OnClickListener {
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    override fun onClick(v: View?) {
        onTabClicked(v)
    }

    fun onTabClicked(v:View?){
        when (v?.id) {
            R.id.re_vehicle -> {


            }

            R.id.re_delivery ->{

                val intentReg  =  Intent(this@DashboardActivity, ServiceProviderListActivity::class.java)
                startActivity(intentReg)
            }
            R.id.re_guest ->{
                val intentReg  =  Intent(this@DashboardActivity, GuestCustomViewFinderScannerActivity::class.java)
                startActivity(intentReg)

            }
            R.id.re_staff ->{
                val intentReg  =  Intent(this@DashboardActivity,StaffListActivity::class.java)
                startActivity(intentReg)

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_kt)
        checkAndroidVersion()
//        makeVisitorLog()
//        makeUnitLog()
        rv_dashboard.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_CALL_LOG//,
               // Manifest.permission.WRITE_CALL_LOG,
               // Manifest.permission.CALL_PHONE
            )
            .withListener(object: MultiplePermissionsListener {

                override fun onPermissionsChecked(report : MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
//                        Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog();
                    }
                }

                override fun  onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    if (token != null) {
                        token.continuePermissionRequest()
                    };
                }
            }).
                withErrorListener(object: PermissionRequestErrorListener {

                    override fun onError( error: DexterError) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
            .onSameThread()
            .check();

        txt_assn_name.setText("Society: " + LocalDb.getAssociation().asAsnName)
        txt_device_name.setText("Gate: " )

    }

    override fun onResume() {
        super.onResume()
        makeVisitorLog()

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@DashboardActivity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)
        startActivityForResult(intent, 101)
    }

    private fun makeVisitorLog() {

        RetrofitClinet.instance
            .visitorList("7470AD35-D51C-42AC-BC21-F45685805BBE", intToString(Prefs.getInt(ASSOCIATION_ID,0)), getCurrentTimeLocalYMD())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GlobalApiObject<ArrayList<VisitorLog>>>() {

                override fun onSuccessResponse(globalApiObject: GlobalApiObject<ArrayList<VisitorLog>>) {

                    if (globalApiObject.success == true) {

                        val nonExitedSort = ArrayList<Visitorlogbydate>()
                        val exitedSort = ArrayList<Visitorlogbydate>()
                        val arrayList = globalApiObject.data.visitorlogbydate//.filter{ x -> x.vlExitT == "0001-01-01T00:00:00"}

                        //looping through existing elements
                        for (s in arrayList) {
                            //if the existing elements contains the search input
                            Log.d("button_done ","visitorlogbydate "+s.vlExitT+" "+s.vlExitT.equals("0001-01-01T00:00:00",true)+" ")

                            if (s.vlExitT.equals("0001-01-01T00:00:00",true)) {
                                Log.d("vlExitT ","visitorlogbydate "+s.vlExitT+" "+s.vlfName+" ")
                                nonExitedSort.add(s)
                                //adding the element to filtered list
                            } else {
                                exitedSort.add(s)
                            }
                        }

//                        LocalDb.saveVisitorLog(nonExitedSort)

                        Collections.sort(exitedSort, object : Comparator<Visitorlogbydate>{
                            override  fun compare(lhs: Visitorlogbydate, rhs: Visitorlogbydate): Int {
                                Log.d("Comparator ","visitorlogbydate "+lhs.vlExitT+" "+rhs.vlExitT.compareTo(lhs.vlExitT)+" "+rhs.vlfName+" "+lhs.vlfName)

                                return rhs.vlExitT.compareTo(lhs.vlExitT)
                            }
                        })

                        val newAl = ArrayList<Visitorlogbydate>()
                        newAl.addAll(nonExitedSort)
                        newAl.addAll(exitedSort)

//                        val orderListAdapter = VistorListAdapter(newAl, this@DashboardActivity)
//                        rv_dashboard.adapter = orderListAdapter
                        Log.d("dvd",""+globalApiObject.data);

                        if (arrayList.size == 0) {
                            Toast.makeText(this@DashboardActivity, "No items", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        rv_dashboard.setEmptyAdapter("No items to show!", false, 0)
                    }
                }

                override fun onErrorResponse(e: Throwable) {

//                       rv_dashboard.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
                    Toast.makeText(this@DashboardActivity, "Error", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    Toast.makeText(this@DashboardActivity, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })

    }

    fun checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
            } else {

            }

        } else {

        }

    }

    fun checkAndRequestPermissions(): Boolean {
        val call = ContextCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.CALL_PHONE)
        val listPermissionsNeeded = ArrayList<String>()
        if (call != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this@DashboardActivity, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        Log.d("in fragment on request", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED) {
                        print("Storage permissions are required")
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("in fragment on request", "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@DashboardActivity, Manifest.permission.CALL_PHONE) )
                        //|| ActivityCompat.shouldShowRequestPermissionRationale(this@DashboardActivity, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this@DashboardActivity, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this@DashboardActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        {
                            showDialogOK("Call  permission is required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                        }
                                    }// proceed with logic by disabling the related features or quit the app.
                                })
                        } else {
                            Toast.makeText(this@DashboardActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show()
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }

    }

    fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@DashboardActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }


}
