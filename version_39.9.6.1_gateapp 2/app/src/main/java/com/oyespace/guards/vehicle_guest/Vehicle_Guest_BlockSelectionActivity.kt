package com.oyespace.guards.vehicle_guest


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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

class Vehicle_Guest_BlockSelectionActivity : BaseKotlinActivity(), View.OnClickListener {

    var mBlocksArray = ArrayList<BlocksData>()
    var mBlocksAdapter: BlockSelectionAdapter? = null
    var mUnitsAdapter: SelectedUnitsAdapter? = null
    var mSearchUnitsAdapter: UnitSearchResultAdapter? = null
    var selected = ArrayList<UnitPojo>()
    var searched = ArrayList<UnitPojo>()
    internal var unitNumber1=""
    internal var unitNumber2=""
    internal var unitNumber3=""
    internal var unitNumber4=""
    internal var unitNumber5=""
    internal var unitNames = ""
    internal var unitId = ""
    internal var acAccntID=""
    internal var blockID = ""
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

        try{
            var json:String = (intent.getStringExtra(SELECTED_UNITS))
            if(json != null) {
                var selArray: Array<UnitPojo> = Gson().fromJson(json, Array<UnitPojo>::class.java)
                selected = ArrayList(selArray.asList())
            }

            setUnitsAdapter()

        }catch (e:Exception){
            e.printStackTrace()
        }

        initTitles()
        getBlocksList()


