package com.oyespace.guards.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.ConstantUtils.DELIVERY
import com.oyespace.guards.utils.ConstantUtils.IMAGE_BASE_URL
import com.oyespace.guards.utils.DateTimeUtils.*
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*


class VistorEntryListAdapter(
    private var visitorList: ArrayList<VisitorLog>,
    private val mcontext: Context
) : RecyclerView.Adapter<VistorEntryListAdapter.MenuHolder>() {

    lateinit var firebasedataMapp : HashMap<String, String>
    lateinit var ref: DatabaseReference
    private var refreshImages: Boolean = true
    private var searchList: ArrayList<VisitorLog>? = null
    var number: String? = null
    var searchString: String = ""
    var mobnumber:String?=null
    lateinit var mp:  MediaPlayer

    init {
        this.searchList = visitorList
        ref= FirebaseDatabase.getInstance().getReference("NotificationSync")
        firebasedataMapp= hashMapOf()
    }

    internal var animBlink: Animation =
        AnimationUtils.loadAnimation(mcontext, R.anim.animation_blink)

    // animBlink.setAnimationListener(this)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = LayoutInflater.from(mcontext).inflate(R.layout.layout_dashboard_adapter_row, parent, false) as ViewGroup
        return MenuHolder(mainGroup)
    }

    override fun onBindViewHolder(holder: MenuHolder, p: Int) {

        val position = holder.adapterPosition

        var orderData: VisitorLog? = null
        try {
            orderData = searchList!!.get(position)
        } catch (e: Exception) {
            return
        }
        Log.e("orderData", "" + orderData)
        if (orderData != null && orderData.isValid) {

            val vistordate = orderData.asAssnID
            holder.apartmentNamee.text = orderData.unUniName
            holder.entryTime.text = formatDateHM(orderData.vlEntryT) + " "
            Log.d("ddd", formatDateHM(orderData.vlEntryT))
            holder.entrydate.text = formatDateDMY(orderData.vldCreated)
            if (orderData.vlExitT.equals("0001-01-01T00:00:00", true)) {
//            if (true) {
                holder.exitTime.text = ""
                holder.exitdate.text = ""
                holder.btn_makeexit.visibility = View.VISIBLE

                if (orderData.vlVenImg.contains(",")) {
                    var imageList: Array<String>
                    imageList = orderData.vlVenImg.split(",".toRegex())
                        .dropLastWhile({ it.isEmpty() }).toTypedArray()


                    holder.rv_images.setHasFixedSize(true)
                    val linearLayoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(mcontext,
                            LinearLayoutManager.HORIZONTAL,true)
                    holder.rv_images.layoutManager = linearLayoutManager


                    val adapter = HorizontalImagesAdapter( mcontext,imageList)
                    holder.rv_images.adapter = adapter

                }

                if (orderData.vlVisType.equals(DELIVERY) && deliveryTimeUp(orderData.vlEntryT, getCurrentTimeLocal(), 1)) {
//                    holder.ll_card.setBackgroundColor(Color.parseColor("#ff0000"))
//                    holder.ll_card.startAnimation(animBlink)
                } else {
//                    holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
//                    holder.ll_card.animation = null
                    ref.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {

//                            if(p0.exists()){
//                                for(h in p0.children){
//
//                                    var data= h.getValue(NotificationSyncModel::class.java)
//
//                                    firebasedataMapp.put(data!!.visitorlogId,data!!.buttonColor)
//                                    //  firebasedatalist.add(data!!)
//                                    try {
//                                        if (firebasedataMapp.containsKey(orderData?.vlVisLgID.toString())) {
//
//                                            Toast.makeText(mcontext,"mcv",Toast.LENGTH_LONG).show()
//                                            holder.ll_card.setBackgroundColor(Color.parseColor(firebasedataMapp[orderData?.vlVisLgID.toString()]))
//                                        }
//                                    }catch (e:IndexOutOfBoundsException){
//
//                                    }
//
//                                    Toast.makeText(mcontext, firebasedataMapp.get(data!!.visitorlogId), Toast.LENGTH_LONG).show()
//
//                                }
//                            }

                        }

                    })
                }

            } else {
                holder.exitTime.text = formatDateHM(orderData.vlExitT)
                holder.exitdate.text = formatDateDMY(orderData.vldUpdated)
                holder.btn_makeexit.visibility = View.INVISIBLE
                holder.ll_card.setBackgroundColor(Color.parseColor("#ffffff"))
                holder.ll_card.animation = null

            }

            holder.serviceProvider.text = orderData.vlComName + ", Visitors: " + orderData.vlVisCnt
            holder.visitorName.text = orderData.vlfName

            holder.btn_makeexit.setOnClickListener {
                holder.btn_makeexit.visibility = View.GONE
                exitVisitor(orderData, position)
            }

            if (orderData.vlMobile.length > 5) {
                holder.iv_call.visibility = View.VISIBLE
            } else {
                holder.iv_call.visibility = View.INVISIBLE
            }
            try {


                number = orderData.vlMobile.substring(3)


            } catch (e: StringIndexOutOfBoundsException) {
            }
            // Log.v("Image URL",IMAGE_BASE_URL+"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+number+".jpg")


            var imgPath = IMAGE_BASE_URL + "Images/" + orderData.vlEntryImg

            if (orderData.vlVisType.equals("STAFF", true)) {
                if (orderData.vlEntryImg.isEmpty()) {
                    imgPath = IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + orderData.reRgVisID + ".jpg"
                }
            } else {
                if (orderData.vlEntryImg.isEmpty()) {
                    imgPath = IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + number + ".jpg"
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

                val alertadd = AlertDialog.Builder(mcontext)
                val factory = LayoutInflater.from(mcontext)
                val view = factory.inflate(R.layout.dialog_big_image, null)
                var dialog_imageview: ImageView? = null
                dialog_imageview = view.findViewById(R.id.dialog_imageview)


                //  if (orderData.vlVisType.equals("STAFF", true)) {

                // alertadd.setNeutralButton("Here!", DialogInterface.OnClickListener { dlg, sumthin -> })


                if (orderData.vlVisType.equals("STAFF", true)) {

                    if (orderData.vlEntryImg.equals("")) {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/PERSON" + "STAFF" + orderData.reRgVisID + ".jpg")
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    } else {


                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/" + orderData.vlEntryImg)
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    }

                } else {

                    if (orderData.vlEntryImg.equals("")) {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + number + ".jpg")
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)
                    } else {
                        Picasso.with(mcontext)
                            .load(IMAGE_BASE_URL + "Images/" + orderData.vlEntryImg)
                            .placeholder(R.drawable.user_icon_black)
                            .error(R.drawable.user_icon_black)
                            .into(dialog_imageview)

                    }


                }

            alertadd.setView(view)
            alertadd.show()
        }


            if (orderData.vlVoiceNote.contains("")) {
            holder.iv_play.visibility=View.GONE
        }else{
            holder.iv_play.visibility=View.VISIBLE
        }
        holder.iv_play.setOnClickListener{
            getAudio(orderData.vlVoiceNote)
        }

            holder.tv_comments.text = orderData.vlCmnts

        holder.iv_call.setOnClickListener {

                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + orderData.vlMobile)
                mcontext.startActivity(intent)
            }
            holder.expanded_view.visibility = View.GONE

    //        if(orderData.vlCmnts.equals("")&&orderData.vlVenImg.equals("")&&orderData.vlVoiceNote.equals("")){
