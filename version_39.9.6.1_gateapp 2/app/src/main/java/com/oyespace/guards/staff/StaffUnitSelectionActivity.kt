package com.oyespace.guards.staff

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.adapter.FamilMembersAdapter
import com.oyespace.guards.adapter.PaginationAdapter
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.guest.GuestUnitSelectionActivity
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.TaptoCallApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_unit_list.*
import kotlinx.android.synthetic.main.pager_view.*
import kotlinx.android.synthetic.main.subtitle_bar.*
import kotlinx.android.synthetic.main.title_bar.*

class StaffUnitSelectionActivity : BaseKotlinActivity(), View.OnClickListener {

    var iv_torch: Button?=null
    var clickable1 = 0
    var orderListAdapter: UnitListAdapter? = null
    var pageNumberAdapter: PaginationAdapter? = null
    var arrayList = ArrayList<UnitPojo>()
    var arrayFullList = ArrayList<UnitPojo>()
    var selectedUnits = ArrayList<UnitPojo>()
    var pageArrayList = ArrayList<PaginationData>()
    internal var unitNames = ""
    internal var unitId = 0
    internal var blockId = -1
    internal var blockName = ""
    internal var unitNumber1 = ""
    internal var unitNumber2 = ""
    internal var unitNumber3 = ""
    internal var unitNumber4 = ""
    internal var unitNumber5 = ""
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView

