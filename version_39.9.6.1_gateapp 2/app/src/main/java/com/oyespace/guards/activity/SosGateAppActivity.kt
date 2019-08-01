package com.oyespace.guards.com.oyespace.guards.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import kotlinx.android.synthetic.main.activity_sos_screen_gate.*
import java.io.IOException

open class SosGateAppActivity:BaseKotlinActivity () {
    lateinit var edittext: EditText
    lateinit var edittext1: EditText
    lateinit var edittext2: EditText
    lateinit var save: Button
    internal lateinit var demoRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_screen_gate)
        val intent = intent
        val message = intent.getStringExtra("message")
        if(!message.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("Ok", { dialog, which -> }).show()
        }
        // get reference to button
        val btn_click_me = findViewById(R.id.b1_start) as Button
        // set on-click listener
        btn_click_me.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dview = layoutInflater.inflate(R.layout.activity_custom_alert, null)

            val alert = builder.create()

            alert.setView(dview)

            alert.show()
            alert.getWindow().setLayout(900, 500)
            val button: Button = dview.findViewById(R.id.b1)
            button.setOnClickListener({
                alert.dismiss()
            })
        }
        lateinit var ref : DatabaseReference
        ref = FirebaseDatabase.getInstance().getReference("SOS")
        val h= Units("17",12.8,77.6,9999088899,"hdgshdg",0,"df",5102)
               val _recyclerView: RecyclerView = findViewById(R.id.recyclerV)
        _recyclerView.layoutManager = GridLayoutManager(this, 3)

        val items = ArrayList<MyData>()

        items.add(
            MyData(
                BitmapFactory.decodeResource(resources, R.mipmap.amb_new),
                "Ambulance",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "108"
            )
        )
        items.add(
            MyData(
                BitmapFactory.decodeResource(resources, R.mipmap.pol_ice_1),
                "Police",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "100"
            )
        )
        items.add(
            MyData(
                BitmapFactory.decodeResource(resources, R.mipmap.fir_birgade_new),
                "Fire Brigade",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "101"
            )
        )


        //creating our adapter
        val adapter = RecyclerViewAdapter(items)

        //now adding the adapter to recyclerview
        _recyclerView.adapter = adapter

        b1_stop.setOnClickListener({ b1_start.setVisibility(View.GONE) })
      /*
        img2.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(ph)
            startActivity(intent)
        }*/


        var ref1=FirebaseDatabase.getInstance().getReference("/SOS/17")
        ref1.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChildren())
                {
                    var title:String= p0.child("5102").child("name").getValue() as String
                    Toast.makeText(applicationContext,title, Toast.LENGTH_LONG).show()
                }
            }

        })
        initView()
}
    private fun initView() {
        //This method will use for fetching Token
        Thread(Runnable {
            try {
                Log.i("Service", FirebaseInstanceId.getInstance().getToken("1", "FCM"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }
}



