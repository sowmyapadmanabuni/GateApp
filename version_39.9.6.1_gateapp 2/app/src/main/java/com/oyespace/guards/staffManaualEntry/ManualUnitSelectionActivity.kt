package com.oyespace.guards.staffManaualEntry

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.adapter.PaginationAdapter
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.PaginationData
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.pojo.UnitsList
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_unit_list.*
import kotlinx.android.synthetic.main.pager_view.*
import kotlinx.android.synthetic.main.subtitle_bar.*
import kotlinx.android.synthetic.main.title_bar.*

class ManualUnitSelectionActivity : BaseKotlinActivity(), View.OnClickListener {

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

    private val LIMIT = 10;
    var PAGE_NUMBER = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_selection)

        header_title.setText(this.resources.getString(R.string.units_list_title));
        header_subtitle.setText("A Block")

        /**
         * Setting status bar color (Only for Lollipop & above)
         */
        //setDarkStatusBar()

        try {
            blockId = intent.getIntExtra(ConstantUtils.SELECTED_BLOCK, -1);
            blockName = "" + intent.getStringExtra(ConstantUtils.SELECTED_BLOCK_NAME);
            header_subtitle.setText(blockName)
            var json: String = (intent.getStringExtra(SELECTED_UNITS))
            var selArray: Array<UnitPojo> = Gson().fromJson(json, Array<UnitPojo>::class.java)
            selectedUnits = ArrayList(selArray.asList())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getUnitsFromBlock()

        rv_unit.setLayoutManager(
            androidx.recyclerview.widget.GridLayoutManager(
                this@ManualUnitSelectionActivity,
                2
            )
        )
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext -> {
                for (j in arrayFullList.indices) {
                    if (arrayFullList.get(j).isSelected) {
                        val indices = selectedUnits!!.mapIndexedNotNull { index, event ->
                            if (event.unUnitID.equals(arrayFullList.get(j).unUnitID)) index else null
                        }
                        if (indices == null || indices.size == 0) {
                            selectedUnits.add(arrayFullList[j])
                        }
                    }
                }
                val _intent = Intent(
                    this@ManualUnitSelectionActivity,
                    ManulBlockSelectionActivity::class.java
                )
                var json = Gson().toJson(selectedUnits)
                _intent.putExtra(FLOW_TYPE, DELIVERY)
                _intent.putExtra(VISITOR_TYPE, DELIVERY)
                _intent.putExtra(SELECTED_UNITS, json);
                Log.v("JSONNN", json)
                _intent.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                _intent.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                _intent.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                _intent.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
                _intent.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
                _intent.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                _intent.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
                _intent.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
                _intent.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
                _intent.putExtra("BIRTHDAY", intent.getStringExtra("BIRTHDAY"))
                _intent.putExtra("UNITNAME", "")
                startActivity(_intent)
                finish()

            }

            R.id.btn_page_next -> {
                btn_page_prev.isEnabled = true;
                //var filtered = pageArrayList!!.filter { it.isActive.equals(true) }
                var index = -1
                // val indices = pageArrayList!!.mapIndexedNotNull { index, event ->  if (event.isActive.equals(true)) index else null}
                for (i in 0 until pageArrayList.size) {
                    if (pageArrayList[i].isActive == true) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    if ((index + 1) < pageArrayList.size) {
                        pageArrayList[index].isActive = false;
                        pageArrayList[index + 1].isActive = true;
                        pageNumberAdapter!!.notifyDataSetChanged()
                        PAGE_NUMBER += 1;
                        setPageData()
                    }


                }
            }

            R.id.btn_page_prev -> {
                btn_page_next.isEnabled = true;
                var index = -1
                // val indices = pageArrayList!!.mapIndexedNotNull { index, event ->  if (event.isActive.equals(true)) index else null}
                for (i in 0 until pageArrayList.size) {
                    if (pageArrayList[i].isActive == true) {
                        index = i;
                        break;
                    }
                }
                // val indices = pageArrayList!!.mapIndexedNotNull { index, event ->  if (event.isActive.equals(true)) index else null}
                if (index != -1) {
                    if ((index - 1) >= 0) {
                        pageArrayList[index].isActive = false;
                        pageArrayList[index - 1].isActive = true;
                        pageNumberAdapter!!.notifyDataSetChanged()
                        PAGE_NUMBER -= 1;
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
        var arr = ArrayList<PaginationData>();
        for (pageObj in pageArrayList) {
            if (selected.pageNumber.equals(pageObj.pageNumber)) {
                var obj = pageObj;
                obj.isActive = true;
                arr.add(obj);
            } else {
                var obj = pageObj;
                obj.isActive = false;
                arr.add(obj);
            }
        }
        pageArrayList = arr;
        pageNumberAdapter!!.notifyDataSetChanged();

        /**
         * PaginationData object indexes from 1 and PAGE_NUMBER from 0.
         * So 1 reduced from PAGE_NUMBER to sync the index
         */
        PAGE_NUMBER = (selected.pageNumber.toInt()) - 1;
        setPageData()

    }

    /**
     * managePageNumber sets the page number based on API response and total Units per page.
     * LIMIT  = Total Units per Page
     */
    private fun managePageNumber() {
        if (arrayFullList != null && arrayFullList.size > 0) {

            if (arrayFullList.size > LIMIT) {
                var absolutePageNumber: Int = arrayFullList.size / LIMIT;
                if (arrayFullList.size - (absolutePageNumber * LIMIT) != 0) {
                    absolutePageNumber += 1;
                }
                for (i in 0 until absolutePageNumber) {
                    var isActive: Boolean = true;
                    if (i > 0) {
                        isActive = false
                    }
                    var firstPage: PaginationData = PaginationData("" + (i + 1), isActive);
                    pageArrayList.add(firstPage);
                }

            } else {
                var firstPage: PaginationData = PaginationData("1", true);
                pageArrayList.add(firstPage);
            }

            pageNumberAdapter =
                PaginationAdapter(
                    this@ManualUnitSelectionActivity,
                    pageArrayList,
                    clickListener = { page, index ->
                        onPageClick(page, index)
                    })
            rv_page.adapter = pageNumberAdapter

            rv_page.setLayoutManager(
                androidx.recyclerview.widget.LinearLayoutManager(
                    this@ManualUnitSelectionActivity,
                    androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                    false
                )
            );


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
            rv_page.visibility = View.INVISIBLE;
        } else {
            btn_page_next.visibility = View.VISIBLE
            btn_page_prev.visibility = View.VISIBLE
            rv_page.visibility = View.VISIBLE
        }
        var updatedSelected = ArrayList<UnitPojo>();
        for (i in 0 until arrayFullList.size) {
            var currentUnit: UnitPojo = arrayFullList[i];
            val indices = selectedUnits!!.mapIndexedNotNull { index, event ->
                if (event.unUnitID.equals(arrayFullList.get(i).unUnitID)) index else null
            }
            if (indices != null && indices.size > 0) {
                currentUnit.isSelected = true;
            } else {
                currentUnit.isSelected = false;
            }
            updatedSelected.add(currentUnit);
        }
        arrayFullList = updatedSelected;

        if (arrayFullList.size > LIMIT) {
            var start = 0;
            var end = 0;

            if (PAGE_NUMBER == 0) {
                start = 0;
                end = LIMIT - 1
            } else {
                start = PAGE_NUMBER * LIMIT;
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

            }
            rv_unit.showProgress()
            orderListAdapter =
                ManualUnitSelectionActivity.UnitListAdapter(
                    arrayList as ArrayList<UnitPojo>,
                    this@ManualUnitSelectionActivity,
                    checkListener = { arr, ischecked ->
                        onCheckUnit(arr, ischecked)
                    })
            rv_unit.adapter = orderListAdapter
            orderListAdapter!!.notifyDataSetChanged()
            rv_unit.hideProgress()

        } else {
            rv_unit.showProgress()
            orderListAdapter =
                ManualUnitSelectionActivity.UnitListAdapter(
                    arrayFullList,
                    this@ManualUnitSelectionActivity,
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
        var arr = ArrayList<UnitPojo>();
        for (unitObj in arrayFullList) {
            if (checked.unUnitID.equals(unitObj.unUnitID)) {
                var obj = unitObj;
                obj.isSelected = isSelected;
                arr.add(obj);
            } else {
                arr.add(unitObj);
            }
        }
        arrayFullList = arr;
        if (!isSelected) {

            val indices = selectedUnits!!.mapIndexedNotNull { index, event ->
                if (event.unUnitID.equals(checked.unUnitID)) index else null
            }
            if (indices != null && indices.size > 0) {
                selectedUnits.removeAt(indices[0]);
            }
        } else {
            selectedUnits.add(checked);
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
                        arrayFullList = UnitList.data.unitsByBlockID;
                        setPageData();
                        managePageNumber()

                    } else {
                        rv_unit.setEmptyAdapter("No items to show!", false, 0)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(
                        this@ManualUnitSelectionActivity,
                        "No Units Found ",
                        Toast.LENGTH_LONG
                    ).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(
                        this@ManualUnitSelectionActivity,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

    }

    class UnitListAdapter(
        private val listVistor: ArrayList<UnitPojo>,
        private val mcontext: Context,
        val checkListener: (UnitPojo, Boolean) -> Unit
    ) :
        androidx.recyclerview.widget.RecyclerView.Adapter<UnitListAdapter.MenuHolder>() {

        private val mInflater: LayoutInflater


        init {
            mInflater = LayoutInflater.from(mcontext)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
            val mainGroup =
                mInflater.inflate(R.layout.layout_unit_adapter_row, parent, false) as ViewGroup
            return MenuHolder(mainGroup)
        }


        override fun onBindViewHolder(holder: MenuHolder, position: Int) {
            val orderData = listVistor?.get(position)
            val vistordate = orderData?.asAssnID
            holder.apartmentNamee.text = orderData?.unUniName
            if (listVistor!!.get(position).isSelected) {
                holder.cb_unit.setChecked(true)
                holder.cb_unit.setBackgroundColor(mcontext.resources.getColor(android.R.color.transparent));
            } else {
                holder.cb_unit.isChecked = false;
                holder.cb_unit.setBackgroundResource(R.drawable.checkbox_state_style);
            }

            holder.cb_unit.setOnCheckedChangeListener { buttonView, isChecked ->
                // Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
                listVistor!!.get(position).isSelected = isChecked
                if (isChecked) {
                    holder.cb_unit.setBackgroundColor(mcontext.resources.getColor(android.R.color.transparent));
                } else {
                    holder.cb_unit.setBackgroundResource(R.drawable.checkbox_state_style);
                }
                checkListener(listVistor!!.get(position), isChecked)

            }
            //  Log.d("cdvd",orderData?.unUniName+" "+orderData.owner.uoisdCode+""+orderData.owner.uoMobile);

            holder.iv_unit.setOnClickListener {
                if (orderData!!.owner.size == 0 && orderData!!.tenant.size == 0) {
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


                    if (orderData!!.tenant.size != 0) {

                        val alertadd = androidx.appcompat.app.AlertDialog.Builder(mcontext)
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

                        try {

                            if (orderData.tenant[0].utMobile.equals("")) {
                                iv_unit1.visibility = View.GONE
                                tv_number1.visibility = View.GONE
                            } else {

                                iv_unit1.visibility = View.VISIBLE
                                tv_number1.visibility = View.VISIBLE
                                tv_number1.setText(orderData.tenant[0].utMobile)
                            }

                            if (orderData.tenant[0].utMobile1.equals("")) {
                                iv_unit2.visibility = View.GONE
                                tv_number2.visibility = View.GONE
                            } else {

                                iv_unit2.visibility = View.VISIBLE
                                tv_number2.visibility = View.VISIBLE
                                tv_number2.setText(orderData.tenant[0].utMobile1)
                            }


                        } catch (e: IndexOutOfBoundsException) {

                        }

                        iv_unit1.setOnClickListener {

                            val intent = Intent(Intent.ACTION_CALL);
                            intent.data = Uri.parse("tel:" + orderData.tenant[0].utMobile)
                            mcontext.startActivity(intent)

                        }


                        iv_unit2.setOnClickListener {

                            val intent = Intent(Intent.ACTION_CALL);
                            intent.data = Uri.parse("tel:" + orderData.tenant[0].utMobile1)
                            mcontext.startActivity(intent)

                        }


                        alertadd.setView(view)
                        alertadd.show()

                    } else {

                        if (orderData!!.owner.size != 0) {

                            val alertadd = androidx.appcompat.app.AlertDialog.Builder(mcontext)
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
                                } else {

                                    iv_unit1.visibility = View.VISIBLE
                                    tv_number1.visibility = View.VISIBLE
                                    tv_number1.setText(orderData.owner[0].uoMobile)
                                }

                                if (orderData.owner[0].uoMobile1.equals("")) {
                                    iv_unit2.visibility = View.GONE
                                    tv_number2.visibility = View.GONE
                                } else {
                                    iv_unit2.visibility = View.VISIBLE
                                    tv_number2.visibility = View.VISIBLE
                                    tv_number2.setText(orderData.owner[0].uoMobile1)
                                }

                                if (orderData.owner[0].uoMobile2.equals("")) {
                                    iv_unit3.visibility = View.GONE
                                    tv_number3.visibility = View.GONE
                                } else {
                                    iv_unit3.visibility = View.VISIBLE
                                    tv_number3.visibility = View.VISIBLE
                                    tv_number3.setText(orderData.owner[0].uoMobile2)
                                }

                                if (orderData.owner[0].uoMobile3.equals("")) {
                                    iv_unit4.visibility = View.GONE
                                    tv_number4.visibility = View.GONE
                                } else {
                                    iv_unit4.visibility = View.VISIBLE
                                    tv_number4.visibility = View.VISIBLE
                                    tv_number4.setText(orderData.owner[0].uoMobile3)
                                }
                                if (orderData.owner[0].uoMobile4.equals("")) {
                                    iv_unit5!!.visibility = View.GONE
                                    tv_number5.visibility = View.GONE
                                } else {
                                    iv_unit5!!.visibility = View.VISIBLE
                                    tv_number5.visibility = View.VISIBLE
                                    tv_number5.setText(orderData.owner[0].uoMobile4)
                                }
                            } catch (e: IndexOutOfBoundsException) {

                            }

                            iv_unit1.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile)
                                mcontext.startActivity(intent)

                            }

                            iv_unit2.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile1)
                                mcontext.startActivity(intent)

                            }

                            iv_unit3.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile2)
                                mcontext.startActivity(intent)

                            }
                            iv_unit4.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile3)
                                mcontext.startActivity(intent)

                            }
                            iv_unit5.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile4)
                                mcontext.startActivity(intent)

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

                val intent = Intent(mcontext, ManualMobileNumberScreen::class.java)
                intent.putExtra(FLOW_TYPE, mcontextintent.getStringExtra(FLOW_TYPE))
                intent.putExtra(VISITOR_TYPE, mcontextintent.getStringExtra(VISITOR_TYPE))
                intent.putExtra(COMPANY_NAME, mcontextintent.getStringExtra(COMPANY_NAME))
                intent.putExtra(UNITID, orderData?.unUnitID)
                intent.putExtra(UNITNAME, orderData?.unUniName)
                intent.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
                intent.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
                intent.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                intent.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
                intent.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
                intent.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
                intent.putExtra("BIRTHDAY", intent.getStringExtra("BIRTHDAY"))
//                mcontext.startActivity(intent)
//                (mcontext as Activity).finish()

                if (listVistor!!.get(position).isSelected) {
                    listVistor!!.get(position).isSelected = false
                    holder.cb_unit.setChecked(false)
                } else {
                    listVistor!!.get(position).isSelected = true
                    holder.cb_unit.setChecked(true)
                }
                //checkListener(listVistor!!.get(position))

            })
        }

        override fun getItemCount(): Int {
            return listVistor?.size ?: 0
        }

        inner class MenuHolder(private val view: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

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