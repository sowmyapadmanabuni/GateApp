package com.oyespace.guards.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import com.oyespace.guards.DataBaseHelper
import com.oyespace.guards.activity.*
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import kotlinx.android.synthetic.main.activity_img_view.*
import kotlinx.android.synthetic.main.activity_mobile_number.*




class StaffAdapter (val items : ArrayList<WorkerDetails>, val mcontext: Context) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() ,Filterable{

    private var searchList: ArrayList<WorkerDetails>? = null
    val dbh:DataBaseHelper= DataBaseHelper(mcontext);
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): StaffViewHolder {
        return StaffViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.layout_staff_adapter_row, parent, false))
    }

    init {
        this.searchList = items
    }
    private var progressDialog: ProgressDialog? = null

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {

        val staffdata = searchList!![position]

        holder.tv_staff.text = staffdata.wkfName + " " + staffdata.wklName
        holder.tv_worktype.text = staffdata.wkDesgn




        if(staffdata?.wkMobile.toString().length > 0){
            holder.iv_call.visibility=View.VISIBLE
        }
        else{
            holder.iv_call.visibility=View.INVISIBLE
        }
        holder.lv_staff.setOnClickListener {


            val alertadd = android.support.v7.app.AlertDialog.Builder(mcontext)
            val factory = LayoutInflater.from(mcontext)
            val view = factory.inflate(R.layout.dialog_big_image, null)
            var dialog_imageview: ImageView? = null
            dialog_imageview = view.findViewById(R.id.dialog_imageview)


            if (staffdata.wkEntryImg.equals("STAFF", true)) {
//                val intent1 = Intent(mcontext, ImgView::class.java)
//                intent1.putExtra(
//                    "URL_IMAGE",
//                    IMAGE_BASE_URL +"Images/PERSONAssociation"+ Prefs.getInt(ASSOCIATION_ID,0)+"STAFF"+staffdata.wkWorkID+".jpg"
//                )
//                mcontext.startActivity(intent1)


                Picasso.with(mcontext)
                    .load(IMAGE_BASE_URL +"Images/"+ staffdata.wkEntryImg)
                    .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(dialog_imageview)

            } else {
//                val intent2 = Intent(mcontext, ImgView::class.java)
//                intent2.putExtra(
//                    "URL_IMAGE",
//                    IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"STAFF"+staffdata.wkWorkID+".jpg"
//                )
//                mcontext.startActivity(intent2)

                Picasso.with(mcontext)
                    .load(IMAGE_BASE_URL +"Images/"+staffdata.wkEntryImg)
                    .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(dialog_imageview)

            }

            alertadd.setView(view)
            alertadd.show()

        }
        holder.btn_biometric.setOnClickListener {
            Log.d("btn_biometric","bf ")

            progressDialog = ProgressDialog(mcontext)
            progressDialog?.isIndeterminate = false
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(true)
            progressDialog?.max=100
            progressDialog?.show()

            val progressRunnable = Runnable { progressDialog?.cancel() }

            val pdCanceller = Handler()
            pdCanceller.postDelayed(progressRunnable, 3000)

            val d = Intent(mcontext, Biometric::class.java)
            d.putExtra(ConstantUtils.WORKER_ID, staffdata.wkWorkID)
            d.putExtra(ConstantUtils.PERSONNAME, staffdata.wkfName + " " + staffdata.wklName)
            d.putExtra(UNITID, staffdata.unUnitID)
            d.putExtra(UNITNAME, staffdata.unUniName)
            d.putExtra(FLOW_TYPE, STAFF_REGISTRATION)
            d.putExtra(VISITOR_TYPE, "STAFF")
            d.putExtra(COMPANY_NAME, staffdata.wkDesgn)
            d.putExtra(COUNTRYCODE,"")
            d.putExtra(MOBILENUMBER, staffdata.wkMobile)
            mcontext.startActivity(d);
            Log.d("btn_biometric","af ")

        }


        Picasso.with(mcontext)
            .load(IMAGE_BASE_URL +"Images/"+staffdata.wkEntryImg)
           .placeholder(R.drawable.placeholder_dark_potrait).error(R.drawable.placeholder_dark_potrait).into(holder.iv_staff)

//        Picasso.with(mcontext)
//            .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"STAFF"+staffdata.wkWorkID+".jpg")
//            .placeholder(R.drawable.placeholder_dark_potrait).error(R.drawable.placeholder_dark_potrait).into(holder.iv_staff)
        // var imgName="PERSON"+"Association"+ASSOCIATIONID+"STAFF" +globalApiObject.data.worker.wkWorkID  + ".jpg"

        holder.btn_makeentry.setOnClickListener {

            holder.btn_makeentry.setEnabled(false)
            holder.btn_makeentry.setClickable(false)

            if(holder.btn_biometric.visibility==View.VISIBLE){
                val dialogBuilder = AlertDialog.Builder(mcontext)

                // set message of alert dialog
                dialogBuilder.setMessage("Fingerprint is not captured")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                            dialog, id ->  val d = Intent(mcontext, Biometric::class.java)
                        d.putExtra(ConstantUtils.WORKER_ID, staffdata.wkWorkID)
                        d.putExtra(ConstantUtils.PERSONNAME, staffdata.wkfName + " " + staffdata.wklName)
                        d.putExtra(UNITID, staffdata.unUnitID)
                        d.putExtra(UNITNAME, staffdata.unUniName)
                        d.putExtra(FLOW_TYPE, STAFF_REGISTRATION)
                        d.putExtra(VISITOR_TYPE, "STAFF")
                        d.putExtra(UNITID,staffdata.unUnitID)
                        d.putExtra(COMPANY_NAME, staffdata.wkDesgn)
                        d.putExtra(COUNTRYCODE,"")
                        d.putExtra(MOBILENUMBER, staffdata.wkMobile)
                        mcontext.startActivity(d);
                        (mcontext as Activity).finish()

                    })
                    // negative button text and action
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                // show alert dialog
                alert.show()
            }
          else if(staffdata?.wkMobile.toString().length > 0){

                val d=Intent(mcontext, MobileNumberforEntryScreen::class.java)
                d.putExtra("UNITID", staffdata.unUnitID)
                d.putExtra("FIRSTNAME", staffdata.wkfName)
                d.putExtra("LASTNAME", staffdata.wklName)
                d.putExtra(MOBILENUMBER, staffdata.wkMobile)
                d.putExtra("DESIGNATION",  staffdata.wkDesgn)
                d.putExtra("WORKTYPE",  staffdata.wkWrkType)
                d.putExtra(ConstantUtils.WORKER_ID,  staffdata.wkWorkID)
                d.putExtra("UNITNAME",  staffdata.unUniName)
                d.putExtra("Image",staffdata.wkEntryImg)
                d.putExtra(COMPANY_NAME, staffdata.wkDesgn)

                mcontext.startActivity(d);
                (mcontext as Activity).finish()

            }
          else{
                val enteredStaff = java.util.ArrayList<VisitorEntryLog>()
                Log.d("Biometric 973", " " + (LocalDb.getVisitorEnteredLog() != null))
                //looping through existing elements
                if (LocalDb.getVisitorEnteredLog() != null) {
                    for (s in LocalDb.getVisitorEnteredLog()!!) {
                        //if the existing elements contains the search input
                        if (s.reRgVisID == staffdata.wkWorkID) {
                            //adding the element to filtered list
                            enteredStaff.add(s)
                        } else {

                        }
                    }
                }
                Log.d("Biometric 983", " ")

                if (enteredStaff.size > 0) run {

                    // t1.speak("Thank You " + enteredStaff[0].vlfName, TextToSpeech.QUEUE_FLUSH, null)
                    Log.d("check 79 ", "bio")
                    Utils.showToast(mcontext, "Duplicate Entry not allowed")

                }else {


                    if(staffdata.unUniName.contains(",")){
                        var unitname_dataList: Array<String>
                        var unitid_dataList: Array<String>
                      //  var unitAccountId_dataList: Array<String>
                        unitname_dataList = staffdata.unUniName.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                        unitid_dataList=staffdata.unUnitID.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                      //  unitAccountId_dataList=intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                        if(unitname_dataList.size>0) {
                            for (i in 0 until unitname_dataList.size) {
                                try {
                                    visitorLog(
                                        unitid_dataList.get(i).replace(" ", "").toInt(),
                                        staffdata.wkfName + " " + staffdata.wklName,
                                        staffdata.wkMobile,
                                        staffdata.wkDesgn,
                                        staffdata.wkWrkType,
                                        staffdata.wkWorkID,
                                        unitname_dataList.get(i).replace(" ", ""),
                                        staffdata.wkEntryImg
                                    );
                                }catch (e:ArrayIndexOutOfBoundsException){

                                }
                            }
                        }
                    }else{
                        visitorLog(staffdata.unUnitID.toInt(), staffdata.wkfName + " " + staffdata.wklName, staffdata.wkMobile, staffdata.wkDesgn, staffdata.wkWrkType, staffdata.wkWorkID, staffdata.unUniName,staffdata.wkEntryImg);
                    }


                }
            }

        }

        holder.iv_edit.setOnClickListener {


            val intent= Intent(mcontext,EditStaffActivity::class.java)

            intent.putExtra("UNITID", staffdata.unUnitID)
            intent.putExtra("FIRSTNAME", staffdata.wkfName)
            intent.putExtra("LASTNAME", staffdata.wklName)
            intent.putExtra(MOBILENUMBER, staffdata.wkMobile)
            intent.putExtra("DESIGNATION",  staffdata.wkDesgn)
            intent.putExtra("WORKTYPE",  staffdata.wkWrkType)
            intent.putExtra(ConstantUtils.WORKER_ID,  staffdata.wkWorkID)
            intent.putExtra("UNITNAME",  staffdata.unUniName)
            intent.putExtra("IMAGE",  staffdata.wkEntryImg)
            intent.putExtra("DOB",staffdata.wkdob)

           // (mcontext as Activity).startActivityForResult(intent, 2)
            mcontext.startActivity(intent);
            (mcontext as Activity).finish()

        }

        holder.iv_call.setOnClickListener {

            val intent = Intent(Intent.ACTION_CALL);
            intent.data = Uri.parse("tel:" + staffdata?.wkMobile)
            mcontext.startActivity(intent)
        }

        if(dbh.fingercount(staffdata.wkWorkID)>1){
            holder.btn_biometric.visibility=View.INVISIBLE
        }else{
            if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 1") {
                holder.btn_biometric.visibility=View.INVISIBLE
            }else{
                holder.btn_biometric.visibility=View.VISIBLE

            }
            if(staffdata.wkWorkID!=null) {
                val ddc = Intent(mcontext, BackgroundSyncReceiver::class.java)
                Log.d("btn_biometric", "af " + staffdata.wkWorkID)

                ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC)
                ddc.putExtra("ID", staffdata.wkWorkID)
                mcontext.sendBroadcast(ddc);
            }
        }

    }

    private fun visitorLog(unitId:Int,personName:String,mobileNumb:String, desgn:String,
                           workerType:String,staffID:Int,unitName:String,vlEntryImage:String) {


        var memID:Int=410;
        if(BASE_URL.contains("dev",true)){
            memID=64;
        }
        else if(BASE_URL.contains("uat",true)){
            memID=64;
        }
//        var memID:Int=64;
//        if(!BASE_URL.contains("dev",true)){
//            memID=410;
//        }
        var SPPrdImg1=""
        var SPPrdImg2=""
        var SPPrdImg3=""
        var SPPrdImg4=""
        var SPPrdImg5=""
        var SPPrdImg6=""
        var SPPrdImg7=""
        var SPPrdImg8=""
        var SPPrdImg9=""
        var SPPrdImg10=""
        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID,0), staffID,
            unitName,unitId ,desgn,
            personName,LocalDb.getAssociation()!!.asAsnName,0,"",mobileNumb,
            "","","","",
            1,workerType,SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10,"",vlEntryImage,Prefs.getString(ConstantUtils.GATE_NO, ""));
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        CompositeDisposable().add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {
                            // Utils.showToast(applicationContext, intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            visitorEntryLog(globalApiObject.data.visitorLog.vlVisLgID)

                         //  if (  (globalApiObject.data.visitorLog.unUniName).contains(","))



                            getUnitLog(unitId, personName,  " " ,desgn, workerType,staffID, unitName,globalApiObject.data.visitorLog.vlVisLgID)

//                            val ddc  =  Intent(mcontext, BackgroundSyncReceiver::class.java)
//                            ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
//                            ddc.putExtra("msg", personName+" "+desgn +" is coming to your home")
//                            ddc.putExtra("mobNum", mobileNumb)
//                            ddc.putExtra("name", personName)
//                            ddc.putExtra("nr_id", AppUtils.intToString(globalApiObject.data.visitorLog.vlVisLgID))
//                            ddc.putExtra("unitname", unitName)
//                            ddc.putExtra("memType", "Owner")
//                            ddc.putExtra(UNITID,unitId.toString())
//                            ddc.putExtra(COMPANY_NAME,"Staff")
//                            ddc.putExtra(UNIT_ACCOUNT_ID,"0")
//                            ddc.putExtra("VLVisLgID",globalApiObject.data.visitorLog.vlVisLgID)
////                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
////                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
////                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
//                            mcontext.sendBroadcast(ddc);

                            Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.data.toString())
                        } else {
                            Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.toString())

                            Utils.showToast(mcontext, "Entry not Saved"+globalApiObject.toString())
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Log.d("onErrorResponse","StaffEntry "+e.toString())

                        Utils.showToast(mcontext, "Something went wrong")
