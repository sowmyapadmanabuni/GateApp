package com.oyespace.guards.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.oyespace.guards.BGService
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.activity.ImgView
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.VisitorExitReq
import com.oyespace.guards.pojo.VisitorExitResp
import com.oyespace.guards.utils.DateTimeUtils.*
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import com.oyespace.guards.constants.PrefKeys.BG_NOTIFICATION_ON
import com.oyespace.guards.pojo.VisitorEntryLog
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import android.content.DialogInterface
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.models.VisitorLog
import io.realm.Realm
import io.realm.kotlin.isValid
import kotlinx.android.synthetic.main.activity_emergency_response.view.*
import kotlinx.android.synthetic.main.activity_img_view.*


class VistorEntryListAdapter(private var listVistor: ArrayList<VisitorLog>, private val mcontext: Context) :

    RecyclerView.Adapter<VistorEntryListAdapter.MenuHolder>() {

    private val mInflater: LayoutInflater
    var number:String?=null
    var mobnumber:String?=null

    init {
        mInflater = LayoutInflater.from(mcontext)
    }
    //var mTTS: TextToSpeech?=null

    internal var animBlink: Animation= AnimationUtils.loadAnimation(mcontext, R.anim.animation_blink)
    // animBlink.setAnimationListener(this)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = mInflater.inflate(R.layout.layout_dashboard_adapter_row, parent, false) as ViewGroup

//        mTTS = TextToSpeech(mcontext, TextToSpeech.OnInitListener { status ->
//            if (status != TextToSpeech.ERROR){
//                //if there is no error then set language
//                mTTS!!.language = Locale.getDefault()
//            }
//        })

        return MenuHolder(mainGroup)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val orderData = listVistor.get(position)
        Log.e("orderData",""+orderData);
        if(orderData != null && orderData.isValid()) {

            val vistordate = orderData?.asAssnID
            val lgid = orderData.vlVisLgID;
            holder.apartmentNamee.text = orderData?.unUniName
            holder.entryTime.text = formatDateHM(orderData?.vlEntryT) + " "
            Log.d("ddd", formatDateHM(orderData?.vlEntryT))
            holder.entrydate.text = formatDateDMY(orderData?.vldCreated)
            if (orderData?.vlExitT.equals("0001-01-01T00:00:00", true)) {
                holder.exitTime.text = ""
                holder.exitdate.text = ""
                holder.btn_makeexit.visibility = View.VISIBLE
                Log.d(
                    "dddh",
                    " u " + (orderData?.vlVisType.equals(DELIVERY)) + " " + deliveryTimeUp(
                        orderData?.vlEntryT,
                        getCurrentTimeLocal(),
                        1
                    )
                )

                Log.v("TTTTIMEEE", orderData?.vlEntryT + "...." + getCurrentTimeLocal())

                if (orderData?.vlVisType.equals(DELIVERY) && deliveryTimeUp(
                        orderData?.vlEntryT,
                        getCurrentTimeLocal(),
                        1
                    )
                ) {
                    holder.ll_card.setBackgroundColor(Color.parseColor("#ff0000"))
                    holder.ll_card.startAnimation(animBlink)

                    refresh(1000)


                    // mTTS!!.speak("Overstaying "+orderData.vlfName+orderData.vllName, TextToSpeech.QUEUE_FLUSH, null)

//                if (!Prefs.getBoolean(BG_NOTIFICATION_ON, false)) {
//                    mcontext.startService(Intent(mcontext, BGService::class.java))
//                }

                } else {
                    holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
                    holder.ll_card.animation = null

                }

            } else {
                holder.exitTime.text = formatDateHM(orderData?.vlExitT)
                holder.exitdate.text = formatDateDMY(orderData?.vldUpdated)
                holder.btn_makeexit.visibility = View.INVISIBLE
                holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.ll_card.animation = null

            }

            holder.serviceProvider.text = orderData.vlComName + ", Visitors: " + orderData.vlVisCnt
            holder.visitorName.text = orderData?.vlfName
            holder.btn_makeexit.setOnClickListener {
                var realm = Realm.getDefaultInstance();

                //Log.e("VISITOR",""+visitor);
                makeExitCall(lgid);
                val visitor = realm.where(VisitorLog::class.java).equalTo("vlMobile", listVistor.get(position).vlMobile).findFirst()
                realm.beginTransaction()
                visitor!!.deleteFromRealm()
                realm.commitTransaction()

                //    .findFirst();


//                realm.executeTransaction { _ ->
//                    //visitor!!.vlExitT = DateTimeUtils.getCurrentTimeLocal();
//
//
//                    //realm.beginTransaction();
//                    visitor!!.deleteFromRealm()
//                    //realm.commitTransaction();
//                }
//                realm.close()



                Log.e("UPDATED", "" + LocalDb.getVisitorEnteredLog());

                //listVistor.get(position).vlExitT=DateTimeUtils.getCurrentTimeLocal();
                listVistor.removeAt(position)


                //  listVistor.removeAt(position)
//            listVistor.remove(listVistor.get(position))
                notifyDataSetChanged();
//            val smsIntent = Intent(ConstantUtils.SYNC)
//            smsIntent.putExtra("message", VISITOR_ENTRY_SYNC)
//            LocalBroadcastManager.getInstance(mcontext).sendBroadcast(smsIntent)

            }

            if (orderData.vlMobile.length > 5) {
                holder.iv_call.setVisibility(View.VISIBLE);
            } else {
                holder.iv_call.setVisibility(View.INVISIBLE);
            }
            try {


                number = orderData.vlMobile.substring(3)


            } catch (e: StringIndexOutOfBoundsException) {
            }
            // Log.v("Image URL",IMAGE_BASE_URL+"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+number+".jpg")

//        ImageHelper.loadImage(mcontext, IMAGE_BASE_URL+"Images/PERSONAssociation"+ASSOCIATIONID+"NONREGULAR"+orderData?.vlVisLgID+".jpg", holder.iv_user)
            if (orderData?.vlVisType.equals("STAFF", true)) {
//            Picasso.with(mcontext)
//                .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+ Prefs.getInt(ASSOCIATION_ID,0)+"STAFF"+orderData?.reRgVisID+".jpg")
//                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(holder.iv_user)

                if (orderData?.vlEntryImg.equals("")) {
                    Picasso.with(mcontext)
                        .load(IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + orderData?.reRgVisID + ".jpg")
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(holder.iv_user)
                } else {


                    Picasso.with(mcontext)
                        .load(IMAGE_BASE_URL + "Images/" + orderData?.vlEntryImg)
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(holder.iv_user)
                }

            } else {
//            Picasso.with(mcontext)
//                .load(IMAGE_BASE_URL+"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+number+".jpg")
//                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(holder.iv_user)

                if (orderData?.vlEntryImg.equals("")) {
                    Picasso.with(mcontext)
                        .load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + number + ".jpg")
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(holder.iv_user)
                } else {
                    Picasso.with(mcontext)
                        .load(IMAGE_BASE_URL + "Images/" + orderData?.vlEntryImg)
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(holder.iv_user)

                }
            }

            holder.iv_user.setOnClickListener {

                try {

                    mobnumber = orderData.vlMobile.substring(3)


                } catch (e: StringIndexOutOfBoundsException) {
                }


                val alertadd = AlertDialog.Builder(mcontext)
                val factory = LayoutInflater.from(mcontext)
                val view = factory.inflate(R.layout.dialog_big_image, null)
                var dialog_imageview: ImageView? = null
                dialog_imageview = view.findViewById(R.id.dialog_imageview)


                //  if (orderData.vlVisType.equals("STAFF", true)) {

                // alertadd.setNeutralButton("Here!", DialogInterface.OnClickListener { dlg, sumthin -> })


                if (orderData.vlVisType.equals("STAFF", true)) {

                    if (orderData?.vlEntryImg.equals("")) {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + orderData?.reRgVisID + ".jpg")
                            .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    } else {


                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/" + orderData?.vlEntryImg)
                            .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    }


//                val intent1 = Intent(mcontext, ImgView::class.java)
//                intent1.putExtra(
//                    "URL_IMAGE",
//                    IMAGE_BASE_URL + "Images/PERSONAssociation" + Prefs.getInt(ASSOCIATION_ID,0) + "STAFF" + orderData?.reRgVisID + ".jpg"
//                )
//                mcontext.startActivity(intent1)

                } else {

                    if (orderData?.vlEntryImg.equals("")) {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + number + ".jpg")
                            .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    } else {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/" + orderData?.vlEntryImg)
                            .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                            .into(dialog_imageview)

                    }


                }

                alertadd.setView(view)
                alertadd.show()
            }
            holder.iv_call.setOnClickListener {

                val intent = Intent(Intent.ACTION_CALL);
                intent.data = Uri.parse("tel:" + orderData?.vlMobile)
                mcontext.startActivity(intent)
            }
            holder.expanded_view.visibility = View.GONE

            holder.lyt_text.setOnClickListener {

                if (holder.expanded_view.visibility == View.GONE) {
                    holder.expanded_view.visibility = View.VISIBLE
                } else {
                    holder.expanded_view.visibility = View.GONE
                }
            }

            // holder.tv_purpose.text=orderData?.vlMobile
        }

    }

    private fun makeExitCall(visitorLogID: Int) {

        val req = VisitorExitReq(getCurrentTimeLocal(), 0, visitorLogID,Prefs.getString(GATE_NO, ""))
        CompositeDisposable().add(
            RetrofitClinet.instance.visitorExitCall("7470AD35-D51C-42AC-BC21-F45685805BBE",req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorExitResp) {

                        Log.e("globalApiObject",""+globalApiObject);

                        if (globalApiObject.success == true) {
//                            Utils.showToast(mcontext, "Success")
                            val intentAction1 = Intent(mcontext, BackgroundSyncReceiver::class.java)
                            intentAction1.putExtra(ConstantUtils.BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
                            mcontext.sendBroadcast(intentAction1)

                        } else {
                            Utils.showToast(mcontext, "Failed")
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Utils.showToast(mcontext, "Error visitor exit")
                    }

                    override fun noNetowork() {
                        Utils.showToast(mcontext, "no_internet visitor exit")
                    }

                    override fun onShowProgress() {
//                        showProgress()
                    }

                    override fun onDismissProgress() {
//                        dismissProgress()
                    }
                }))

        val filteredList = java.util.ArrayList<VisitorLog>()

        //looping through existing elements
        for (s in listVistor) {
            //if the existing elements contains the search input
            Log.d("button_done ","visitorlogbydate "+s.vlExitT+" "+s.vlExitT.equals("0001-01-01T00:00:00",true)+" ")

            if (s.vlExitT.equals("0001-01-01T00:00:00",true)) {
                Log.d("vlExitT ","visitorlogbydate "+s.vlExitT+" "+s.vlfName+" ")
                filteredList.add(s)

                //adding the element to filtered list
            } else {

            }
        }
        // LocalDb.saveEnteredVisitorLog(filteredList)

//        Collections.sort(listVistor, object : Comparator<Visitorlogbydate> {
//            override  fun compare(lhs: Visitorlogbydate, rhs: Visitorlogbydate): Int {
//                return rhs.vlExitT.compareTo(lhs.vlExitT)
//            }
//        })
        listVistor= RandomUtils.getSortedVisitorLog( listVistor );

        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return listVistor?.size ?: 0
    }

    private fun refresh( milliseconds:Long) {
        val handler: Handler =  Handler()
        val runnable:Runnable = Runnable(){
            run(){
                content()
            }
        }

        handler.postDelayed(runnable, milliseconds);

    }

    private fun content() {
        refresh(1000)
    }

    inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val entryTime: TextView
        val exitTime: TextView
        val visitorName: TextView
        val serviceProvider: TextView
        val apartmentNamee: TextView
        val btn_makeexit:Button
        val iv_call: ImageButton
        val iv_user: ImageView
        val entrydate: TextView
        val exitdate: TextView
        val ll_card:LinearLayout
        val expanded_view: LinearLayout
        val lyt_text: LinearLayout
        val iv_map:ImageView


        init {
            entryTime = view.findViewById(R.id.tv_entrytime)
            exitTime = view.findViewById(R.id.tv_exittime)
            visitorName = view.findViewById(R.id.tv_name)

            if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 1")) {
                visitorName!!.setTextSize(10 * mcontext.getResources().getDisplayMetrics().density);
            }

            serviceProvider = view.findViewById(R.id.tv_serviceprovider)
            apartmentNamee = view.findViewById(R.id.tv_unitname)
            btn_makeexit=view.findViewById(R.id.btn_makeexit)
            iv_call=view.findViewById(R.id.iv_call)
            iv_user=view.findViewById(R.id.iv_user)

            entrydate = view.findViewById(R.id.tv_entrydate)
            exitdate = view.findViewById(R.id.tv_exitdate)
            ll_card=view.findViewById(R.id.ll_card)
            expanded_view = view.findViewById(R.id.expanded_view)
            lyt_text=view.findViewById(R.id.lyt_text)
            iv_map=view.findViewById(R.id.iv_map);


        }

    }
}