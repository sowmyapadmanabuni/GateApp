package com.oyespace.guards.adapter

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
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
import com.bumptech.glide.signature.StringSignature
import com.google.firebase.database.*
import com.oyespace.guards.BGService
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.models.NotificationSyncModel
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.UpdateApprovalStatus
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.*
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateFirebaseColorforExit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.util.*


class VisitorEntryListAdapter(private var visitorList: ArrayList<VisitorLog>, private val mcontext: Context) : RecyclerView.Adapter<VisitorEntryListAdapter.MenuHolder>() {

    val ACCEPTED_COLOR = "#75be6f"
    val REJECTED_COLOR = "#ff0000"
    val PENDING_COLOR = "#ffb81a"
    val TIMEUP_COLOR = "#ff0000"
    var status_: String? =null
    var progress:ProgressDialog?=null
    lateinit var fbEventListener:FBValueEventListener

    private var searchList: ArrayList<VisitorLog>? = null
    var number: String? = null
    var mobnumber: String? = null

    val timerHashMap: HashMap<String, TimerUtil>
    var notificationSyncFBRef: DatabaseReference
    var fbdbAssocName: String

    init {
        progress =  ProgressDialog(mcontext);
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

        if (visitor.isValid) {
            val vlLogId = visitor.vlVisLgID.toString()
            val unitName = visitor.unUniName
            val unitID = visitor.unUnitID
            val phone = visitor.vlMobile


            val asAssnID =  visitor.asAssnID
            val asAsnName = LocalDb.getAssociation()!!.asAsnName
            val ntDesc = visitor.vlfName + " from " + visitor.vlComName + " has left your premises"
            val ntTitle = visitor.vlfName + " left"
            val ntType = "gate_app";
            val sbSubID = visitor.unUnitID
            val userID = visitor.reRgVisID

            Log.e("VISITOR_DAT",""+visitor);
            Log.v("taaag", "refreshed IN list element at $position for vlID: $vlLogId unit: $unitName ($unitID)")
            holder.apartmentNamee.text = if (debug) "${unitName} ($vlLogId) ($phone)" else unitName
            holder.entryTime.text = formatDateHM(visitor.vlEntryT) + " "
            holder.entrydate.text = formatDateDMY(visitor.vldCreated)



            if (visitor.vlExitT.equals("0001-01-01T00:00:00", true)) {
                holder.exitTime.text = ""

              fbEventListener = FBValueEventListener(vlLogId, holder)

                updateAttachments(visitor, holder)

                val type = visitor.vlVisType
                when {

                    type.contains(DELIVERY, true) -> {
                        holder.tv.text=visitor.vlApprStat
                        notificationSyncFBRef.child(vlLogId).addValueEventListener(fbEventListener)
                    }
                    type.contains(KIDEXIT, true) -> {
                        holder.tv.text=visitor.vlApprStat
                        notificationSyncFBRef.child(vlLogId).addValueEventListener(fbEventListener)
                    }

                    type.contains(STAFF, true) -> {
                        notificationSyncFBRef.child(vlLogId).addValueEventListener(fbEventListener)
                        holder.btn_makeexit.visibility = View.VISIBLE
                    }
                    else -> {
                        holder.btn_makeexit.visibility = View.VISIBLE
                        Log.v("taaag", "fb child listener not attached")
                    }
                }

                if (debug) {
                    holder.btn_makeexit.visibility = View.VISIBLE
                }
                holder.btn_makeexit.setOnClickListener {
                    val type = visitor.vlVisType
                    when {
                        type.contains(GUEST,true)->{
                            holder.btn_makeexit.visibility = View.GONE
                            notificationSyncFBRef.child(vlLogId).removeEventListener(fbEventListener)
                            updateVisitorStatus(visitor, position, EXITED, false)
                        }

                        type.contains(STAFF,true)->{
                            holder.btn_makeexit.visibility = View.GONE
                            notificationSyncFBRef.child(vlLogId).removeEventListener(fbEventListener)
                            updateVisitorStatus(visitor, position, EXITED, false)
                        }

                        type.contains(DELIVERY, true) -> {
                            if (status_!!.contains("EntryApproved")) {


                            }
                            if (holder.btn_makeexit.text == "Request Exit") {

                                updateFirebaseColorforExit(vlLogId.toInt(), "#ffb81a", "ExitPending")

                                VisitorLogRepo.updateVisitorStatus(mcontext, visitor, EXITPENDING, true)
                                updateApprovalStatus("ExitPending",visitor.vlVisLgID,visitor.vlApprdBy,"")

                                val assName = LocalDb.getAssociation()!!.asAsnName
                                val gateName = Prefs.getString(GATE_NO, null)
                                val d = Intent(mcontext, BackgroundSyncReceiver::class.java)
                                d.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
                                d.putExtra("msg",  "${visitor.vlfName} from ${visitor.vlComName} has requested for exit from $gateName")
                                d.putExtra("mobNum", visitor.vlMobile)
                                d.putExtra("name", visitor.vlfName)
                                d.putExtra("nr_id", visitor.vlVisLgID.toString())
                                d.putExtra("unitname", visitor.unUniName)
                                d.putExtra("memType", "Owner")
                                d.putExtra(ConstantUtils.UNITID, visitor.unUnitID)
                                d.putExtra(ConstantUtils.COMPANY_NAME, visitor.vlComName)
                                d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID, visitor.unUnitID)
                                d.putExtra("VLVisLgID", visitor.vlVisLgID)
                                d.putExtra(ConstantUtils.VISITOR_TYPE, visitor.vlVisType)
                                d.putExtra(ConstantUtils.SEND_NOTIFICATION, true)
                                mcontext.sendBroadcast(d)
                            }
                            else if(holder.btn_makeexit.text == "Exit"){
                                notificationSyncFBRef.child(vlLogId).removeEventListener(fbEventListener)
                                updateVisitorStatus(visitor, holder.adapterPosition, EXITED, false)
                            }
                        }

                        type.contains(KIDEXIT, true) -> {
                            if(holder.btn_makeexit.text == "Exit"){
                                notificationSyncFBRef.child(vlLogId).removeEventListener(fbEventListener)
                                updateVisitorStatus(visitor, holder.adapterPosition, EXITED, false)
                            }
                        }
                    }


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

            if(visitor.vlComName.contains("Others", true)&&(visitor.vlVisType.contains(DELIVERY, true))) {
                holder.tv_purposeofvisit.text=visitor.vlpOfVis
                holder.tv_purposeofvisit.visibility=View.VISIBLE
            }
            else  if(visitor.vlComName.contains("Kid Exit",true)){
                holder.tv_purposeofvisit.text="Kid Name: "+visitor.vlKidName
                holder.tv_purposeofvisit.visibility=View.VISIBLE
            }
            else{
                holder.tv_purposeofvisit.visibility=View.GONE
            }


            if (visitor.vlVisType.contains(STAFF, true)) {
                if (entryImg.isEmpty()) {
                    imgPath = IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + visitor.reRgVisID + ".jpg"
                }
            }

//                val baos = ByteArrayOutputStream()
//                val bitmapp = BitmapFactory.decodeResource(mcontext.resources, R.drawable.user_icon_black)
//                bitmapp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                var imageBytes = baos.toByteArray()
//                val imageString = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
//                imageBytes = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT)
//                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                holder.iv_user.setImageBitmap(decodedImage)

            val imageAsBytes = android.util.Base64.decode(visitor.vlEntryImg,android.util.Base64.DEFAULT);
            val decodedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size);
            holder.iv_user.setImageBitmap(decodedImage)

            Log.i("taaag", "loading image $imgPath")
//            Glide.with(mcontext)
//                .load(Uri.parse(imgPath))
//                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(false)
//                .signature(StringSignature(System.currentTimeMillis().toString()))
//                .into(holder.iv_user)




            holder.iv_user.setOnClickListener {

                val alertadd = AlertDialog.Builder(mcontext)
                val factory = LayoutInflater.from(mcontext)
                val view = factory.inflate(R.layout.dialog_big_image, null)
                var dialog_imageview: ImageView? = null
                dialog_imageview = view.findViewById(R.id.dialog_imageview)

                dialog_imageview!!.setBackground(holder.iv_user!!.getDrawable())
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



                var agentNumber="AGENTNUMBER="+visitor.vlMobile.replace("+91", "")
                var gateMobileNumber= Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")
                TaptoCallApi.taptocallApi(gateMobileNumber,agentNumber, mcontext)

            }

        }


    }