//                    dismissProgress()
                    }

                    override fun noNetowork() {
                        Utils.showToast(mcontext, "No Internet")
                    }

                    override fun onShowProgress() {
//                    showProgress()
                    }

                    override fun onDismissProgress() {
//                    dismissProgress()
                    }
                }))
    }


    private fun getUnitLog(unitId:Int,personName:String,mobileNumb:String, desgn:String,
                           workerType:String,staffID:Int,unitName:String,vlVisLgID:Int) {

        RetrofitClinet.instance
            .getUnitListbyUnitId("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", unitId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitlistbyUnitID>() {

                override fun onSuccessResponse(UnitList: UnitlistbyUnitID) {

                    if (UnitList.success == true) {

                        val ddc  =  Intent(mcontext, BackgroundSyncReceiver::class.java)
                        ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
                        ddc.putExtra("msg", personName+" "+desgn +" is coming to your home")
                        ddc.putExtra("mobNum", mobileNumb)
                        ddc.putExtra("name", personName)
                        ddc.putExtra("nr_id", vlVisLgID.toString())
                        ddc.putExtra("unitname", unitName)
                        ddc.putExtra("memType", "Owner")
                        ddc.putExtra(UNITID,unitId.toString())
                        ddc.putExtra(COMPANY_NAME,"Staff")
                        ddc.putExtra(UNIT_ACCOUNT_ID,UnitList.data.unit.acAccntID.toString())
                        ddc.putExtra("VLVisLgID",vlVisLgID)
//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                        mcontext.sendBroadcast(ddc);


                    } else {
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd", e.message);


                }

                override fun noNetowork() {

                }
            })

    }

    private fun visitorEntryLog( visitorLogID: Int) {
//        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        val currentDate = sdf.format(Date())
//        System.out.println(" C DATE is  "+currentDate)

        val req = VisitorEntryReq(DateTimeUtils.getCurrentTimeLocal(), LocalDb.getStaffList()[0].wkWorkID, visitorLogID)
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        CompositeDisposable().add(RetrofitClinet.instance.visitorEntryCall(OYE247TOKEN,req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                override fun onSuccessResponse(globalApiObject: VisitorExitResp) {
                    if (globalApiObject.success == true) {
//                        Log.d("VisitorEntryReq","StaffEntry "+globalApiObject.data.toString())
                        //(mcontext as Activity).finish()
                        val d = Intent(mcontext, Dashboard::class.java)
                        d.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        mcontext.startActivity(d)
                        (mcontext as Activity).finish()
                    } else {
                        Utils.showToast(mcontext, globalApiObject.apiVersion)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Utils.showToast(mcontext, "Something went wrong")
//                    dismissProgress()
                }

                override fun noNetowork() {
                    Utils.showToast(mcontext, "No Internet")
                }

                override fun onShowProgress() {
//                    showProgress()
                }

                override fun onDismissProgress() {
//                    dismissProgress()
                }
            }))
    }


    override fun getItemCount(): Int {
        // return items.size
        return searchList!!.size

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_staff: TextView
        val iv_staff: ImageView
        val lv_staff: LinearLayout
        val tv_worktype: TextView
        val btn_biometric: Button
        val btn_makeentry: Button
        val iv_call: ImageView
        val iv_edit:ImageView

        init {
            tv_staff = view.findViewById(R.id.tv_staff)
            iv_staff = view.findViewById(R.id.iv_staff)
            lv_staff = view.findViewById(R.id.lv_staff)
            tv_worktype = view.findViewById(R.id.tv_worktype)
            btn_biometric = view.findViewById(R.id.btn_biometric)
            btn_makeentry = view.findViewById(R.id.btn_makeentry)
            iv_call = view.findViewById(R.id.iv_call)
            iv_edit=view.findViewById(R.id.iv_edit)


        }

    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchList = results?.values as ArrayList<WorkerDetails>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    searchList = items
                } else {
                    val filteredList = ArrayList<WorkerDetails>()
                    for (row in items) {
                        // if (row.wkfName!!.toLowerCase().contains(charString.toLowerCase()) || row.age!!.contains(charSequence)) {
                        if (row.wkfName!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    searchList = filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = searchList
                return filterResults
            }
        }

    }

}