        search_text.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchUnits()
                true
            } else {
                false
            }
        }

    }

    private fun onSearchResultClick(unit:UnitPojo, index:Int){
        val indices =
            selected.mapIndexedNotNull { index, event -> if (event.unUnitID.equals(unit.unUnitID)) index else null }
        if(indices == null || indices.size == 0){
            selected.add(unit)
            setUnitsAdapter()
            search_text.setText("")
            markSelectedBlock()
        }
        //searched.clear();
        //setSearchUnitsAdapter()
    }

    private fun setSearchUnitsAdapter(){
        mSearchUnitsAdapter =
            UnitSearchResultAdapter(this@Vehicle_Guest_BlockSelectionActivity, searched,clickListener = {
                    unit,index -> onSearchResultClick(unit,index)
            })
        rcv_searched_units.adapter = mSearchUnitsAdapter
        rcv_searched_units.layoutManager =
            LinearLayoutManager(this@Vehicle_Guest_BlockSelectionActivity)
        mSearchUnitsAdapter!!.notifyDataSetChanged()
        rcv_searched_units.visibility = View.VISIBLE
    }

    private fun setUnitsAdapter(){
        mUnitsAdapter =
            SelectedUnitsAdapter(this@Vehicle_Guest_BlockSelectionActivity, selected,clickListener = {
                    unit,index -> onUnitClose(unit,index)
            })
        rcv_selected_units.adapter = mUnitsAdapter
        rcv_selected_units.layoutManager =
            GridLayoutManager(this@Vehicle_Guest_BlockSelectionActivity, 5)
        mUnitsAdapter!!.notifyDataSetChanged()
    }

    private fun setBlockAdapter(){
        mBlocksAdapter =
            BlockSelectionAdapter(this@Vehicle_Guest_BlockSelectionActivity, mBlocksArray,clickListener = {
                    block,index -> onPageClick(block,index)
            })
        rcv_blocks.adapter = mBlocksAdapter
        rcv_blocks.layoutManager = GridLayoutManager(this@Vehicle_Guest_BlockSelectionActivity, 5)
        mBlocksAdapter!!.notifyDataSetChanged()
    }

    private fun markSelectedBlock(){
        var updatedBlocks = ArrayList<BlocksData>()
        for(i in 0 until mBlocksArray.size){
            var block: BlocksData = mBlocksArray[i]
            val indices =
                selected.mapIndexedNotNull { index, event -> if (event.blBlockID.equals(block.blBlockID)) index else null }
            block.isSelected = indices != null && indices.size > 0
            updatedBlocks.add(block)
        }
        mBlocksArray = updatedBlocks
        setBlockAdapter()
    }

    private fun onUnitClose(unit:UnitPojo, index:Int){
        selected.removeAt(index)
        mUnitsAdapter!!.notifyDataSetChanged()
        markSelectedBlock()
    }

    override fun onClick(v: View?) {
        when (v?.id){
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
                    unitNames += ", "
                    unitId += ", "
                    acAccntID += ", "
                    blockID += ", "
                    unitNumber1 += ", "
                    unitNumber2 += ", "
                    unitNumber3 += ", "
                    unitNumber4 += ", "
                    unitNumber5 += ", "
                }
                unitNames += selected.get(j).unUniName
                unitId += selected.get(j).unUnitID
                acAccntID += selected.get(j).acAccntID
                blockID += selected.get(j).blBlockID

//                if (selected.get(j).tenant.size != 0) {
//                    try {
//                        unitNumber1 += selected.get(j).tenant[0].utMobile
//                        unitNumber2 += selected.get(j).tenant[0].utMobile1
//                        //0  Toast.makeText(this@UnitListActivity, unitNumber1, Toast.LENGTH_LONG).show()
//                    } catch (e: IndexOutOfBoundsException) {
//
//                    }
//                } else {
//                    if (selected.get(j).owner.size != 0) {
//
//                        try {
//                            unitNumber1 += selected.get(j).owner[0].uoMobile
//                            unitNumber2 += selected.get(j).owner[0].uoMobile1
//                            unitNumber3 += selected.get(j).owner[0].uoMobile2
//                            unitNumber4 += selected.get(j).owner[0].uoMobile3
//                            unitNumber5 += selected.get(j).owner[0].uoMobile4
//
//                            //   Toast.makeText(this@UnitListActivity,unitNumber1,Toast.LENGTH_LONG).show()
//                        } catch (e: IndexOutOfBoundsException) {
//
//                        }
//
//                    }
//
//                }


            }

            if (unitNames.length > 0) {

                val d = Intent(this@Vehicle_Guest_BlockSelectionActivity, VehicleGuestMobileNumberScreen::class.java)
                Log.d(
                    "intentdata NameEntr",
                    "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(
                        UNITID
                    )
                            + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(
                        COUNTRYCODE
                    ) + " "
                )
                d.putExtra(UNITID, unitId )
                d.putExtra(UNITNAME,unitNames )
                d.putExtra(FLOW_TYPE, VEHICLE_GUESTWITHOUTINVITATION)
                d.putExtra(VISITOR_TYPE,ConstantUtils.GUEST)
                d.putExtra(COMPANY_NAME,"Guest")
                d.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                d.putExtra(BLOCK_ID, blockID)
                startActivity(d)
                finish()
            }

            } else {
            buttonNext.isEnabled = true
            buttonNext.isClickable = true
                Toast.makeText(applicationContext, "Select Unit", Toast.LENGTH_SHORT).show()

            }



    }

    private fun onPageClick(selectedBlock:BlocksData, index:Int){
        val _intent = Intent(this@Vehicle_Guest_BlockSelectionActivity, Vehicle_guest_UnitSelectionActivity::class.java)
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

    private fun initTitles(){
        title_unit.header_title.text = this.resources.getString(R.string.units_selection_title)
        title_block.header_title.setTextColor(this.resources.getColor(R.color.black))
        title_block.header_title.text = this.resources.getString(R.string.blocks_selection_title)
    }


    private fun searchUnits(){
        if(search_text.text.toString().trim().length > 0) {
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
                            Toast.makeText(this@Vehicle_Guest_BlockSelectionActivity, "No Units Found !!! ", Toast.LENGTH_LONG).show()

                        }

                        override fun noNetowork() {
                            dismissProgressrefresh()
                            Toast.makeText(this@Vehicle_Guest_BlockSelectionActivity, "No network call ", Toast.LENGTH_LONG).show()
                        }
                    })
            )
        }
    }


    private fun getBlocksList(){
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
                    Toast.makeText(this@Vehicle_Guest_BlockSelectionActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(this@Vehicle_Guest_BlockSelectionActivity, "No network call ", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if(result != null && result.size > 0) {
                        search_text.setText(result[0].trim())
                    }
                }
            }
        }
    }
}

