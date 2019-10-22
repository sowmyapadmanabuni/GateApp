package com.oyespace.guards.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.models.ExitVisitorLog
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils.DELIVERY
import com.oyespace.guards.utils.ConstantUtils.IMAGE_BASE_URL
import com.oyespace.guards.utils.DateTimeUtils
import com.oyespace.guards.utils.DateTimeUtils.*
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import java.util.*

class VistorOutListAdapter(
    private var listVistor: ArrayList<ExitVisitorLog>,
    private val mcontext: Context
) : RecyclerView.Adapter<VistorOutListAdapter.MenuHolder>() {

    private var refreshImages: Boolean = true
    private var searchList: ArrayList<ExitVisitorLog>? = null

    private val mInflater: LayoutInflater
    var number: String? = null
    var mobnumber: String? = null

    init {
        this.searchList = listVistor
        mInflater = LayoutInflater.from(mcontext)


    }
    //  var mTTS: TextToSpeech?=null

    internal var animBlink: Animation =
        AnimationUtils.loadAnimation(mcontext, R.anim.animation_blink)

    // animBlink.setAnimationListener(this)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup =
            mInflater.inflate(R.layout.layout_dashboard_adapter_row, parent, false) as ViewGroup

//       mTTS = TextToSpeech(mcontext, TextToSpeech.OnInitListener { status ->
//           if (status != TextToSpeech.ERROR){
//               //if there is no error then set language
//               mTTS!!.language = Locale.getDefault()
//           }
//       })
        return MenuHolder(mainGroup)
    }

    override fun onBindViewHolder(holder: MenuHolder, p: Int) {

        val position = holder.adapterPosition

        val orderData = searchList!!.get(position)
        val vistordate = orderData.asAssnID
        var visitor: ExitVisitorLog? = null
        try {
            visitor = searchList!!.get(position)
        } catch (e: Exception) {
            return
        }
        holder.apartmentNamee.text = orderData.unUniName
        holder.entryTime.text = formatDateHM(orderData.vlEntryT) + " "
        holder.entrydate.text = formatDateDMY(orderData.vldCreated)

        if (visitor.vlVenImg.isEmpty() and visitor.vlVoiceNote.isEmpty() and visitor.vlCmnts.isEmpty()) {
            holder.iv_attachment.visibility = View.GONE
            holder.expanded_view.visibility = View.GONE
        } else {
            holder.iv_attachment.visibility = View.VISIBLE
            holder.lyt_text.setOnClickListener {

                if (holder.expanded_view.visibility == View.GONE) {
                    holder.expanded_view.visibility = View.VISIBLE
                } else {
                    holder.expanded_view.visibility = View.GONE
                }
            }
            if (visitor.vlVoiceNote.isEmpty()) {
                holder.iv_play.visibility = View.GONE
            } else {
                holder.iv_play.visibility = View.VISIBLE
                holder.iv_play.setOnClickListener {
                    AppUtils.playAudio(mcontext, visitor.vlVoiceNote)
                }
            }

            if (visitor.vlCmnts.isEmpty()) {
                holder.tv_comments.visibility = View.GONE
            } else {
                holder.tv_comments.visibility = View.VISIBLE
                holder.tv_comments.text = visitor.vlCmnts
            }

            if (visitor.vlVenImg.isEmpty()) {
                holder.rv_images.visibility = View.GONE
            } else {

                holder.rv_images.visibility = View.VISIBLE
                if (visitor.vlVenImg.contains(",")) {
                    var imageList: Array<String>
                    imageList = visitor.vlVenImg.split(",".toRegex())
                        .dropLastWhile({ it.isEmpty() }).toTypedArray()


                    holder.rv_images.setHasFixedSize(true)
                    holder.rv_images.adapter = HorizontalImagesAdapter(mcontext, imageList)

                }
            }

        }

        if (orderData.vlExitT.equals("0001-01-01T00:00:00", true)) {
            holder.exitTime.text = ""
            holder.exitdate.text = ""
            holder.btn_makeexit.visibility = View.VISIBLE

            if (orderData.vlVisType.equals(DELIVERY) && deliveryTimeUp(
                    orderData.vlEntryT,
                    getCurrentTimeLocal(),
                    1
                )
            ) {
                holder.ll_card.setBackgroundColor(Color.parseColor("#ff0000"))
                holder.ll_card.startAnimation(animBlink)
                // mTTS!!.speak("Overstaying "+orderData.vlfName+orderData.vllName, TextToSpeech.QUEUE_FLUSH, null)

            } else {
                holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.ll_card.animation = null
            }

        } else {
            // holder.exitTime.text = formatDateHM(orderData?.vlExitT)

            holder.exitTime.text = formatDateHM(orderData.vlExitT)
            Log.v(
                "Timme",
                formatDateHM(orderData.vlExitT) + "..." + (orderData.vlExitT).substring(11, 19)
            )
            holder.exitdate.text = formatDateDMY(orderData.vldUpdated)
            holder.btn_makeexit.visibility = View.INVISIBLE
            holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.ll_card.animation = null

        }

        holder.serviceProvider.text = orderData.vlComName + ", Visitors: " + orderData.vlVisCnt
        holder.visitorName.text = orderData.vlfName
        holder.btn_makeexit.setOnClickListener {
            listVistor.get(position).vlExitT = DateTimeUtils.getCurrentTimeLocal()

//            makeExitCall(orderData.vlVisLgID)

            notifyDataSetChanged()

        }

        if (orderData.vlMobile.length > 5) {
            holder.iv_call.visibility = View.VISIBLE
        } else {
            holder.iv_call.visibility = View.INVISIBLE
        }
        try {
            number = orderData.vlMobile.substring(3)
            //  number = orderData.vlMobile
        } catch (e: StringIndexOutOfBoundsException) {
        }

        var imgPath = IMAGE_BASE_URL + "Images/" + orderData.vlEntryImg

        if (orderData.vlVisType.equals("STAFF", true)) {

            if (orderData.vlEntryImg.isEmpty()) {
                imgPath = IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + orderData.reRgVisID + ".jpg"
            }

        }

        if (refreshImages) {
            Picasso.with(mcontext).invalidate(imgPath)
        }

        Picasso.with(mcontext)
            .load(imgPath)
            .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
            .into(holder.iv_user)

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


            if (orderData.vlVisType.equals("STAFF", true)) {

                if (orderData.vlEntryImg.equals("")) {
                    Picasso.with(mcontext)
                        .load(IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + orderData.reRgVisID + ".jpg")
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                        .into(dialog_imageview)
                } else {


                    Picasso.with(mcontext)
                        .load(IMAGE_BASE_URL + "Images/" + orderData.vlEntryImg)
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                        .into(dialog_imageview)
                }


            } else {


                Picasso.with(mcontext)
                    .load(IMAGE_BASE_URL + "Images/" + orderData.vlEntryImg)
                    .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                    .into(dialog_imageview)


            }

            alertadd.setView(view)
            alertadd.show()

        }

        holder.iv_call.setOnClickListener {

            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + orderData.vlMobile)
            mcontext.startActivity(intent)
        }

        holder.expanded_view.visibility = View.GONE

    }

    fun setVisitorLog(visitorLog: ArrayList<ExitVisitorLog>?) {
        refreshImages = true
        if (visitorLog == null) {
            this.searchList = listVistor
        } else {
            this.searchList = visitorLog
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return searchList?.size ?: 0
    }

    inner class MenuHolder(private val view: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val entryTime: TextView
        val exitTime: TextView
        val visitorName: TextView
        val serviceProvider: TextView
        val apartmentNamee: TextView
        val btn_makeexit: Button
        val iv_call: ImageButton
        val iv_user: ImageView
        val entrydate: TextView
        val exitdate: TextView
        val ll_card: LinearLayout
        val rv_images: RecyclerView
        val expanded_view: ConstraintLayout
        val tv_comments: TextView
        val lyt_text: LinearLayout
        val iv_attachment: ImageView
        val iv_play: ImageView

        init {
            entryTime = view.findViewById(R.id.tv_entrytime)
            exitTime = view.findViewById(R.id.tv_exittime)
            visitorName = view.findViewById(R.id.tv_name)

            if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
                visitorName.textSize = 10 * mcontext.resources.displayMetrics.density
            }

            serviceProvider = view.findViewById(R.id.tv_serviceprovider)
            apartmentNamee = view.findViewById(R.id.tv_unitname)
            btn_makeexit = view.findViewById(R.id.btn_makeexit)
            iv_call = view.findViewById(R.id.iv_call)
            iv_user = view.findViewById(R.id.iv_user)
            expanded_view = view.findViewById(R.id.expanded_view)
            entrydate = view.findViewById(R.id.tv_entrydate)
            exitdate = view.findViewById(R.id.tv_exitdate)
            ll_card = view.findViewById(R.id.ll_card)
            lyt_text = view.findViewById(R.id.lyt_text)
            iv_attachment = view.findViewById(R.id.iv_attachment)
            rv_images = view.findViewById(R.id.rv_images)
            tv_comments = view.findViewById(R.id.tv_comments)
            iv_play = view.findViewById(R.id.iv_play)

        }

    }

    fun applySearch(search: String, forceImageRefresh: Boolean = false) {

        if (forceImageRefresh) {
            refreshImages = forceImageRefresh
        } else {
            refreshImages = search.isEmpty()
        }


        searchList = VisitorLogRepo.search_OUT_Visitors(search)

        notifyDataSetChanged()

    }

}