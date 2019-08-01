package com.oyespace.guards.guest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.activity.UnitSelectionActivity
import com.oyespace.guards.com.oyespace.guards.adapter.BlockSelectionAdapter
import com.oyespace.guards.com.oyespace.guards.adapter.SelectedUnitsAdapter
import com.oyespace.guards.com.oyespace.guards.adapter.UnitSearchResultAdapter
import com.oyespace.guards.com.oyespace.guards.pojo.BlocksData
import com.oyespace.guards.com.oyespace.guards.pojo.BlocksList
import com.oyespace.guards.com.oyespace.guards.pojo.SearchUnitRequest
import com.oyespace.guards.com.oyespace.guards.pojo.UnitsList
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.UnitList
import com.oyespace.guards.pojo.UnitListSearch
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils
import kotlinx.android.synthetic.main.activity_block_selection.*
import kotlinx.android.synthetic.main.title_bar.view.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import kotlinx.android.synthetic.main.activity_mobile_number.buttonNext
import kotlinx.android.synthetic.main.activity_unit_list.*
import kotlinx.android.synthetic.main.search_layout.*
import java.lang.Exception

class GuestBlockSelectionActivity : BaseKotlinActivity(), View.OnClickListener {

    var mBlocksArray = ArrayList<BlocksData>()
    var mBlocksAdapter:BlockSelectionAdapter?=null
    var mUnitsAdapter:SelectedUnitsAdapter?=null
    var mSearchUnitsAdapter:UnitSearchResultAdapter?=null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_selection)
        //setDarkStatusBar()


        try{
            var json:String = (intent.getStringExtra(SELECTED_UNITS))
            if(json != null) {
                var selArray: Array<UnitPojo> = Gson().fromJson(json, Array<UnitPojo>::class.java)
                selected = ArrayList(selArray.asList())
            }

            setUnitsAdapter();

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
        val indices = selected!!.mapIndexedNotNull { index, event ->  if (event.unUnitID.equals(unit.unUnitID)) index else null}
        if(indices == null || indices.size == 0){
            selected.add(unit);
            setUnitsAdapter()
            search_text.setText("");
            markSelectedBlock()
        }
        //searched.clear();
        //setSearchUnitsAdapter()
    }

    private fun setSearchUnitsAdapter(){
        mSearchUnitsAdapter =
            UnitSearchResultAdapter(this@GuestBlockSelectionActivity, searched,clickListener = {
                    unit,index -> onSearchResultClick(unit,index)
            })
        rcv_searched_units.adapter = mSearchUnitsAdapter
        rcv_searched_units.setLayoutManager(LinearLayoutManager(this@GuestBlockSelectionActivity));
        mSearchUnitsAdapter!!.notifyDataSetChanged()
        rcv_searched_units.visibility = View.VISIBLE
    }

    private fun setUnitsAdapter(){
        mUnitsAdapter =
            SelectedUnitsAdapter(this@GuestBlockSelectionActivity, selected,clickListener = {
                    unit,index -> onUnitClose(unit,index)
            })
        rcv_selected_units.adapter = mUnitsAdapter
        rcv_selected_units.setLayoutManager(GridLayoutManager(this@GuestBlockSelectionActivity,5));
        mUnitsAdapter!!.notifyDataSetChanged()
    }

    private fun setBlockAdapter(){
        mBlocksAdapter =
            BlockSelectionAdapter(this@GuestBlockSelectionActivity, mBlocksArray,clickListener = {
                    block,index -> onPageClick(block,index)
            })
        rcv_blocks.adapter = mBlocksAdapter
        rcv_blocks.setLayoutManager(GridLayoutManager(this@GuestBlockSelectionActivity,5));
        mBlocksAdapter!!.notifyDataSetChanged()
    }

    private fun markSelectedBlock(){
        var updatedBlocks = ArrayList<BlocksData>();
        for(i in 0 until mBlocksArray.size){
            var block:BlocksData = mBlocksArray[i];
            val indices = selected!!.mapIndexedNotNull { index, event ->  if (event.blBlockID.equals(block.blBlockID)) index else null}
            if(indices != null && indices.size > 0){
                block.isSelected = true;
            }else{
                block.isSelected = false
            }
            updatedBlocks.add(block)
        }
        mBlocksArray = updatedBlocks;
        setBlockAdapter()
    }

    private fun onUnitClose(unit:UnitPojo, index:Int){
        selected.removeAt(index);
        mUnitsAdapter!!.notifyDataSetChanged()
        markSelectedBlock()
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.buttonNext -> {
                buttonNext.setEnabled(false)
                buttonNext.setClickable(false)
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

        if (selected?.size > 0) {
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


                val d = Intent(this@GuestBlockSelectionActivity, GuestMobileNumberScreen::class.java)
                Log.d(
                    "intentdata NameEntr",
                    "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
                            + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(
                        COUNTRYCODE
                    ) + " "
                );
                d.putExtra(UNITID, unitId)
                d.putExtra(UNITNAME, unitNames)
                d.putExtra(FLOW_TYPE, GUEST_REGISTRATION)
                d.putExtra(VISITOR_TYPE, GUEST)
                d.putExtra(COMPANY_NAME, GUEST)
                d.putExtra(UNIT_ACCOUNT_ID,acAccntID)

                startActivity(d);
                finish()
                }

            } else {
                buttonNext.setEnabled(true)
                buttonNext.setClickable(true)
                Toast.makeText(applicationContext, "Select Unit", Toast.LENGTH_SHORT).show()

            }



    }

    private fun onPageClick(selectedBlock:BlocksData, index:Int){
        val _intent = Intent(this@GuestBlockSelectionActivity, GuestUnitSelectionActivity::class.java)
        _intent.putExtra(ConstantUtils.SELECTED_BLOCK,selectedBlock.blBlockID);
        _intent.putExtra(ConstantUtils.SELECTED_BLOCK_NAME,selectedBlock.blBlkName);
        _intent.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
        _intent.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
        _intent.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))



        var json = Gson().toJson(selected)
        _intent.putExtra(ConstantUtils.SELECTED_UNITS,json);
        startActivity(_intent)
        finish();
    }

    private fun initTitles(){
        title_unit.header_title.setText(this.resources.getString(R.string.units_selection_title));
        title_block.header_title.setTextColor(this.resources.getColor(R.color.black));
        title_block.header_title.setText(this.resources.getString(R.string.blocks_selection_title));
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
                            Toast.makeText(this@GuestBlockSelectionActivity, "No Units Found !!! ", Toast.LENGTH_LONG).show()

                        }

                        override fun noNetowork() {
                            dismissProgressrefresh()
                            Toast.makeText(this@GuestBlockSelectionActivity, "No network call ", Toast.LENGTH_LONG).show()
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
                        mBlocksArray = BlocksList.data.blocksByAssoc;
                        setBlockAdapter()
                        markSelectedBlock();
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(this@GuestBlockSelectionActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(this@GuestBlockSelectionActivity, "No network call ", Toast.LENGTH_LONG).show()
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
                        search_text.setText(result[0].trim());
                    }
                }
            }
        }
    }
}