    fun updateApprovalStatus( vlApprStat: String, vlVisLgID: Int, vlApprdBy: String, vlExAprdBy: String) {


        val dataReq = UpdateApprovalStatus(vlApprStat,vlVisLgID,vlApprdBy,vlExAprdBy)


        RetrofitClinet.instance
            .updateApprovalStatus(OYE247TOKEN, dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<Any>() {

                override fun onSuccessResponse(any: Any) {


                }


                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
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
                else{

                        var imageList: Array<String>
                        imageList = visitor.vlVenImg.split(",".toRegex())
                            .dropLastWhile({ it.isEmpty() }).toTypedArray()


                        holder.rv_images.setHasFixedSize(true)
                        holder.rv_images.adapter = HorizontalImagesAdapter(mcontext, imageList)

                }
            }
        }
    }

    private fun updateVisitorStatus(visitor: VisitorLog, position: Int, status: String, onlyLocal: Boolean = false) {

        try {
            val lgid = visitor.vlVisLgID
            when (status) {
                EXITPENDING-> deleteEntryFromList(lgid, position, false)
                ENTRYAPPROVED -> deleteEntryFromList(lgid, position, false)
                ENTRYREJECTED -> deleteEntryFromList(lgid, position, !onlyLocal)
                else -> deleteEntryFromList(lgid, position, true)
            }
            VisitorLogRepo.updateVisitorStatus(mcontext, visitor, status, onlyLocal)
        } catch (e: Exception) {

        }
    }

