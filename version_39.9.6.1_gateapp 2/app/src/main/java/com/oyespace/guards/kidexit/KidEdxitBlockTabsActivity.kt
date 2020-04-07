package com.oyespace.guards.kidexit

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.fragment.UnitsFragment
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.BlocksData
import com.oyespace.guards.pojo.BlocksList
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class KidEdxitBlockTabsActivity : BaseKotlinActivity(), OnDataPass {
    var clickable1 = 0
    var iv_torch: Button?=null
    var buttonNext:Button?=null
    internal var acAccntID = ""
    internal var unitNumber1 = ""
    internal var unitNumber2 = ""
    internal var unitNumber3 = ""
    internal var unitNumber4 = ""
    internal var unitNumber5 = ""
    internal var unitNames = ""
    internal var unitOccupancyStatus=""
    internal var blockID = ""
    internal var unitId = ""
    var selected = ArrayList<UnitPojo>()
    var hashSet = HashSet<UnitPojo>()
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    var mBlocksArray = ArrayList<BlocksData>()
    var tabs: TabLayout? = null
    var vp_block: ViewPager? = null
    var tabTitle = java.util.ArrayList<String>()
    var tabId = java.util.ArrayList<String>()
     val mFragmentList: ArrayList<Fragment> = ArrayList()
    var selectedUnits = ArrayList<UnitPojo>()
     var adapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_tabs)

        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name.textSize = 5 * resources.displayMetrics.density
        }
        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, "")
        try {
            var appVersion = ""
            val manager = baseContext.packageManager
            val info = manager.getPackageInfo(baseContext.packageName, 0)
            appVersion = info.versionName
            Log.d("tag", "app " + appVersion + " " + info.versionName)
            txt_device_name.text = "V: $appVersion"

        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name.text = " "

        }

        buttonNext=findViewById(R.id.buttonNext)
        iv_torch=findViewById(R.id.iv_torch)
        iv_torch!!.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
                var cameraId: String? = null
                cameraId = camManager.getCameraIdList()[0];
                if(clickable1==0){
                    try {
                        iv_torch!!.background=resources.getDrawable(R.drawable.torch_off)
                        camManager.setTorchMode(cameraId, true);   //Turn ON

                        //  iv_torch!!.text = "OFF"
                        clickable1=1
                    } catch (e: CameraAccessException) {
                        e.printStackTrace();
                    }
                }
                else if(clickable1==1){
                    camManager.setTorchMode(cameraId, false);
                    // iv_torch!!.text = "ON"
                    iv_torch!!.background=resources.getDrawable(R.drawable.torch_on)
                    clickable1=0

                }
            }

        }
        tabs = findViewById(R.id.tabs)
        vp_block = findViewById(R.id.vp_block)
        getBlocksList()
        buttonNext!!.setOnClickListener{
            buttonNext!!.isEnabled = false
            buttonNext!!.isClickable = false
            //  if (selected?.size > 0) {
            for (i in 0 until selectedUnits.size) {
                Log.d("LOG","UnitName " + selectedUnits[i].unUniName)
                selected.add(selectedUnits[i])
            }
            hashSet.addAll(selected);
            selected.clear();
            selected.addAll(hashSet);
            onNextPress()
        }

    }

    private fun getBlocksList() {
        showProgressrefresh()
        RetrofitClinet.instance
            .blocksList(ConstantUtils.CHAMPTOKEN, AppUtils.intToString(Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<BlocksList<ArrayList<BlocksData>>>() {

                override fun onSuccessResponse(BlocksList: BlocksList<ArrayList<BlocksData>>) {
                    dismissProgressrefresh()
                    if (BlocksList.success == true) {
                        mBlocksArray = BlocksList.data.blocksByAssoc
                        for (l in 0 until mBlocksArray.size) {
                           // tabs!!.addTab(tabs!!.newTab().setText(mBlocksArray[l].blBlkName))
                              tabTitle.add(mBlocksArray[l].blBlkName)
                            tabId.add(mBlocksArray[l].blBlockID.toString())
                        }
                        for (i in 0 until tabTitle.size) {
                            mFragmentList.add(UnitsFragment())
                        }
                        setupViewPager(vp_block!!)
                        tabs!!.setupWithViewPager(vp_block);
                        // Tab ViewPager setting
                        vp_block!!.setOffscreenPageLimit(mFragmentList.size);
                        tabs!!.setupWithViewPager(vp_block);
                        tabs!!.setTabsFromPagerAdapter(adapter);
                      //  vp_block!!.setOffscreenPageLimit(BlocksList.data.blocksByAssoc.size);
                       // val adapter = UnitsPagerAdapter(supportFragmentManager, tabs!!.getTabCount(), tabTitle)
                      //  vp_block!!.setAdapter(adapter)
                      //  tabs!!.setupWithViewPager(vp_block);
                       // vp_block!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabs))
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(this@KidEdxitBlockTabsActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(
                        this@KidEdxitBlockTabsActivity,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(supportFragmentManager, mFragmentList, tabTitle,tabId)
        viewPager.adapter = adapter
    }

    class ViewPagerAdapter(fm: FragmentManager?, fragments: List<Fragment>?, titleLists: List<String>, idLists:List<String>) :
        FragmentPagerAdapter(fm) {
        var fragment: Fragment? = null
        private var mIdList: List<String>? = ArrayList()
        private var mFragmentList: List<Fragment>? = ArrayList()
        private var mFragmentTitleList: List<String> = ArrayList()
        override fun getItem(position: Int): Fragment? {
            for (i in 0 until mIdList!!.size) {
                if (i == position) {
                    fragment = UnitsFragment().newInstance(mIdList!!.get(position));
                }
            }

            return fragment
        }

        override fun getCount(): Int {
            return mFragmentList?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        init {
            mFragmentList = fragments
            mFragmentTitleList = titleLists
            mIdList=idLists
        }
    }

    private fun onNextPress() {

        if (selected.size > 0) {
            for (j in selected.indices) {
                if ((unitNames.length != 0) || (unitNumber1.length != 0)) {
                    unitNames += ", "
                    unitId += ", "
                    acAccntID += ", "
                    acAccntID += ", "
                    unitNumber1 += ", "
                    unitNumber2 += ", "
                    unitNumber3 += ", "
                    unitNumber4 += ", "
                    unitNumber5 += ", "
                }
                unitNames += selected.get(j).unUniName
                unitId += selected.get(j).unUnitID
                acAccntID += selected.get(j).acAccntID

            }

            if (unitNames.length > 0) {


                val d = Intent(this@KidEdxitBlockTabsActivity, KidExitMobileNumberScreen::class.java)
                d.putExtra(UNITID, unitId)
                d.putExtra(UNITNAME, unitNames)
                d.putExtra(FLOW_TYPE, KIDEXIT)
                d.putExtra(VISITOR_TYPE,KIDEXIT)
                d.putExtra(COMPANY_NAME, KIDEXIT)
                d.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                //d.putExtra("RESIDENT_NUMBER",unitNumber1)
                d.putExtra(
                    "RESIDENT_NUMBER",
                    unitNumber1 + ", " + unitNumber2 + ", " + unitNumber3 + ", " + unitNumber4 + ", " + unitNumber5
                )


                startActivity(d)
                finish()
                // }

            } else {
                buttonNext!!.isEnabled = true
                buttonNext!!.isClickable = true
                Toast.makeText(applicationContext, "Select Unit", Toast.LENGTH_SHORT).show()

            }


        } else {
            buttonNext!!.isEnabled = true
            buttonNext!!.isClickable = true
            Toast.makeText(applicationContext, "No data", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDataPass(data: String) {
        Log.d("LOG","hello " + data.toString())
        if (data != null) {
            var selArray: Array<UnitPojo> = Gson().fromJson(data, Array<UnitPojo>::class.java)
            selectedUnits = ArrayList(selArray.asList())

            Log.d("LOG", "111 " + selectedUnits.size)
        }
    }
}
