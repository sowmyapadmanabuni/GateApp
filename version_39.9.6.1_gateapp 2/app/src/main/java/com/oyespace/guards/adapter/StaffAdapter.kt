package com.oyespace.guards.adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.activity.Biometric
import com.oyespace.guards.activity.EditStaffActivity
import com.oyespace.guards.activity.MobileNumberforEntryScreen
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.models.Worker
import com.oyespace.guards.repo.StaffRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import java.util.*


class StaffAdapter(val items: ArrayList<Worker>, val mcontext: Context) :
    RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    private var searchList: ArrayList<Worker>? = null
    var searchString: String = ""
    var encodedImage: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): StaffViewHolder {
        return StaffViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.layout_staff_adapter_row, parent, false))
    }

    init {
        this.searchList = items
    }

    private var progressDialog: ProgressDialog? = null

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {

        val staffdata = searchList!![position]

        if (!staffdata.isValid) {
            return
        }

        holder.tv_staff.text = staffdata.wkfName + " " + staffdata.wklName
        holder.tv_worktype.text = staffdata.wkDesgn




        if (staffdata.wkMobile.isNotEmpty()) {
            holder.iv_call.visibility = View.VISIBLE
        } else {
            holder.iv_call.visibility = View.INVISIBLE
        }
        holder.lv_staff.setOnClickListener {


            val alertadd = androidx.appcompat.app.AlertDialog.Builder(mcontext)
            val factory = LayoutInflater.from(mcontext)
            val view = factory.inflate(R.layout.dialog_big_image, null)
            var dialog_imageview: ImageView? = null
            dialog_imageview = view.findViewById(R.id.dialog_imageview)

            dialog_imageview.setBackground(holder.iv_staff!!.getDrawable())

//            Picasso.with(mcontext)
//                .load(IMAGE_BASE_URL + "Images/" + staffdata.wkEntryImg)
//                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(dialog_imageview)

            alertadd.setView(view)
            alertadd.show()

        }




//    try {

        if(staffdata.wkEntryImg.contains("PERSON")) {
            val url = IMAGE_BASE_URL + "Images/" + staffdata.wkEntryImg

         GetImageFromUrl(holder.iv_staff).execute(url);

        }else
            if((staffdata.wkEntryImg.equals("")) ) {
                //|| (!staffdata.wkEntryImg.contains("PERSON"))
            holder.iv_staff.setBackgroundResource(R.drawable.user_icon_black);
        } else if(staffdata.wkEntryImg.contains(".")){
            val v = staffdata.wkEntryImg.substring(0,staffdata.wkEntryImg.indexOf("."))
                if(!v.contains("PERSON"))
                {
                    holder.iv_staff.setBackgroundResource(R.drawable.user_icon_black);
                }
        }

        else{
            val imageBytes = Base64.decode(staffdata.wkEntryImg, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.iv_staff!!.setImageBitmap(decodedImage)
        }
//        Picasso.with(mcontext)
//            .load(IMAGE_BASE_URL + "Images/" + staffdata.wkEntryImg)
//            .placeholder(R.drawable.placeholder_dark_potrait)
//            .error(R.drawable.placeholder_dark_potrait).into(holder.iv_staff)

        holder.btn_makeentry.setOnClickListener {

            holder.btn_makeentry.isEnabled = false
            holder.btn_makeentry.isClickable = false

if(staffdata.isValid){
            val d = Intent(mcontext, MobileNumberforEntryScreen::class.java)
            d.putExtra(UNITID, staffdata.unUnitID)
            d.putExtra("FIRSTNAME", staffdata.wkfName)
            d.putExtra("LASTNAME", staffdata.wklName)
            d.putExtra(MOBILENUMBER, staffdata.wkMobile)
            d.putExtra("DESIGNATION", staffdata.wkDesgn)
            d.putExtra("WORKTYPE", staffdata.wkWrkType)
            d.putExtra(ConstantUtils.WORKER_ID, staffdata.wkWorkID)
            d.putExtra(UNITNAME, staffdata.unUniName)
            d.putExtra("Image", staffdata.wkEntryImg)
            d.putExtra(COMPANY_NAME, staffdata.wkDesgn)
            d.putExtra("BIRTHDAY", staffdata.wkdob)

            mcontext.startActivity(d)
            (mcontext as Activity).finish()
}

        }

        holder.iv_edit.setOnClickListener {

                val intent = Intent(mcontext, EditStaffActivity::class.java)

                intent.putExtra(UNITID, staffdata.unUnitID)
                intent.putExtra("FIRSTNAME", staffdata.wkfName)
                intent.putExtra("LASTNAME", staffdata.wklName)
                intent.putExtra(MOBILENUMBER, staffdata.wkMobile)
                intent.putExtra("DESIGNATION", staffdata.wkDesgn)
                intent.putExtra("WORKTYPE", staffdata.wkWrkType)
                intent.putExtra(WORKER_ID, staffdata.wkWorkID)
                intent.putExtra(UNITNAME, staffdata.unUniName)
                intent.putExtra("IMAGE", staffdata.wkEntryImg)
                intent.putExtra("DOB", staffdata.wkdob)

                // (mcontext as Activity).startActivityForResult(intent, 2)
                mcontext.startActivity(intent)
                (mcontext as Activity).finish()

        }

        holder.iv_call.setOnClickListener {

            var agentNumber="AGENTNUMBER="+staffdata.wkMobile.replace("+91", "")
            var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
            TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber,mcontext)

        }

        val noofFingers = StaffRepo.getFingersForStaff(staffdata.wkWorkID)

        if (noofFingers > 1) {
            holder.btn_biometric.visibility = View.INVISIBLE
        } else {

            holder.btn_biometric.setOnClickListener {

                progressDialog = ProgressDialog(mcontext)
                progressDialog?.isIndeterminate = false
                progressDialog?.setCancelable(false)
                progressDialog?.setCanceledOnTouchOutside(true)
                progressDialog?.max = 100
                progressDialog?.show()

                val progressRunnable = Runnable { progressDialog?.cancel() }

                val pdCanceller = Handler()
                pdCanceller.postDelayed(progressRunnable, 3000)

                if(staffdata.isValid) {
                    val d = Intent(mcontext, Biometric::class.java)
                    d.putExtra(WORKER_ID, staffdata.wkWorkID)
                    d.putExtra(PERSONNAME, staffdata.wkfName + " " + staffdata.wklName)
                    d.putExtra(UNITID, staffdata.unUnitID)
                    d.putExtra(UNITNAME, staffdata.unUniName)
                    d.putExtra(FLOW_TYPE, STAFF_REGISTRATION)
                    d.putExtra(VISITOR_TYPE, "STAFF")
                    d.putExtra(COMPANY_NAME, staffdata.wkDesgn)
                    d.putExtra(COUNTRYCODE, "")
                    d.putExtra(MOBILENUMBER, staffdata.wkMobile)
                    mcontext.startActivity(d)
                }

            }



            if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 1") {
                holder.btn_biometric.visibility = View.INVISIBLE
            } else {
                holder.btn_biometric.visibility = View.VISIBLE

            }
            if (staffdata.wkWorkID != null) {
                val ddc = Intent(mcontext, BackgroundSyncReceiver::class.java)
                Log.d("btn_biometric", "af " + staffdata.wkWorkID)

                ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC)
                ddc.putExtra("ID", staffdata.wkWorkID)
                mcontext.sendBroadcast(ddc)
            }
        }

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
        val iv_edit: ImageView

        init {
            tv_staff = view.findViewById(R.id.tv_staff)
            iv_staff = view.findViewById(R.id.iv_staff)
            lv_staff = view.findViewById(R.id.lv_staff)
            tv_worktype = view.findViewById(R.id.tv_worktype)
            btn_biometric = view.findViewById(R.id.btn_biometric)
            btn_makeentry = view.findViewById(R.id.btn_makeentry)
            iv_call = view.findViewById(R.id.iv_call)
            iv_edit = view.findViewById(R.id.iv_edit)


        }

    }

    fun applySearch(search: String) {

        this.searchString = search
        searchList = StaffRepo.search_Staff(search)

        notifyDataSetChanged()

    }
}