    private val LIMIT = 10
    var PAGE_NUMBER = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_selection)

        header_title.text = this.resources.getString(R.string.units_list_title)
        header_subtitle.text = "A Block"

        /**
         * Setting status bar color (Only for Lollipop & above)
         */
        //setDarkStatusBar()

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

        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1"))

        {
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

        try {
            blockId = intent.getIntExtra(ConstantUtils.SELECTED_BLOCK, -1)
            blockName = "" + intent.getStringExtra(ConstantUtils.SELECTED_BLOCK_NAME)
            header_subtitle.text = blockName
            var json: String = (intent.getStringExtra(SELECTED_UNITS))
            var selArray: Array<UnitPojo> = Gson().fromJson(json, Array<UnitPojo>::class.java)
            selectedUnits = ArrayList(selArray.asList())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getUnitsFromBlock()

        rv_unit.setLayoutManager(GridLayoutManager(this@StaffUnitSelectionActivity, 2))
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext -> {
                for (j in arrayFullList.indices) {
                    if (arrayFullList.get(j).isSelected) {
                        val indices = selectedUnits.mapIndexedNotNull { index, event ->
                            if (event.unUnitID.equals(arrayFullList.get(j).unUnitID)) index else null
                        }
                        if (indices == null || indices.size == 0) {
                            selectedUnits.add(arrayFullList[j])
                        }
                    }
                }
                val _intent = Intent(this@StaffUnitSelectionActivity, StaffBlockSelectionActivity::class.java)
                var json = Gson().toJson(selectedUnits)
                _intent.putExtra(FLOW_TYPE, DELIVERY)
                _intent.putExtra(VISITOR_TYPE, DELIVERY)
                _intent.putExtra(SELECTED_UNITS, json)
                _intent.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                _intent.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                _intent.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                startActivity(_intent)
                finish()

            }

            R.id.btn_page_next -> {
                btn_page_prev.isEnabled = true
                //var filtered = pageArrayList!!.filter { it.isActive.equals(true) }
                var index = -1
                // val indices = pageArrayList!!.mapIndexedNotNull { index, event ->  if (event.isActive.equals(true)) index else null}
                for (i in 0 until pageArrayList.size) {
                    if (pageArrayList[i].isActive == true) {
                        index = i
                        break
                    }
                }
                if (index != -1) {
                    if ((index + 1) < pageArrayList.size) {
                        pageArrayList[index].isActive = false
                        pageArrayList[index + 1].isActive = true
                        pageNumberAdapter!!.notifyDataSetChanged()
                        PAGE_NUMBER += 1
                        setPageData()
                    }


                }
            }

            R.id.btn_page_prev -> {
                btn_page_next.isEnabled = true
                var index = -1
                // val indices = pageArrayList!!.mapIndexedNotNull { index, event ->  if (event.isActive.equals(true)) index else null}
                for (i in 0 until pageArrayList.size) {
                    if (pageArrayList[i].isActive == true) {
                        index = i
                        break
                    }
                }
                // val indices = pageArrayList!!.mapIndexedNotNull { index, event ->  if (event.isActive.equals(true)) index else null}
                if (index != -1) {
                    if ((index - 1) >= 0) {
                        pageArrayList[index].isActive = false
                        pageArrayList[index - 1].isActive = true
                        pageNumberAdapter!!.notifyDataSetChanged()
                        PAGE_NUMBER -= 1
                        setPageData()
                    }


                }
            }

        }
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tap next to continue", Toast.LENGTH_SHORT).show()
    }

    /**
     * onPageClick handles the click of page number
     * This changes the active page number color and invokes setPageData
     * for setting the data based on page number
     */
    fun onPageClick(selected: PaginationData, index: Int) {
        var arr = ArrayList<PaginationData>()
        for (pageObj in pageArrayList) {
            if (selected.pageNumber.equals(pageObj.pageNumber)) {
                var obj = pageObj
                obj.isActive = true
                arr.add(obj)
            } else {
                var obj = pageObj
                obj.isActive = false
                arr.add(obj)
            }
        }
        pageArrayList = arr
        pageNumberAdapter!!.notifyDataSetChanged()

        /**
         * PaginationData object indexes from 1 and PAGE_NUMBER from 0.
         * So 1 reduced from PAGE_NUMBER to sync the index
         */
        PAGE_NUMBER = (selected.pageNumber.toInt()) - 1
        setPageData()

    }

    /**
     * managePageNumber sets the page number based on API response and total Units per page.
     * LIMIT  = Total Units per Page
     */
    private fun managePageNumber() {
        if (arrayFullList != null && arrayFullList.size > 0) {

            if (arrayFullList.size > LIMIT) {
                var absolutePageNumber: Int = arrayFullList.size / LIMIT
                if (arrayFullList.size - (absolutePageNumber * LIMIT) != 0) {
                    absolutePageNumber += 1
                }
                for (i in 0 until absolutePageNumber) {
                    var isActive: Boolean = true
                    if (i > 0) {
                        isActive = false
                    }
                    var firstPage: PaginationData = PaginationData("" + (i + 1), isActive)
                    pageArrayList.add(firstPage)
                }

            } else {
                var firstPage: PaginationData = PaginationData("1", true)
                pageArrayList.add(firstPage)
            }

            pageNumberAdapter =
                PaginationAdapter(
                    this@StaffUnitSelectionActivity,
                    pageArrayList,
                    clickListener = { page, index ->
                        onPageClick(page, index)
                    })
            rv_page.adapter = pageNumberAdapter

            rv_page.layoutManager =
                LinearLayoutManager(
                    this@StaffUnitSelectionActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )


        }
    }

    /**
     * setPageData does the pagination logic
     * over ArrayList (assuming arrayFullList object contains complete list of Units)
     */
    private fun setPageData() {
        if (arrayFullList.size <= LIMIT) {
            btn_page_next.visibility = View.INVISIBLE
            btn_page_prev.visibility = View.INVISIBLE
            rv_page.visibility = View.INVISIBLE
        } else {
            btn_page_next.visibility = View.VISIBLE
            btn_page_prev.visibility = View.VISIBLE
            rv_page.visibility = View.VISIBLE
        }
        var updatedSelected = ArrayList<UnitPojo>()
        for (i in 0 until arrayFullList.size) {
            var currentUnit: UnitPojo = arrayFullList[i]
            val indices = selectedUnits.mapIndexedNotNull { index, event ->
                if (event.unUnitID.equals(arrayFullList.get(i).unUnitID)) index else null
            }
            currentUnit.isSelected = indices != null && indices.size > 0
            updatedSelected.add(currentUnit)
        }
        arrayFullList = updatedSelected

        if (arrayFullList.size > LIMIT) {
            var start = 0
            var end = 0

            if (PAGE_NUMBER == 0) {
                start = 0
                end = LIMIT - 1
            } else {
                start = PAGE_NUMBER * LIMIT
                end = start + (LIMIT - 1)

                if (arrayFullList.size < end) {
                    end = arrayFullList.size - 1
                }
            }

            /**
             * Sublist end index is exclusive. So an additional one is added to the end index
             */
            try {
                arrayList = ArrayList(arrayFullList.subList(start, end + 1))
            } catch (e: Exception) {
                arrayList = ArrayList(arrayFullList.subList(start, end))
            }
            rv_unit.showProgress()
            orderListAdapter =
                StaffUnitSelectionActivity.UnitListAdapter(
                    arrayList,
                    this@StaffUnitSelectionActivity,
                    checkListener = { arr, ischecked ->
                        onCheckUnit(arr, ischecked)
                    })
            rv_unit.adapter = orderListAdapter
            orderListAdapter!!.notifyDataSetChanged()
            rv_unit.hideProgress()

        } else {
            rv_unit.showProgress()
            orderListAdapter =
                StaffUnitSelectionActivity.UnitListAdapter(
                    arrayFullList,
                    this@StaffUnitSelectionActivity,
                    checkListener = { arr, ischecked ->
                        onCheckUnit(arr, ischecked)
                    })
            rv_unit.adapter = orderListAdapter
            orderListAdapter!!.notifyDataSetChanged()
            rv_unit.hideProgress()

        }

        managePageNavButtons()
    }


    private fun onCheckUnit(checked: UnitPojo, isSelected: Boolean) {
        var arr = ArrayList<UnitPojo>()
        for (unitObj in arrayFullList) {
            if (checked.unUnitID.equals(unitObj.unUnitID)) {
                var obj = unitObj
                obj.isSelected = isSelected
                arr.add(obj)
            } else {
                arr.add(unitObj)
            }
        }
        arrayFullList = arr
        if (!isSelected) {

            val indices =
                selectedUnits.mapIndexedNotNull { index, event -> if (event.unUnitID.equals(checked.unUnitID)) index else null }
            if (indices != null && indices.size > 0) {
                selectedUnits.removeAt(indices[0])
            }
        } else {
            selectedUnits.add(checked)
        }
        //orderListAdapter!!.notifyDataSetChanged();
    }

    private fun managePageNavButtons() {
        if (PAGE_NUMBER == 0) {
            btn_page_prev.setImageDrawable(this.resources.getDrawable(R.drawable.prev_page))
        } else {
            btn_page_prev.setImageDrawable(this.resources.getDrawable(R.drawable.prev_active))
        }

        if (PAGE_NUMBER == (pageArrayList.size - 1)) {
            btn_page_next.setImageDrawable(this.resources.getDrawable(R.drawable.next_inactive))
        } else {
            btn_page_next.setImageDrawable(this.resources.getDrawable(R.drawable.next_page))
        }
    }


    private fun getUnitsFromBlock() {
        showProgressrefresh()
        RetrofitClinet.instance
            .getUnitsFromBlock(CHAMPTOKEN, "" + blockId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitsList<ArrayList<UnitPojo>>>() {

                override fun onSuccessResponse(UnitList: UnitsList<ArrayList<UnitPojo>>) {
                    dismissProgressrefresh()
                    if (UnitList.success == true) {
                        arrayFullList = UnitList.data.unitsByBlockID
                        setPageData()
                        managePageNumber()

                    } else {
                        rv_unit.setEmptyAdapter("No items to show!", false, 0)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(this@StaffUnitSelectionActivity, "No Units Found ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(this@StaffUnitSelectionActivity, "No network call ", Toast.LENGTH_LONG).show()
                }
            })

    }

    class UnitListAdapter(private val listVistor: ArrayList<UnitPojo>, private val mcontext: Context, val checkListener:(UnitPojo, Boolean) -> Unit) :
        RecyclerView.Adapter<UnitListAdapter.MenuHolder>() {

        private val mInflater: LayoutInflater
        var family_recyclerview:RecyclerView?=null
        var familMembersAdapter: FamilMembersAdapter?=null
        var arrayFamilyList = ArrayList<FamilyMember>()

        init {
            mInflater = LayoutInflater.from(mcontext)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
            val mainGroup = mInflater.inflate(R.layout.layout_unit_adapter_row, parent, false) as ViewGroup
            return MenuHolder(mainGroup)
        }


        override fun onBindViewHolder(holder: MenuHolder, position: Int) {
            val orderData = listVistor.get(position)
            val vistordate = orderData.asAssnID
            holder.apartmentNamee.text = orderData.unUniName
            if (listVistor.get(position).isSelected) {
                holder.cb_unit.isChecked = true
                holder.cb_unit.setBackgroundColor(mcontext.resources.getColor(android.R.color.transparent))
            } else {
                holder.cb_unit.isChecked = false
                holder.cb_unit.setBackgroundResource(R.drawable.checkbox_state_style)
            }

            holder.cb_unit.setOnCheckedChangeListener { buttonView, isChecked ->
                // Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
                listVistor.get(position).isSelected = isChecked
                if (isChecked) {
                    holder.cb_unit.setBackgroundColor(mcontext.resources.getColor(android.R.color.transparent))
                } else {
                    holder.cb_unit.setBackgroundResource(R.drawable.checkbox_state_style)
                }
                checkListener(listVistor.get(position), isChecked)

            }
            //  Log.d("cdvd",orderData?.unUniName+" "+orderData.owner.uoisdCode+""+orderData.owner.uoMobile);

            holder.iv_unit.setOnClickListener {
                if (orderData.owner.size == 0 && orderData.tenant.size == 0) {
                    //  Log.d("cdvd 2", "" + orderData?.owner[0].uoisdCode + " " + orderData?.owner[0].uoMobile);

//if(orderData.owner[0].uoMobile!=null) {



                    val dialogBuilder = AlertDialog.Builder(mcontext)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Not a Valid Mobile Number")
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()


                        })
                    // negative button text and action
//                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                                dialog, id -> dialog.cancel()
//                        })

                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    // show alert dialog
                    alert.show()
                } else {
                    // Toast.makeText(mcontext,"Not a Valid Mobile Number",Toast.LENGTH_SHORT).show()

//                    val intent = Intent(Intent.ACTION_CALL);
//                    intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile)
//                    mcontext.startActivity(intent)




                    if (orderData.tenant.size != 0) {

                        val alertadd = AlertDialog.Builder(mcontext)
                        val factory = LayoutInflater.from(mcontext)
                        val view = factory.inflate(R.layout.layout_phonenumber, null)
                        var tv_number1: TextView? = null
                        tv_number1 = view.findViewById(R.id.tv_number1)
                        var tv_number2: TextView? = null
                        tv_number2 = view.findViewById(R.id.tv_number2)


                        var iv_unit1: ImageView? = null
                        iv_unit1 = view.findViewById(R.id.iv_unit1)

                        var iv_unit2: ImageView? = null
                        iv_unit2 = view.findViewById(R.id.iv_unit2)

                        family_recyclerview=view.findViewById(R.id.rv_family)
                        family_recyclerview?.layoutManager = LinearLayoutManager(
                            mcontext,
                            LinearLayoutManager.VERTICAL,
                            false
                        )


                        if(orderData.unOcStat.contains("Sold Owner Occupied Unit")){

                            RetrofitClinet.instance.getFamilyMemberList(ConstantUtils.OYE247TOKEN, orderData.unUnitID, orderData.asAssnID.toString(), orderData.owner[0].acAccntID.toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : CommonDisposable<GetFamilyMemberResponse<ArrayList<FamilyMember>>>() {

                                    override fun onSuccessResponse(getdata: GetFamilyMemberResponse<ArrayList<FamilyMember>>) {

                                        if (getdata.success) {


                                            // var familydataList=ArrayList<FamilyMember>()
                                            arrayFamilyList=getdata.data.familyMembers
                                            familMembersAdapter=FamilMembersAdapter(arrayFamilyList,mcontext)
                                            family_recyclerview!!.adapter=familMembersAdapter
                                        }

                                    }

                                    override fun onErrorResponse(e: Throwable) {

                                    }

                                    override fun noNetowork() {
                                    }
                                })


                        }else if(orderData.unOcStat.contains("Sold Tenant Occupied Unit")){

//                            Toast.makeText(mcontext,"222",Toast.LENGTH_LONG).show()
//                            getFamilyMemberData(orderData.unUnitID,orderData.asAssnID,orderData.tenant[0].acAccntID)

                            RetrofitClinet.instance.getFamilyMemberList(ConstantUtils.OYE247TOKEN, orderData.unUnitID, orderData.asAssnID.toString(), orderData.tenant[0].acAccntID.toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : CommonDisposable<GetFamilyMemberResponse<ArrayList<FamilyMember>>>() {

                                    override fun onSuccessResponse(getdata: GetFamilyMemberResponse<ArrayList<FamilyMember>>) {

                                        if (getdata.success) {


                                            // var familydataList=ArrayList<FamilyMember>()
                                            arrayFamilyList=getdata.data.familyMembers
                           familMembersAdapter=FamilMembersAdapter(arrayFamilyList,mcontext)
                           family_recyclerview!!.adapter=familMembersAdapter
                                        }

                                    }

                                    override fun onErrorResponse(e: Throwable) {

                                    }

                                    override fun noNetowork() {
                                    }
                                })


                        }else if(orderData.unOcStat.contains("UnSold Tenant Occupied Unit")){

                            RetrofitClinet.instance.getFamilyMemberList(ConstantUtils.OYE247TOKEN, orderData.unUnitID, orderData.asAssnID.toString(), orderData.tenant[0].acAccntID.toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : CommonDisposable<GetFamilyMemberResponse<ArrayList<FamilyMember>>>() {

                                    override fun onSuccessResponse(getdata: GetFamilyMemberResponse<ArrayList<FamilyMember>>) {

                                        if (getdata.success) {


                                            // var familydataList=ArrayList<FamilyMember>()
                                            arrayFamilyList=getdata.data.familyMembers
                                            //   Toast.makeText(mcontext,arrayFamilyList.size.toString(),Toast.LENGTH_LONG).show()
                                            familMembersAdapter=FamilMembersAdapter(arrayFamilyList,mcontext)
                                            family_recyclerview!!.adapter=familMembersAdapter
                                        }

                                    }

                                    override fun onErrorResponse(e: Throwable) {

                                    }

                                    override fun noNetowork() {
                                    }
                                })


                        }else if(orderData.unOcStat.contains("UnSold Vacant Unit")){

                        }else if(orderData.unOcStat.contains("Sold Vacant Unit")){

                        }
                        else{

                        }

                        try {

                            if (orderData.tenant[0].utMobile.equals("")) {
                                iv_unit1.visibility = View.GONE
                                tv_number1.visibility = View.GONE
                            } else {

                                iv_unit1.visibility = View.VISIBLE
                                tv_number1.visibility = View.VISIBLE
                                tv_number1.setText("Tenant's mobile number")
                            }

                            if (orderData.tenant[0].utMobile1.equals("")) {
                                iv_unit2.visibility = View.GONE
                                tv_number2.visibility = View.GONE
                            } else {

                                iv_unit2.visibility = View.VISIBLE
                                tv_number2.visibility = View.VISIBLE
                                tv_number2.setText("Tenant's alternative mobile number")
                            }


                        } catch (e: IndexOutOfBoundsException) {

                        }

                        iv_unit1.setOnClickListener {

                            var agentNumber="AGENTNUMBER="+orderData.tenant[0].utMobile.replace("+91", "")
                            var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                            TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)
                        }


                        iv_unit2.setOnClickListener {

                            var agentNumber="AGENTNUMBER="+orderData.tenant[0].utMobile1.replace("+91", "")
                            var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                            TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)
                        }


                        alertadd.setView(view)
                        alertadd.show()

                    } else {

                        if (orderData.owner.size != 0) {

                            val alertadd = AlertDialog.Builder(mcontext)
                            val factory = LayoutInflater.from(mcontext)
                            val view = factory.inflate(R.layout.layout_phonenumber, null)
                            var tv_number1: TextView? = null
                            tv_number1 = view.findViewById(R.id.tv_number1)
                            var tv_number2: TextView? = null
                            tv_number2 = view.findViewById(R.id.tv_number2)
                            var tv_number3: TextView? = null
                            tv_number3 = view.findViewById(R.id.tv_number3)
                            var tv_number4: TextView? = null
                            tv_number4 = view.findViewById(R.id.tv_number4)
                            var tv_number5: TextView? = null
                            tv_number5 = view.findViewById(R.id.tv_number5)


                            var iv_unit1: ImageView? = null
                            iv_unit1 = view.findViewById(R.id.iv_unit1)

                            var iv_unit2: ImageView? = null
                            iv_unit2 = view.findViewById(R.id.iv_unit2)

                            var iv_unit3: ImageView? = null
                            iv_unit3 = view.findViewById(R.id.iv_unit3)

                            var iv_unit4: ImageView? = null
                            iv_unit4 = view.findViewById(R.id.iv_unit4)

                            var iv_unit5: ImageView? = null
                            iv_unit5 = view.findViewById(R.id.iv_unit5)

                            try {

                                if (orderData.owner[0].uoMobile.equals("")) {
                                    iv_unit1.visibility = View.GONE
                                    tv_number1.visibility = View.GONE
                                }

                                else if (orderData.owner[0].uoMobile.equals("null")) {
                                    iv_unit1.visibility = View.GONE
                                    tv_number1.visibility = View.GONE
                                }
                                else {

                                    iv_unit1.visibility = View.VISIBLE
                                    tv_number1.visibility = View.VISIBLE
                                    tv_number1.setText("Owner's mobile number")
                                }

                                if (orderData.owner[0].uoMobile1.equals("")) {
                                    iv_unit2.visibility = View.GONE
                                    tv_number2.visibility = View.GONE
                                }
                                else if (orderData.owner[0].uoMobile1.equals("null")) {
                                    iv_unit2.visibility = View.GONE
                                    tv_number2.visibility = View.GONE
                                }
                                else {
                                    iv_unit2.visibility = View.VISIBLE
                                    tv_number2.visibility = View.VISIBLE
                                    tv_number2.setText("Owner's alternative mobile number1")
                                }

                                if (orderData.owner[0].uoMobile2.equals("")) {
                                    iv_unit3.visibility = View.GONE
                                    tv_number3.visibility = View.GONE
                                }

                                 else if (orderData.owner[0].uoMobile2.equals("null")) {
                                    iv_unit3.visibility = View.GONE
                                    tv_number3.visibility = View.GONE
                                }
                                else {
                                    iv_unit3.visibility = View.VISIBLE
                                    tv_number3.visibility = View.VISIBLE
                                    tv_number3.setText("Owner's alternative mobile number2")
                                }

                                if (orderData.owner[0].uoMobile3.equals("")) {
                                    iv_unit4.visibility = View.GONE
                                    tv_number4.visibility = View.GONE
                                }
                               else if (orderData.owner[0].uoMobile3.equals("null")) {
                                    iv_unit4.visibility = View.GONE
                                    tv_number4.visibility = View.GONE
                                }
                                else {
                                    iv_unit4.visibility = View.VISIBLE
                                    tv_number4.visibility = View.VISIBLE
                                    tv_number4.setText("Owner's alternative mobile number3")
                                }
                                if (orderData.owner[0].uoMobile4.equals("")) {
                                    iv_unit5!!.visibility = View.GONE
                                    tv_number5.visibility = View.GONE
                                }
                               else if (orderData.owner[0].uoMobile4.equals("null")) {
                                    iv_unit5!!.visibility = View.GONE
                                    tv_number5.visibility = View.GONE
                                }
                                else {
                                    iv_unit5!!.visibility = View.VISIBLE
                                    tv_number5.visibility = View.VISIBLE
                                    tv_number5.setText("Owner's alternative mobile number4")
                                }
                            } catch (e: IndexOutOfBoundsException) {

                            }

                            iv_unit1.setOnClickListener {

                                var agentNumber="AGENTNUMBER="+orderData.owner[0].uoMobile.replace("+91", "")
                                var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                                TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)
                            }

                            iv_unit2.setOnClickListener {


                                var agentNumber="AGENTNUMBER="+orderData.owner[0].uoMobile1.replace("+91", "")
                                var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                                TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)

                            }

                            iv_unit3.setOnClickListener {

                                var agentNumber="AGENTNUMBER="+orderData.owner[0].uoMobile2.replace("+91", "")
                                var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                                TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)

                            }
                            iv_unit4.setOnClickListener {


                                var agentNumber="AGENTNUMBER="+orderData.owner[0].uoMobile3.replace("+91", "")
                                var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                                TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)


                            }
                            iv_unit5.setOnClickListener {


                                var agentNumber="AGENTNUMBER="+orderData.owner[0].uoMobile4.replace("+91", "")
                                var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                                TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)


                            }




                            alertadd.setView(view)
                            alertadd.show()
                        } else {

                            val dialogBuilder = AlertDialog.Builder(mcontext)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Not a Valid Mobile Number")
                                // if the dialog is cancelable
                                .setCancelable(false)
                                // positive button text and action
                                .setPositiveButton(
                                    "Ok",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        dialog.cancel()


                                    })
                            // negative button text and action
