package com.oyespace.guards.staff

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.adapter.BlockSelectionAdapter
import com.oyespace.guards.adapter.SelectedUnitsAdapter
import com.oyespace.guards.adapter.UnitSearchResultAdapter
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_block_selection.*
import kotlinx.android.synthetic.main.activity_mobile_number.*
import kotlinx.android.synthetic.main.search_layout.*
import kotlinx.android.synthetic.main.title_bar.view.*

class StaffBlockSelectionActivity : BaseKotlinActivity(), View.OnClickListener {

    var iv_torch: Button?=null
    var clickable1 = 0
    var mBlocksArray = ArrayList<BlocksData>()
    var mBlocksAdapter: BlockSelectionAdapter? = null
    var mUnitsAdapter: SelectedUnitsAdapter? = null
    var mSearchUnitsAdapter: UnitSearchResultAdapter? = null
    var selected = ArrayList<UnitPojo>()
    var searched = ArrayList<UnitPojo>()
    internal var unitNumber1 = ""
    internal var unitNumber2 = ""
    internal var unitNumber3 = ""
    internal var unitNumber4 = ""
    internal var unitNumber5 = ""
    internal var unitNames = ""
    internal var unitOccupancyStatus=""
    internal var blockID = ""
    internal var unitId = ""
    internal var acAccntID = ""
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_selection)
        //setDarkStatusBar()

        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name.textSize = 5 * resources.displayMetrics.density
        }
        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(GATE_NO, "")
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

        try {
            try {
                var json: String = (intent.getStringExtra(SELECTED_UNITS))
                if (json != null) {
                    var selArray: Array<UnitPojo> =
                        Gson().fromJson(json, Array<UnitPojo>::class.java)
                    selected = ArrayList(selArray.asList())
                }
            } catch (e: IllegalStateException) {

            }
            setUnitsAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        initTitles()
        getBlocksList()


        search_text.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchUnits()
                true
            } else {
                false
            }
        }

    }

    private fun onSearchResultClick(unit: UnitPojo, index: Int) {
        val indices =
            selected.mapIndexedNotNull { index, event -> if (event.unUnitID.equals(unit.unUnitID)) index else null }
        if (indices == null || indices.size == 0) {
            selected.add(unit)
            setUnitsAdapter()
            search_text.setText("")
            markSelectedBlock()
        }
        //searched.clear();
        //setSearchUnitsAdapter()
    }

    private fun setSearchUnitsAdapter() {
        mSearchUnitsAdapter =
            UnitSearchResultAdapter(
                this@StaffBlockSelectionActivity,
                searched,
                clickListener = { unit, index ->
                    onSearchResultClick(unit, index)
                })
        rcv_searched_units.adapter = mSearchUnitsAdapter
        rcv_searched_units.layoutManager = LinearLayoutManager(this@StaffBlockSelectionActivity)
        mSearchUnitsAdapter!!.notifyDataSetChanged()
        rcv_searched_units.visibility = View.VISIBLE
    }

    private fun setUnitsAdapter() {
        mUnitsAdapter =
            SelectedUnitsAdapter(
                this@StaffBlockSelectionActivity,
                selected,
                clickListener = { unit, index ->
                    onUnitClose(unit, index)
                })
        rcv_selected_units.adapter = mUnitsAdapter
        rcv_selected_units.layoutManager = GridLayoutManager(this@StaffBlockSelectionActivity, 5)
        mUnitsAdapter!!.notifyDataSetChanged()
    }

    private fun setBlockAdapter() {
        mBlocksAdapter =
            BlockSelectionAdapter(
                this@StaffBlockSelectionActivity,
                mBlocksArray,
                clickListener = { block, index ->
                    onPageClick(block, index)
                })
        rcv_blocks.adapter = mBlocksAdapter
        rcv_blocks.layoutManager = GridLayoutManager(this@StaffBlockSelectionActivity, 5)
        mBlocksAdapter!!.notifyDataSetChanged()
    }

    private fun markSelectedBlock() {
        var updatedBlocks = ArrayList<BlocksData>()
        for (i in 0 until mBlocksArray.size) {
            var block: BlocksData = mBlocksArray[i]
            val indices =
                selected.mapIndexedNotNull { index, event -> if (event.blBlockID.equals(block.blBlockID)) index else null }
            block.isSelected = indices != null && indices.size > 0
            updatedBlocks.add(block)
        }
        mBlocksArray = updatedBlocks
        setBlockAdapter()
    }

    private fun onUnitClose(unit: UnitPojo, index: Int) {
        selected.removeAt(index)
        mUnitsAdapter!!.notifyDataSetChanged()
        markSelectedBlock()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonNext -> {
                buttonNext.isEnabled = false
                buttonNext.isClickable = false
                //  if (selected?.size > 0) {
                onNextPress()
                //  }
            }
            R.id.btn_search_action -> {
                searchUnits()
            }
            R.id.btn_mic_action -> {
                openMic()
            }
        }
    }

    private fun onNextPress() {

        if (selected.size > 0) {
            for (j in selected.indices) {
                if ((unitNames.length != 0) || (unitNumber1.length != 0)) {
                    blockID += ", "
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
                blockID += selected.get(j).blBlockID

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

                if (intent.getStringExtra(COMPANY_NAME) != null && intent.getStringExtra(COMPANY_NAME).equals("Others")) {
                    val d = Intent(this@StaffBlockSelectionActivity, StaffPurposeScreen::class.java)
//                            Log.d( "intentdata MobileNumber", "buttonNext " + intent.getStringExtra(UNITNAME) +
// " " + intent.getStringExtra(UNITID) + " " + Ed_phoneNum.text + " " + countryCode );
                    d.putExtra(UNITID, unitId)
                    d.putExtra(BLOCK_ID,blockID)
                    d.putExtra(UNITNAME, unitNames)
                    d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                    d.putExtra("RESIDENT_NUMBER", unitNumber1 + ", " + unitNumber2 + ", " + unitNumber3 + ", " + unitNumber4 + ", " + unitNumber5)
                    d.putExtra(UNITOCCUPANCYSTATUS,unitOccupancyStatus)
                    startActivity(d)
                    finish()
                } else {

                    val d = Intent(this@StaffBlockSelectionActivity, StaffMobileNumberScreen::class.java)
                    Log.d(
                        "intentdata NameEntr", "buttonNext " + intent.getStringExtra(UNITNAME) + " "
                                + intent.getStringExtra(UNITID) + " " + intent.getStringExtra(
                            MOBILENUMBER
                        ) + " "
                                + intent.getStringExtra(COUNTRYCODE) + " "
                    )
                    d.putExtra(UNITID, unitId)
                    d.putExtra(BLOCK_ID,blockID)
                    d.putExtra(UNITNAME, unitNames)
                    d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                    //d.putExtra("RESIDENT_NUMBER",unitNumber1)
                    d.putExtra(
                        "RESIDENT_NUMBER",
                        unitNumber1 + ", " + unitNumber2 + ", " + unitNumber3 + ", " + unitNumber4 + ", " + unitNumber5
                    )

                    d.putExtra(UNITOCCUPANCYSTATUS,unitOccupancyStatus)

                    startActivity(d)
                    finish()
                }

            } else {
                buttonNext.isEnabled = true
                buttonNext.isClickable = true
                Toast.makeText(applicationContext, "Select Unit", Toast.LENGTH_SHORT).show()

            }


        } else {
            Toast.makeText(applicationContext, "No data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPageClick(selectedBlock: BlocksData, index: Int) {
        val _intent = Intent(this@StaffBlockSelectionActivity, StaffUnitSelectionActivity::class.java)
        _intent.putExtra(ConstantUtils.SELECTED_BLOCK, selectedBlock.blBlockID)
        _intent.putExtra(ConstantUtils.SELECTED_BLOCK_NAME, selectedBlock.blBlkName)
        _intent.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
        _intent.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
        _intent.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
        var json = Gson().toJson(selected)
        _intent.putExtra(ConstantUtils.SELECTED_UNITS, json)
        startActivity(_intent)
        finish()
    }

    private fun initTitles() {
        title_unit.header_title.text = this.resources.getString(R.string.units_selection_title)
        title_block.header_title.setTextColor(this.resources.getColor(R.color.black))
        title_block.header_title.text = this.resources.getString(R.string.blocks_selection_title)
    }


    private fun searchUnits() {
        if (search_text.text.toString().trim().length > 0) {
            showProgressrefresh()
            var associationId: Int = Prefs.getInt(ASSOCIATION_ID, 0)
            var searchObj = SearchUnitRequest(associationId, search_text.text.toString())
            CompositeDisposable().add(
                RetrofitClinet.instance
                    .searchUnits(searchObj, ConstantUtils.CHAMPTOKEN)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : CommonDisposable<UnitListSearch<UnitPojo>>() {

                        override fun onSuccessResponse(UnitListSearch: UnitListSearch<UnitPojo>) {
                            dismissProgressrefresh()
                            if (UnitListSearch.success == true) {
                                //searched.clear()
                                //searched.add(UnitListSearch.data.unit)
                                onSearchResultClick(UnitListSearch.data.unit, 0)
                            }
                        }

                        override fun onErrorResponse(e: Throwable) {
                            dismissProgressrefresh()
                            Toast.makeText(
                                this@StaffBlockSelectionActivity,
                                "No Units Found !!! ",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                        override fun noNetowork() {
                            dismissProgressrefresh()
                            Toast.makeText(
                                this@StaffBlockSelectionActivity,
                                "No network call ",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            )
        }
    }


    private fun getBlocksList() {
        showProgressrefresh()
        RetrofitClinet.instance
            .blocksList(CHAMPTOKEN, AppUtils.intToString(Prefs.getInt(ASSOCIATION_ID, 0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<BlocksList<ArrayList<BlocksData>>>() {

                override fun onSuccessResponse(BlocksList: BlocksList<ArrayList<BlocksData>>) {
                    dismissProgressrefresh()
                    if (BlocksList.success == true) {
                        mBlocksArray = BlocksList.data.blocksByAssoc
                        setBlockAdapter()
                        markSelectedBlock()
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(this@StaffBlockSelectionActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(
                        this@StaffBlockSelectionActivity,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (result != null && result.size > 0) {
                        search_text.setText(result[0].trim())
                    }
                }
            }
        }
    }
}