//            holder.iv_attachment.visibility = View.GONE
//
//        }
//
//        else{
//            holder.iv_attachment.visibility=View.VISIBLE
//
//        }

//        holder.lyt_text.setOnClickListener {
//
    //            if(orderData.vlCmnts.equals("")&&orderData.vlVenImg.equals("")&&orderData.vlVoiceNote.equals("")){
//                holder.expanded_view.visibility = View.GONE
//            }
//
//            else{
//                if (holder.expanded_view.visibility == View.GONE) {
    //                    holder.expanded_view.visibility = View.VISIBLE
//                } else {
//                    holder.expanded_view.visibility = View.GONE
//                }
//            }
//
//
//        }

            // holder.tv_purpose.text=orderData?.vlMobile
        }


    }

    private fun exitVisitor(orderData: VisitorLog, position: Int) {

        try {
            val lgid = orderData.vlVisLgID
            VisitorLogRepo.exitVisitor(mcontext, lgid)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        val log = VisitorLogRepo.get_IN_VisitorLog()
//        if (log != null) {
//            visitorList = log
//        }


        try {
            searchList!!.removeAt(position)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return searchList?.size ?: 0
    }

    fun setVisitorLog(visitorLog: ArrayList<VisitorLog>?) {
        refreshImages = true
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
        val expanded_view: LinearLayout
        val lyt_text: LinearLayout
        val iv_map: ImageView
        val rv_images:RecyclerView
        val iv_play:ImageView
        val tv_comments:TextView
        val iv_attachment:ImageView


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
            iv_map = view.findViewById(R.id.iv_map)
            rv_images=view.findViewById(R.id.rv_images)
            iv_play=view.findViewById(R.id.iv_play)
            tv_comments=view.findViewById(R.id.tv_comments)
            iv_attachment=view.findViewById(R.id.iv_attachment)


        }

    }

    fun applySearch(search: String) {

        refreshImages = search.isEmpty()

        this.searchString = search
        searchList = VisitorLogRepo.search_IN_Visitors(search)

        notifyDataSetChanged()

    }
    fun getAudio(filename:String) {



        try {
            if (mp.isPlaying) {
                mp.stop()
                mp.release()

            }

            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }



        val mediaPlayer: MediaPlayer
//
        val am = mcontext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        mediaPlayer = MediaPlayer()


        var spb = SoundPool.Builder()
        spb.setMaxStreams(10)
        var attrBuilder = AudioAttributes.Builder()
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        spb.setAudioAttributes(attrBuilder.build())
        spb.build()

        mediaPlayer.setDataSource("http://mediaupload.oyespace.com/" + filename)
        mediaPlayer.prepare()

        mediaPlayer.start()


        val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .absolutePath
        val f = File(baseDir + filename)
        f.delete()

    }


}