    private fun deleteEntryFromList(lgid: Int, position: Int, removeFromSearchList: Boolean = true) {
        try {
            if (removeFromSearchList) {
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
        val tv_purposeofvisit: TextView
        val ll_card: LinearLayout
        val expanded_view: ConstraintLayout
        val lyt_text: LinearLayout
        //        val iv_map: ImageView
        val rv_images: RecyclerView
        val iv_play: ImageView
        val tv_comments: TextView
        val iv_attachment: ImageView
        val tv:TextView

        var isAnimating: Boolean = false
         var timer: CountDownTimer? = null


        init {

            entryTime = view.findViewById(R.id.tv_entrytime)
            exitTime = view.findViewById(R.id.tv_exittime)
            visitorName = view.findViewById(R.id.tv_name)

            if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
                visitorName.textSize = 10 * mcontext.resources.displayMetrics.density
            }

            tv=view.findViewById(R.id.tv)
            serviceProvider = view.findViewById(R.id.tv_serviceprovider)
            apartmentNamee = view.findViewById(R.id.tv_unitname)
            btn_makeexit = view.findViewById(R.id.btn_makeexit)
            iv_call = view.findViewById(R.id.iv_call)
            iv_user = view.findViewById(R.id.iv_user)

            entrydate = view.findViewById(R.id.tv_entrydate)
            tv_purposeofvisit = view.findViewById(R.id.tv_purposeofvisit)
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

    inner class FBValueEventListener(val vlLogId: String, var holder: MenuHolder) : ValueEventListener {

        lateinit var visitor: VisitorLog
        lateinit var venImg: String
        lateinit var voiceNote: String
        lateinit var cmnts: String
        lateinit var apprStat: String

        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(dataSnapshot: DataSnapshot) {

            visitor = VisitorLogRepo.get_IN_VisitorForVisitorId(vlLogId) ?: return
            if (!visitor.isValid) return

            venImg = visitor.vlVenImg
            voiceNote = visitor.vlVoiceNote
            cmnts = visitor.vlCmnts
            apprStat = visitor.vlApprStat
            var actionTime = visitor.vlEntryT
            val visitorType = visitor.vlVisType

            val firebaseObject = dataSnapshot.getValue(NotificationSyncModel::class.java)

            if (firebaseObject != null) {

                var msLeft: Long = 0
                when {
                    visitorType.contains(DELIVERY, true) -> {


                        var timeupCallback: () -> Unit = {}

                        holder.isAnimating = false
                        holder.ll_card.clearAnimation()

                        val fbColor = firebaseObject.buttonColor.toLowerCase()
                         status_=firebaseObject.status
                      //  Toast.makeText(mcontext, status_, Toast.LENGTH_SHORT).show()

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

//                        val expiry_reject_time = if (debug) 3 * 60 else 30 * 60

                        var onTick: (ms: Long) -> Unit = {}

                        when (fbColor) {
                            "#00ff00",
                            ACCEPTED_COLOR -> {

                                if(status_!!.contains("EntryApproved")) {
                                    // accepted by resident, start timer for 7 mins to overstay

                                    holder.tv.text="Entry Approved"

                                    holder.btn_makeexit.visibility = View.VISIBLE
                                    msLeft = msLeft(actionTime, MAX_DELIVERY_ALLOWED_SEC)


                                     holder.btn_makeexit.text = "Request Exit"

                                    VisitorLogRepo.updateVisitorStatus(mcontext, visitor, ENTRYAPPROVED, true)

                                    if (msLeft < 0) {// time is up
                                        holder.ll_card.setBackgroundColor(Color.parseColor(TIMEUP_COLOR))
                                        holder.isAnimating = true
                                        holder.ll_card.startAnimation(animBlink)
                                        holder.btn_makeexit.visibility = View.VISIBLE

                                       // val overstayValue=Prefs.getBoolean(PrefKeys.BG_NOTIFICATION_ON, false)

                                      // if (overstayValue==true) {
                                           // Let it continue running until it is stopped.


                                        Handler().postDelayed({
                                            var serviceIntent = Intent(mcontext, BGService::class.java);
                                            mcontext.startService(serviceIntent);
                                        }, 1000*1*60)



                                      // }




                                    } else {
                                        timeupCallback = {
                                            if (debug)
                                                Toast.makeText(mcontext, "overtime", Toast.LENGTH_SHORT).show()
                                            refreshList()// this will get you overstaying sorted list
                                        }
                                    }
                                }
                                else if(status_!!.contains("ExitApproved")){

                                    holder.tv.text=status_
                                    holder.btn_makeexit.visibility = View.VISIBLE
                                    holder.btn_makeexit.setEnabled(true)
                                    holder.btn_makeexit.text = "Exit"

                                }
                            }
                            REJECTED_COLOR -> {// rejected by resident, start timer for 30 mins to remove

                                holder.tv.text=status_
                                holder.btn_makeexit.visibility = View.INVISIBLE
                                msLeft = msLeft(actionTime, MAX_ENTRY_EXPIRY_SEC)// 30 mins
                                if (debug)
                                    Toast.makeText(mcontext, "rejected ${msLeft / 1000} secs left", Toast.LENGTH_SHORT).show()
                                if (msLeft > 0) {
                                    updateVisitorStatus(visitor, holder.adapterPosition, ENTRYREJECTED, true)
                                }
                                timeupCallback = {
                                    if (debug)
                                        Toast.makeText(mcontext, "moved rejected", Toast.LENGTH_SHORT).show()
                                    updateVisitorStatus(visitor, holder.adapterPosition, ENTRYREJECTED, false)
                                }

                            }
                            else -> {// pending, start timer for 30mins to remove, hide exit button

                                if(status_!!.contains("EntryPending")) {

                                    holder.tv.text="Entry Pending"
                                    holder.btn_makeexit.visibility = if (debug) View.VISIBLE else View.INVISIBLE

                                    msLeft = msLeft(actionTime, MAX_ENTRY_EXPIRY_SEC)// 30 mins
                                    timeupCallback = {
                                        if (debug)
                                            Toast.makeText(mcontext, "expired", Toast.LENGTH_SHORT).show()
                                        updateVisitorStatus(visitor, holder.adapterPosition, ENTRYEXPIRED, false)
                                    }
                                }
                                else if(status_!!.contains("ExitPending")){

                                    holder.btn_makeexit.visibility = View.VISIBLE
                                    holder.btn_makeexit.text="Waiting for Approval"
                                    holder.btn_makeexit.setEnabled(false);
                                    if (debug)
                                        Toast.makeText(mcontext, "expired", Toast.LENGTH_SHORT).show()
                                    VisitorLogRepo.updateVisitorStatus(mcontext, visitor, EXITPENDING, true)
                                   // updateVisitorStatus(visitor, holder.adapterPosition, EXITPENDING, false)

                                    msLeft = msLeft(actionTime, MAX_ENTRY_EXPIRY_SEC)// 30 mins
                                    timeupCallback = {
                                        if (debug)
                                            Toast.makeText(mcontext, "expired", Toast.LENGTH_SHORT).show()
                                        updateVisitorStatus(visitor, holder.adapterPosition, EXITEXPIRED, false)
                                    }
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
                    visitorType.contains(KIDEXIT, true) -> {

                        var timeupCallback: () -> Unit = {}

                        holder.isAnimating = false
                        holder.ll_card.clearAnimation()

                        val fbColor = firebaseObject.buttonColor.toLowerCase()
                        status_=firebaseObject.status

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

//                        val expiry_reject_time = if (debug) 3 * 60 else 30 * 60

                        var onTick: (ms: Long) -> Unit = {}

                        when (fbColor) {
                            "#00ff00",
                            ACCEPTED_COLOR -> {

                                if(status_!!.contains("ExitApproved")) {
                                 // accepted by resident, start timer for 7 mins to overstay
                                    holder.tv.text="Exit Approved"
                                    holder.btn_makeexit.visibility = View.VISIBLE
                                    holder.btn_makeexit.setEnabled(true)
                                    holder.btn_makeexit.text = "Exit"

                                }

                            }
                            REJECTED_COLOR -> {// rejected by resident, start timer for 30 mins to remove

                                holder.tv.text=status_
                                holder.btn_makeexit.visibility = View.INVISIBLE
                                msLeft = msLeft(actionTime, MAX_ENTRY_EXPIRY_SEC)// 30 mins
                                if (debug)
                                    Toast.makeText(mcontext, "rejected ${msLeft / 1000} secs left", Toast.LENGTH_SHORT).show()
                                if (msLeft > 0) {
                                    updateVisitorStatus(visitor, holder.adapterPosition, EXITREJECTED, true)
                                }
                                timeupCallback = {
                                    if (debug)
                                        Toast.makeText(mcontext, "moved rejected", Toast.LENGTH_SHORT).show()
                                    updateVisitorStatus(visitor, holder.adapterPosition, EXITREJECTED, false)
                                }

                            }
                            else -> {// pending, start timer for 30mins to remove, hide exit button

                                if(status_!!.contains("ExitPending")) {
                                    holder.tv.text=status_
                                    holder.btn_makeexit.visibility = if (debug) View.VISIBLE else View.INVISIBLE
                                  //  updateVisitorStatus(visitor, holder.adapterPosition, EXITPENDING, false)
                                    msLeft = msLeft(actionTime, MAX_ENTRY_EXPIRY_SEC)// 30 mins
                                    timeupCallback = {
                                        if (debug)
                                            Toast.makeText(mcontext, "expired", Toast.LENGTH_SHORT).show()
                                        updateVisitorStatus(visitor, holder.adapterPosition, EXITEXPIRED, false)
                                    }
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
                }

            }

        }

        private fun checkForAttachments(firebaseObject: NotificationSyncModel) {
            if (firebaseObject.newAttachment) {

                if (venImg.isEmpty() and voiceNote.isEmpty() and cmnts.isEmpty()) {
                    VisitorLogRepo.get_IN_VisitorForVisitorId(vlLogId, true) {
                        Log.d("taaag", "updated visitor from backend: $it")
                        updateAttachments(it!!, holder)
                    }
//                    VisitorLogRepo.get_IN_VisitorLog(true, object : VisitorLogRepo.VisitorLogFetchListener {
//                        override fun onFetch(visitorLog: ArrayList<VisitorLog>?, error: String?) {
//                            val v = VisitorLogRepo.get_IN_VisitorForVisitorId(vlLogId)
//                            updateAttachments(v!!, holder)
//                        }
//
//                    })
                } else {
                    updateAttachments(visitor, holder)
                }

            } else {
                holder.iv_attachment.visibility = View.GONE
                holder.expanded_view.visibility = View.GONE
            }
        }


        private fun refreshList(fromBackend: Boolean = true) {

            Handler().post {
                VisitorLogRepo.get_IN_VisitorLog(fromBackend, object : VisitorLogRepo.VisitorLogFetchListener {
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