package com.oyespace.guards.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.adapter.FamilMembersAdapter
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.FamilyMember
import com.oyespace.guards.pojo.GetFamilyMemberResponse
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.pojo.UnitsList
import com.oyespace.guards.staffManaualEntry.ManualBlockTabsActivity
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.OnDataPass
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.TaptoCallApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UnitsFragment : Fragment() {
    lateinit var dataPasser: OnDataPass
    var data:String?=null
    var v: View? = null
    var list:RecyclerView?=null
    var empty:TextView?=null
    var arrayFullList = ArrayList<UnitPojo>()
    var orderListAdapter: UnitListAdapter? = null
    var selectedUnits = ArrayList<UnitPojo>()
    fun newInstance(data: String?): UnitsFragment? {
        val fragment: UnitsFragment = UnitsFragment()
        val args = Bundle()
        args.putString("someValue", data)
        fragment.setArguments(args)
        return fragment
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_unit, container, false);

        list= v!!.findViewById(R.id.list)
        empty=v!!.findViewById(R.id.empty)
      //  empty!!.text=getArguments()!!.getString("someValue")
        getUnitsFromBlock()
        list!!.setLayoutManager(GridLayoutManager(activity, 2))
        return v
    }

    companion object {

    }
    private fun getUnitsFromBlock() {


        RetrofitClinet.instance
            .getUnitsFromBlock(ConstantUtils.CHAMPTOKEN, "" + getArguments()!!.getString("someValue"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitsList<ArrayList<UnitPojo>>>() {

                override fun onSuccessResponse(UnitList: UnitsList<ArrayList<UnitPojo>>) {
                    if (UnitList.success == true) {

                        arrayFullList = UnitList.data.unitsByBlockID
                        orderListAdapter =
                           UnitListAdapter(
                               UnitList.data.unitsByBlockID,
                                activity!!,
                                checkListener = { arr, ischecked ->
                                    onCheckUnit(arr, ischecked)
                                })
                        list!!.adapter = orderListAdapter
                        orderListAdapter!!.notifyDataSetChanged()
//                       arrayFullList = UnitList.data.unitsByBlockID
//                        setPageData()
//                        managePageNumber()

                    } else {
                       // list.setEmptyAdapter("No items to show!", false, 0)
                        empty!!.visibility=View.VISIBLE
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                   // Toast.makeText(activity, "No Units Found ", Toast.LENGTH_LONG).show()
                    empty!!.visibility=View.VISIBLE
                    list!!.visibility= View.GONE
                }

                override fun noNetowork() {
                    Toast.makeText(activity, "No network call ", Toast.LENGTH_LONG).show()
                }
            })

    }
    class UnitListAdapter(private val listVistor: ArrayList<UnitPojo>, private val mcontext: Context, val checkListener:(UnitPojo, Boolean) -> Unit) :
        RecyclerView.Adapter<UnitListAdapter.MenuHolder>() {

        private val mInflater: LayoutInflater
        var family_recyclerview: RecyclerView?=null
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
                                            familMembersAdapter=
                                                FamilMembersAdapter(arrayFamilyList,mcontext)
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

                            val alert = dialogBuilder.create()
                            alert.show()

                        }

                    }

                }
            }

        }

        override fun getItemCount(): Int {
            return listVistor.size
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
        var json = Gson().toJson(selectedUnits)
        passData(json)
         }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }
    fun passData(data: String){
        dataPasser.onDataPass(data)
    }
}
