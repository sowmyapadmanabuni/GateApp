package com.oyespace.guards.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.*
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
import io.realm.RealmChangeListener
import io.realm.RealmResults
import java.util.*


class VisitorEntryListAdapter(
    private var visitorList: ArrayList<VisitorLog>,
    private val mcontext: Context
) : RecyclerView.Adapter<VisitorEntryListAdapter.MenuHolder>() {

    val ACCEPTED_COLOR = "#75be6f"
    val REJECTED_COLOR = "#ff0000"
    val PENDING_COLOR = "#ffb81a"
    val TIMEUP_COLOR = "#ff0000"

    private var searchList: ArrayList<VisitorLog>? = null
    var number: String? = null
    var mobnumber: String? = null

    val timerHashMap: HashMap<String, TimerUtil>
    var notificationSyncFBRef: DatabaseReference
    var fbdbAssocName: String

    init {
        this.searchList = visitorList
        timerHashMap = hashMapOf()
        fbdbAssocName = "A_${Prefs.getInt(ASSOCIATION_ID, 0)}"
        notificationSyncFBRef = FirebaseDatabase.getInstance().getReference("NotificationSync").child(fbdbAssocName)
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            searchList?.get(position)?.vlVisLgID!!
        } catch (e: java.lang.Exception) {
            return position
        }
    }

    internal var animBlink: Animation =
        AnimationUtils.loadAnimation(mcontext, R.anim.animation_blink)

    // animBlink.setAnimationListener(this)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = LayoutInflater.from(mcontext).inflate(R.layout.layout_dashboard_adapter_row, parent, false) as ViewGroup
        return MenuHolder(mainGroup)
    }

    override fun onViewAttachedToWindow(holder: MenuHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.isAnimating) {
            holder.ll_card.startAnimation(animBlink)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: MenuHolder, p: Int) {

        val position = holder.adapterPosition

        val visitor: VisitorLog?
        try {
            visitor = searchList!![position]
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

//        visitor.addChangeListener( RealmChangeListener() {
//    @Override
//    public void onChange(RealmResults<VisitorLog> results) {
//      // results and puppies point are both up to date
//      results.size(); // => 1
//      puppies.size(); // => 1
//    }
//});
        if (visitor.isValid) {
            val vlLogId = visitor.vlVisLgID.toString()
            val unitName = visitor.unUniName
            val unitID = visitor.unUnitID
            val phone = visitor.vlMobile
            Log.v("taaag", "refreshed IN list element at $position for vlID: $vlLogId unit: $unitName ($unitID)")
            holder.apartmentNamee.text = if (debug) "${unitName} ($vlLogId) ($phone)" else unitName
            holder.entryTime.text = formatDateHM(visitor.vlEntryT) + " "
            holder.entrydate.text = formatDateDMY(visitor.vldCreated)


            if (visitor.vlExitT.equals("0001-01-01T00:00:00", true)) {
                holder.exitTime.text = ""
                val fbEventListener = FBValueEventListener(visitor, holder)

                updateAttachments(visitor, holder)

                val type = visitor.vlVisType
                when {

                    type.contains(DELIVERY, true) -> notificationSyncFBRef.child(vlLogId).addValueEventListener(fbEventListener)
                    type.contains(STAFF, true) -> {
                        notificationSyncFBRef.child(vlLogId).addValueEventListener(fbEventListener)
                        holder.btn_makeexit.visibility = View.VISIBLE
                    }
                    else -> {
                        holder.btn_makeexit.visibility = View.VISIBLE
                        Log.d("taaag", "fb child listener not attached")
                    }
                }

                if (debug) {
                    holder.btn_makeexit.visibility = View.VISIBLE
                }
                holder.btn_makeexit.setOnClickListener {
                    //                    sendExitNotification(visitor)
                    holder.btn_makeexit.visibility = View.GONE
                    notificationSyncFBRef.child(vlLogId).removeEventListener(fbEventListener)
                    updateVisitorStatus(visitor, position, EXITED)
                }

            } else {
                holder.exitTime.text = formatDateHM(visitor.vlExitT)
//                holder.exitdate.text = formatDateDMY(visitor.vldUpdated)
                holder.btn_makeexit.visibility = View.INVISIBLE
                holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.ll_card.animation = null

            }

            holder.serviceProvider.text = visitor.vlComName + ", Visitors: " + visitor.vlVisCnt
            holder.visitorName.text = visitor.vlfName


            val entryImg = visitor.vlEntryImg
            var imgPath = IMAGE_BASE_URL + "Images/" + entryImg

            if (visitor.vlVisType.contains(STAFF, true)) {
                if (entryImg.isEmpty()) {
                    imgPath = IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + visitor.reRgVisID + ".jpg"
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


                if (visitor.vlVisType.contains(STAFF, true)) {

                    var img = IMAGE_BASE_URL + "Images/" + visitor.vlEntryImg

                    if (visitor.vlEntryImg.isEmpty()) {

                        img = IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + visitor.reRgVisID + ".jpg"

                    }

                    Picasso.with(mcontext)
                        .load(img)
                        .placeholder(R.drawable.user_icon_black)
                        .error(R.drawable.user_icon_black)
                        .into(dialog_imageview)

                } else {

                    try {
                        number = visitor.vlMobile.substring(3)
                    } catch (e: StringIndexOutOfBoundsException) {
                    }

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
                AppUtils.playAttachementAudio(mcontext, visitor.vlVoiceNote)
            }

            holder.tv_comments.text = visitor.vlCmnts

            holder.expanded_view.visibility = View.GONE

            holder.iv_call.visibility = if (visitor.vlMobile.length > 5) View.VISIBLE else View.INVISIBLE
            holder.iv_call.setOnClickListener {

                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + visitor.vlMobile)
                mcontext.startActivity(intent)
            }

        }


    }

    private fun updateAttachments(visitor: VisitorLog, holder: MenuHolder) {
        Log.v("taaag", "updating attachmetns")

        if (visitor.vlVenImg.isEmpty() and visitor.vlVoiceNote.isEmpty() and visitor.vlCmnts.isEmpty()) {
            holder.iv_attachment.visibility = View.GONE
            holder.expanded_view.visibility = View.GONE
        } else {

            holder.iv_attachment.visibility = View.VISIBLE
            holder.iv_attachment.setOnClickListener {

                if (holder.expanded_view.visibility == View.GONE) {
                    holder.expanded_view.visibility = View.VISIBLE
                } else {
                    holder.expanded_view.visibility = View.GONE
                    AppUtils.stopAudioPlayback()
                }
            }
            if (visitor.vlVoiceNote.isEmpty()) {
                holder.iv_play.visibility = View.GONE
            } else {
                holder.iv_play.visibility = View.VISIBLE
                holder.iv_play.setOnClickListener {
                    AppUtils.stopAudioPlayback()
                    AppUtils.playAttachementAudio(mcontext, visitor.vlVoiceNote)
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
                    val imageList: Array<String> = visitor.vlVenImg.split(",".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()


                    holder.rv_images.setHasFixedSize(true)
                    holder.rv_images.adapter = HorizontalImagesAdapter(mcontext, imageList)

                }
            }
        }
    }

    private fun updateVisitorStatus(visitor: VisitorLog, position: Int, status: String) {

        try {
            val lgid = visitor.vlVisLgID
            deleteEntryFromList(lgid, position, false)
            VisitorLogRepo.updateVisitorStatus(mcontext, visitor, status)
        } catch (e: Exception) {

        }


    }

    private fun deleteEntryFromList(lgid: Int, position: Int, updateSearchList: Boolean = true) {
        try {
            if (updateSearchList) {
                searchList?.removeAt(position)
                notifyItemRemoved(position)
            }
            timerHashMap[lgid.toString()]?.cancel()
            timerHashMap.remove(lgid.toString())
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
        //        val exitdate: TextView
        val ll_card: LinearLayout
        val expanded_view: ConstraintLayout
        val lyt_text: LinearLayout
        //        val iv_map: ImageView
        val rv_images: RecyclerView
        val iv_play: ImageView
        val tv_comments: TextView
        val iv_attachment: ImageView

        var isAnimating: Boolean = false

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
//            exitdate = view.findViewById(R.id.tv_exitdate)
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

        searchList = VisitorLogRepo.search_IN_Visitors(search)
        notifyDataSetChanged()

    }

    inner class FBValueEventListener(val visitor: VisitorLog, var holder: MenuHolder) : ValueEventListener {

        val vlLogId: String = visitor.vlVisLgID.toString()
        val venImg = visitor.vlVenImg
        val voiceNote = visitor.vlVoiceNote
        val cmnts = visitor.vlCmnts
        var actionTime = visitor.vlEntryT
        var visitorType = visitor.vlVisType
        var apprStat = visitor.vlApprStat

        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val firebaseObject = dataSnapshot.getValue(NotificationSyncModel::class.java)

            if (firebaseObject != null) {

                var msLeft: Long = 0
                when {
                    visitorType.contains(DELIVERY, true) -> {
                        var timeupCallback: () -> Unit = {}

                        holder.isAnimating = false
                        holder.ll_card.clearAnimation()

                        val fbColor = firebaseObject.buttonColor.toLowerCase()
                        actionTime = if (dataSnapshot.hasChild("updatedTime")) {
                            firebaseObject.updatedTime
                        } else {
                            visitor.vlEntryT
                        }

                        try {
                            holder.ll_card.setBackgroundColor(Color.parseColor(fbColor))
                        } catch (e: Exception) {
                            if (ACCEPTED_COLOR.contains(fbColor)) {
                                holder.ll_card.setBackgroundColor(Color.parseColor(PENDING_COLOR))
                            } else if (REJECTED_COLOR.contains(fbColor)) {
                                holder.ll_card.setBackgroundColor(Color.parseColor(REJECTED_COLOR))
                            } else {
                                holder.ll_card.setBackgroundColor(Color.parseColor(PENDING_COLOR))
                            }
                            e.printStackTrace()
                        }


                        checkForAttachments(firebaseObject)

                        val expiry_reject_time = if (debug) 30 * 60 else 30 * 60

                        var onTick: (ms: Long) -> Unit = {}

                        when (fbColor) {
                            "#00ff00",
                            ACCEPTED_COLOR -> {// accepted by resident, start timer for 7 mins to overstay
                                holder.btn_makeexit.visibility = View.VISIBLE
                                msLeft = msLeft(actionTime, MAX_DELIVERY_ALLOWED_SEC)
                                if (!apprStat.equals(APPROVED, true)) {
                                    VisitorLogRepo.get_IN_VisitorLog(true)
                                }
                                if (msLeft < 0) {// time is up
                                    holder.ll_card.setBackgroundColor(Color.parseColor(TIMEUP_COLOR))
                                    holder.isAnimating = true
                                    holder.ll_card.startAnimation(animBlink)
                                    holder.btn_makeexit.visibility = View.VISIBLE
                                } else {
                                    timeupCallback = {
                                        Toast.makeText(mcontext, "overtime", Toast.LENGTH_SHORT).show()
                                        refreshList()
                                    }
                                }
                            }
                            REJECTED_COLOR -> {// rejected by resident, start timer for 30 mins to remove
                                holder.btn_makeexit.visibility = View.INVISIBLE
                                msLeft = msLeft(actionTime, expiry_reject_time)// 30 mins
                                Toast.makeText(mcontext, "rejected $msLeft", Toast.LENGTH_SHORT).show()
                                timeupCallback = {
                                    Toast.makeText(mcontext, "move rejected", Toast.LENGTH_SHORT).show()
                                    updateVisitorStatus(visitor, holder.adapterPosition, REJECTED)
                                    refreshList()
                                }

                            }
                            else -> {// pending, start timer for 30mins to remove, hide exit button
                                holder.btn_makeexit.visibility = if (debug) View.VISIBLE else View.INVISIBLE
                                msLeft = msLeft(actionTime, expiry_reject_time)// 30 mins
                                timeupCallback = {
                                    Toast.makeText(mcontext, "expired", Toast.LENGTH_SHORT).show()
                                    updateVisitorStatus(visitor, holder.adapterPosition, EXPIRED)
                                    refreshList()
                                }
                            }
                        }

                        if (timerHashMap.contains(vlLogId)) {

                            timerHashMap[vlLogId]?.cancel()
                            timerHashMap.replace(vlLogId, TimerUtil(msLeft, timeupCallback, onTick))

                        } else {
                            timerHashMap[vlLogId] = TimerUtil(msLeft, timeupCallback, onTick)
                        }
                        timerHashMap[vlLogId]?.start()
                    }
                    visitorType.contains(STAFF, true) -> {
                        holder.btn_makeexit.visibility = View.VISIBLE
                        checkForAttachments(firebaseObject)
                    }
                }

                try {
                    Log.d("taaag", "vlID: $vlLogId, time: $actionTime, msleft: $msLeft, actTime: ${visitor.vlsActTm}, status: ${visitor.vlApprStat}, type: $visitorType")
                } catch (ignored: IllegalStateException) {
                }


            }

        }

        private fun checkForAttachments(firebaseObject: NotificationSyncModel) {
            if (firebaseObject.newAttachment) {

                if (venImg.isEmpty() and voiceNote.isEmpty() and cmnts.isEmpty()) {
                    VisitorLogRepo.get_IN_VisitorLog(true, object : VisitorLogRepo.VisitorLogFetchListener {
                        override fun onFetch(visitorLog: ArrayList<VisitorLog>?, error: String?) {
                            val v = VisitorLogRepo.get_IN_VisitorForVisitorId(vlLogId)
                          try {
                              updateAttachments(v!!, holder)
                          }catch (e:KotlinNullPointerException){

                          }
                        }

                    })
                } else {
                    updateAttachments(visitor, holder)
                }

            }
        }

        private fun refreshList() {

            Handler().post {
                VisitorLogRepo.get_IN_VisitorLog(true, object : VisitorLogRepo.VisitorLogFetchListener {
                    override fun onFetch(visitorLog: ArrayList<VisitorLog>?, error: String?) {
                        searchList = visitorLog
                        if (visitorLog != null) {
                            visitorList = visitorLog
                        }
                        notifyDataSetChanged()
                    }

                })
            }

        }

    }


}