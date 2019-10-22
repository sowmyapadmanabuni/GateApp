package com.oyespace.guards.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.models.NotificationSyncModel
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.*
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.TimerUtil
import com.squareup.picasso.Picasso
import java.util.*


class VistorEntryListAdapter(
    private var visitorList: ArrayList<VisitorLog>,
    private val mcontext: Context
) : RecyclerView.Adapter<VistorEntryListAdapter.MenuHolder>() {

    var firebasedataMap: HashMap<String, NotificationSyncModel>
    private var searchList: ArrayList<VisitorLog>? = null
    var number: String? = null
    var searchString: String = ""
    var mobnumber: String? = null
    lateinit var mp: MediaPlayer

    init {
        this.searchList = visitorList
        firebasedataMap = hashMapOf()
    }

    internal var animBlink: Animation =
        AnimationUtils.loadAnimation(mcontext, R.anim.animation_blink)

    // animBlink.setAnimationListener(this)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = LayoutInflater.from(mcontext).inflate(R.layout.layout_dashboard_adapter_row, parent, false) as ViewGroup
        return MenuHolder(mainGroup)
    }

    override fun onBindViewHolder(holder: MenuHolder, p: Int) {

        holder.setIsRecyclable(false)

        val position = holder.adapterPosition

        var visitor: VisitorLog? = null
        try {
            visitor = searchList!!.get(position)
        } catch (e: Exception) {
            return
        }

        if (visitor != null && visitor.isValid) {

            val vistordate = visitor.asAssnID
            holder.apartmentNamee.text = visitor.unUniName
            holder.entryTime.text = formatDateHM(visitor.vlEntryT) + " "
            holder.entrydate.text = formatDateDMY(visitor.vldCreated)
            if (visitor.vlExitT.equals("0001-01-01T00:00:00", true)) {
                holder.exitTime.text = ""
                holder.btn_makeexit.visibility = View.VISIBLE

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

                if (visitor.vlVenImg.contains(",")) {
                    var imageList: Array<String>
                    imageList = visitor.vlVenImg.split(",".toRegex())
                        .dropLastWhile({ it.isEmpty() }).toTypedArray()


                    holder.rv_images.setHasFixedSize(true)
                    val linearLayoutManager =
                        LinearLayoutManager(
                            mcontext,
                            LinearLayoutManager.HORIZONTAL, true
                        )
                    holder.rv_images.layoutManager = linearLayoutManager


                    val adapter = HorizontalImagesAdapter(mcontext, imageList)
                    holder.rv_images.adapter = adapter

                }

                val noofUnits = VisitorLogRepo.getUnitCountForVisitor(visitor.vlMobile)


                if (visitor.vlVisType.equals(DELIVERY)) {

                    val firebaseObject = firebasedataMap.get(visitor.vlVisLgID.toString())
                    holder.btn_makeexit.visibility = View.INVISIBLE

                    val entryTime = visitor.vlEntryT

                    val msLeft = msLeft(entryTime, MAX_DELIVERY_ALLOWED_SEC * noofUnits)

                    if (msLeft < 0) {
                        holder.ll_card.setBackgroundColor(Color.parseColor("#ff0000"))
                        holder.ll_card.startAnimation(animBlink)
                        holder.btn_makeexit.visibility = View.VISIBLE
                    } else {

                        val fbColor = firebaseObject?.buttonColor
                        try {
                            holder.ll_card.setBackgroundColor(Color.parseColor(fbColor))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        when (fbColor) {
                            "#ffb81a" -> {// pending
                                holder.btn_makeexit.visibility = View.INVISIBLE
                                TimerUtil(24 * 60 * 60 * 1000, object : TimerUtil.OnFinishCallback {
                                    override fun onFinish() {

                                        holder.btn_makeexit.visibility = View.GONE
                                        // TODO don't exit, just remove entry
                                        exitVisitor(visitor, position)

                                    }
                                }).start()
                            }
                            "#00FF00" -> {// accepted by resident
                                holder.btn_makeexit.visibility = View.VISIBLE
                            }
                            "#FF0000" -> {// rejected by resident
                                holder.btn_makeexit.visibility = View.VISIBLE
                            }
                        }

                        // TODO start this when accepted
                        TimerUtil(msLeft, object : TimerUtil.OnFinishCallback {
                            override fun onFinish() {
                                holder.ll_card.setBackgroundColor(Color.parseColor("#ff0000"))
                                holder.ll_card.startAnimation(animBlink)
                                Handler().post {
                                    searchList = VisitorLogRepo.get_IN_VisitorLog(false)
                                    if (searchList != null) {
                                        visitorList = searchList!!
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }).start()


                    }

                }


            } else {
                holder.exitTime.text = formatDateHM(visitor.vlExitT)
                holder.exitdate.text = formatDateDMY(visitor.vldUpdated)
                holder.btn_makeexit.visibility = View.INVISIBLE
                holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.ll_card.animation = null

            }

            holder.serviceProvider.text = visitor.vlComName + ", Visitors: " + visitor.vlVisCnt
            holder.visitorName.text = visitor.vlfName

            holder.btn_makeexit.setOnClickListener {
                holder.btn_makeexit.visibility = View.GONE
                exitVisitor(visitor, position)
            }

            if (visitor.vlMobile.length > 5) {
                holder.iv_call.visibility = View.VISIBLE
            } else {
                holder.iv_call.visibility = View.INVISIBLE
            }
            try {


                number = visitor.vlMobile.substring(3)


            } catch (e: StringIndexOutOfBoundsException) {
            }


            var imgPath = IMAGE_BASE_URL + "Images/" + visitor.vlEntryImg

            if (visitor.vlVisType.equals("STAFF", true)) {
                if (visitor.vlEntryImg.isEmpty()) {
                    imgPath = IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + visitor.reRgVisID + ".jpg"
                }
            } else {
                if (visitor.vlEntryImg.isEmpty()) {
                    imgPath = IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + number + ".jpg"
                }
            }

            Glide.with(mcontext)
                .load(imgPath)
                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false)
                .into(holder.iv_user)

            holder.iv_user.setOnClickListener {

                val alertadd = AlertDialog.Builder(mcontext)
                val factory = LayoutInflater.from(mcontext)
                val view = factory.inflate(R.layout.dialog_big_image, null)
                var dialog_imageview: ImageView? = null
                dialog_imageview = view.findViewById(R.id.dialog_imageview)


                //  if (orderData.vlVisType.equals("STAFF", true)) {

                // alertadd.setNeutralButton("Here!", DialogInterface.OnClickListener { dlg, sumthin -> })


                if (visitor.vlVisType.equals("STAFF", true)) {

                    if (visitor.vlEntryImg.equals("")) {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + visitor.reRgVisID + ".jpg")
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    } else {


                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/" + visitor.vlEntryImg)
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    }

                } else {

                    if (visitor.vlEntryImg.equals("")) {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + number + ".jpg")
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    } else {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/" + visitor.vlEntryImg)
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)

                    }


                }

                alertadd.setView(view)
                alertadd.show()
            }


            if (visitor.vlVoiceNote.isEmpty()) {
                holder.iv_play.visibility = View.GONE
            } else {
                holder.iv_play.visibility = View.VISIBLE
            }
            holder.iv_play.setOnClickListener {
                AppUtils.playAudio(mcontext, visitor.vlVoiceNote)
            }

            holder.tv_comments.text = visitor.vlCmnts

            holder.iv_call.setOnClickListener {

                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + visitor.vlMobile)
                mcontext.startActivity(intent)
            }
            holder.expanded_view.visibility = View.GONE

        }


    }

    fun setFirebaseDataHashmap(map: HashMap<String, NotificationSyncModel>) {
        this.firebasedataMap = map
        notifyDataSetChanged()
    }

    private fun exitVisitor(orderData: VisitorLog, position: Int) {

        try {
            val lgid = orderData.vlVisLgID
            VisitorLogRepo.exitVisitor(mcontext, lgid)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            searchList?.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun getItemCount(): Int {
        return searchList?.size ?: 0
    }

    fun setVisitorLog(visitorLog: ArrayList<VisitorLog>?) {
        if (visitorLog == null) {
            this.searchList = visitorList
        } else {
            this.searchList = visitorLog
        }
        notifyDataSetChanged()
    }

    inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {
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
        val expanded_view: ConstraintLayout
        val lyt_text: LinearLayout
        //        val iv_map: ImageView
        val rv_images: RecyclerView
        val iv_play: ImageView
        val tv_comments: TextView
        val iv_attachment: ImageView


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

            entrydate = view.findViewById(R.id.tv_entrydate)
            exitdate = view.findViewById(R.id.tv_exitdate)
            ll_card = view.findViewById(R.id.ll_card)
            expanded_view = view.findViewById(R.id.expanded_view)
            lyt_text = view.findViewById(R.id.lyt_text)
//            iv_map = view.findViewById(R.id.iv_map)
            rv_images = view.findViewById(R.id.rv_images)
            iv_play = view.findViewById(R.id.iv_play)
            tv_comments = view.findViewById(R.id.tv_comments)
            iv_attachment = view.findViewById(R.id.iv_attachment)


        }

    }

    fun applySearch(search: String) {

        this.searchString = search
        searchList = VisitorLogRepo.search_IN_Visitors(search)

        notifyDataSetChanged()

    }


}