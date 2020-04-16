package com.oyespace.guards.activity

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
import io.realm.Realm


class BlockTabsActivity : BaseKotlinActivity(), OnDataPass {

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
     var adapter: ViewPagerAdapter? = null
    var selectedUnits = ArrayList<UnitPojo>()
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
                    Toast.makeText(this@BlockTabsActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(
                        this@BlockTabsActivity,
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
                    //  acAccntID += ", "
                    unitNumber1 += ", "
                    unitNumber2 += ", "
                    unitNumber3 += ", "
                    unitNumber4 += ", "
                    unitNumber5 += ", "
                    unitOccupancyStatus+=","
                }
                unitNames += selected.get(j).unUniName
                unitId += selected.get(j).unUnitID
                unitOccupancyStatus+=selected.get(j).unOcStat

//               if(selected.get(j).unOcStat.contains("Sold Owner Occupied Unit")){
//                   acAccntID += selected.get(j).owner[0].acAccntID
//
//               }
//                else if(selected.get(j).unOcStat.contains("Sold Tenant Occupied Unit")){
//                   acAccntID += selected.get(j).tenant[0].acAccntID
//               }
//                else if(selected.get(j).unOcStat.contains("UnSold Tenant Occupied Unit")){
//                   acAccntID += selected.get(j).tenant[0].acAccntID
//               }
//                else if(selected.get(j).unOcStat.contains("UnSold Vacant Unit")){
//                   acAccntID += 0
//               }
//                else if(selected.get(j).unOcStat.contains("Sold Vacant Unit")){
//                   acAccntID += 0
//               }

            }

            if (unitNames.length > 0) {
                var companyName=intent.getStringExtra(COMPANY_NAME)

                if(companyName!=null){

                    if(companyName.equals("Others")){

                        val i = Intent(this@BlockTabsActivity, PurposeScreen::class.java)
                        i.putExtra(UNITID, unitId)
                        i.putExtra(UNITNAME, unitNames)
                        i.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                        i.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                        i.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                        i.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                        i.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
                        i.putExtra(COMPANY_NAME, OTHERS)
                        i.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                        // i.putExtra(PERSON_PHOTO, intent.getStringExtra(PERSON_PHOTO))
                        i.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                        startActivity(i)
                        finish()

                    }else if(companyName.equals("अन्य")){

                        val i = Intent(this@BlockTabsActivity, PurposeScreen::class.java)
                        i.putExtra(UNITID, unitId)
                        i.putExtra(UNITNAME, unitNames)
                        i.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                        i.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                        i.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                        i.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                        i.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
                        i.putExtra(COMPANY_NAME, OTHERS)
                        i.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                        //i.putExtra(PERSON_PHOTO, intent.getStringExtra(PERSON_PHOTO))
                        i.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                        startActivity(i)
                        finish()

                    }else{
                        var personName= intent.getStringExtra(PERSONNAME)
                        if(personName!=null) {
                            val i = Intent(this@BlockTabsActivity, DeliveryPersonPhotoActivity::class.java)
                            i.putExtra(UNITID, unitId)
                            i.putExtra(UNITNAME, unitNames)
                            i.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                            i.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            i.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                            i.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                            i.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
                            i.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            i.putExtra(UNIT_ACCOUNT_ID, acAccntID)
                            // i.putExtra(PERSON_PHOTO, intent.getStringExtra(PERSON_PHOTO))
                            startActivity(i)
                            finish()
                        }else{
                            val i = Intent(this@BlockTabsActivity, NameEntryScreen::class.java)
                            i.putExtra(UNITID, unitId)
                            i.putExtra(UNITNAME, unitNames)
                            i.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                            i.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                            i.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
                            i.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            i.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                            i.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            i.putExtra(UNIT_ACCOUNT_ID, acAccntID)
                            // i.putExtra(PERSON_PHOTO, intent.getStringExtra(PERSON_PHOTO))
                            startActivity(i)
                            finish()
                        }
                    }


                }else {
                    val d = Intent(this@BlockTabsActivity, ServiceProviderListActivity::class.java)
                    d.putExtra(UNITID, unitId)
                    d.putExtra(UNITNAME, unitNames)
                    // d.putExtra(ACCOUNT_ID, intent.getStringArrayExtra(ACCOUNT_ID))
                    d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                    d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                    d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(UNIT_ACCOUNT_ID, acAccntID)
                    d.putExtra("RESIDENT_NUMBER", unitNumber1 + ", " + unitNumber2 + ", " + unitNumber3 + ", " + unitNumber4 + ", " + unitNumber5)
                    d.putExtra(UNITOCCUPANCYSTATUS, unitOccupancyStatus)
                    // d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
                    // d.putExtra(PERSON_PHOTO, intent.getStringExtra(PERSON_PHOTO))
                    startActivity(d)
                    finish()
                }

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

//            }

              }
    }
}
