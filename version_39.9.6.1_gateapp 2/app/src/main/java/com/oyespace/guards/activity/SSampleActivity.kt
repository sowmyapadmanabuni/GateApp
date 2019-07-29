package com.oyespace.guards.com.oyespace.guards.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import com.oyespace.guards.R
import com.oyespace.guards.com.oyespace.guards.activity.Hero
import com.oyespace.guards.guest.GuestCustomViewFinderScannerActivity
import kotlinx.android.synthetic.main.activity_recycle_sos_items.*
import kotlinx.android.synthetic.main.activity_sos_screen_gate.*

open class SSampleActivity:AppCompatActivity () {
    lateinit var edittext: EditText
    lateinit var edittext1: EditText
    lateinit var edittext2: EditText
    lateinit var save: Button
    internal lateinit var demoRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_screen_gate)
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

        /* edittext = findViewById(R.id.editText)
        edittext1 = findViewById(R.id.editText2)
        edittext2 = findViewById(R.id.editText3)
        save = findViewById(R.id.button3)
        save.setOnClickListener()
        {
            val ref = FirebaseDatabase.getInstance().getReference("SOS")
            val h= Hero("17",12.8,77.6,9999088899,"hdgshdg",0,"df",5102)
        // val h1id=ref.push().key.toString()

               // val value = edittext.text.toString()

            ref.child("17").child("5102").setValue(h)
            //To retrieve data from firebase
            /*ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                Toast.makeText(applicationContext, snapshot.child("h1").getValue(Hero::class.java)!!.id.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {}
        })*/





        }*/


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
        //items.add(MyData(BitmapFactory.decodeResource(resources, R.drawable.security_button),"Security Supervisor",BitmapFactory.decodeResource(resources,R.mipmap.call_orange_call),"0141-444-123"))
        // items.add(MyData(BitmapFactory.decodeResource(resources, R.drawable.police_new),"Facility Incharge",BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),"9876677554"))
        // items.add(MyData(BitmapFactory.decodeResource(resources, R.drawable.firetruck_new),"ASSN Secretary",BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),"7697654446"))


        //creating our adapter
        val adapter = RecyclerViewAdapter(items)

        //now adding the adapter to recyclerview
        _recyclerView.adapter = adapter

        b1_stop.setOnClickListener({ b1_start.setVisibility(View.GONE) })
      /*  val ph = "9994863024"
        img2.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(ph)
            startActivity(intent)
        }*/


    }
}