//                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                                dialog, id -> dialog.cancel()
//                        })

                            // create dialog box
                            val alert = dialogBuilder.create()
                            // set title for alert dialog box
                            // show alert dialog
                            alert.show()

                        }

                    }

                }
            }
            holder.lv_itemrecyclerview.setOnClickListener({
                val mcontextintent = (mcontext as Activity).intent

                val intent = Intent(mcontext, StaffMobileNumberScreen::class.java)
                intent.putExtra(FLOW_TYPE, mcontextintent.getStringExtra(FLOW_TYPE))
                intent.putExtra(VISITOR_TYPE, mcontextintent.getStringExtra(VISITOR_TYPE))
                intent.putExtra(COMPANY_NAME, mcontextintent.getStringExtra(COMPANY_NAME))
                intent.putExtra(UNITID, orderData.unUnitID)
                intent.putExtra(UNITNAME, orderData.unUniName)
//                mcontext.startActivity(intent)
//                (mcontext as Activity).finish()

                if (listVistor.get(position).isSelected) {
                    listVistor.get(position).isSelected = false
                    holder.cb_unit.isChecked = false
                } else {
                    listVistor.get(position).isSelected = true
                    holder.cb_unit.isChecked = true
                }
                //checkListener(listVistor!!.get(position))

            })
        }

        override fun getItemCount(): Int {
            return listVistor.size
        }


        fun getFamilyMemberData(unitId: String, assnID: Int, accountId: Int){
            RetrofitClinet.instance.getFamilyMemberList(ConstantUtils.OYE247TOKEN, unitId, assnID.toString(), accountId.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CommonDisposable<GetFamilyMemberResponse<ArrayList<FamilyMember>>>() {

                    override fun onSuccessResponse(getdata: GetFamilyMemberResponse<ArrayList<FamilyMember>>) {

                        if (getdata.success) {


                           // var familydataList=ArrayList<FamilyMember>()
                            arrayFamilyList=getdata.data.familyMembers
                         //   Toast.makeText(mcontext,arrayFamilyList.size.toString(),Toast.LENGTH_LONG).show()
//                           familMembersAdapter=FamilMembersAdapter(familydataList,mcontext)
//                           family_recyclerview!!.adapter=familMembersAdapter
                        }

                    }

                    override fun onErrorResponse(e: Throwable) {

                    }

                    override fun noNetowork() {
                    }
                })

        }

        inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {

            val iv_unit: ImageView
            val cb_unit: AppCompatCheckBox
            val apartmentNamee: TextView
            val lv_itemrecyclerview: RelativeLayout

            init {

                iv_unit = view.findViewById(R.id.iv_unit)
                cb_unit = view.findViewById(R.id.cb_unit)
                apartmentNamee = view.findViewById(R.id.tv_unit)
                lv_itemrecyclerview = view.findViewById(R.id.lv_itemrecyclerview)

            }

        }
    }


}