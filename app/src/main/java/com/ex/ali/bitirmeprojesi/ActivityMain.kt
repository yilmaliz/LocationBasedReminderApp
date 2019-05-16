package com.ex.ali.bitirmeprojesi


import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_LOCATION = 10
    private var referance = FirebaseDatabase.getInstance().reference
    private var crrtUsr = FirebaseAuth.getInstance().currentUser
    val ReminderNames: ArrayList<String> = ArrayList()
    lateinit var myListener:FirebaseAuth.AuthStateListener
    private var manager: NotificationManager? = null
    private var managerForFriend: NotificationManager? = null
    private var NotificationService=ServiceSendNotification::class.java
    private var NotificationServiceFriend=ServiceSendNotificationForFriend::class.java

    private var sIntent: Intent?=null
    private var sIntentForFriend: Intent?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        managerForFriend = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        getListItems()
        initMyListener()
        val serviceClass = ServiceLocationForCurrentUser::class.java
        val intent = Intent(applicationContext, serviceClass)

        sIntent=Intent(applicationContext,NotificationService)
        startService(sIntent)
        sIntentForFriend= Intent(applicationContext,NotificationServiceFriend)
        startService(sIntentForFriend)

        btnProfilePage.setOnClickListener{
            var intent=Intent(this,ActivityProfilePage::class.java)
            startActivity(intent)
        }

        btnRmndrPage.setOnClickListener{
            var intent=Intent(this,ActivityReminderPage::class.java)
            startActivity(intent)
        }

        btnHomePage.setOnClickListener{
            var intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


        if (checkPermissionForLocation(this)) {
            startService(intent)
        }

        btnAddReminder.setOnClickListener{
            var intent=Intent(this,ActivityReminderPage::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(intent)
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show() } }
    }
    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { true } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false } } else { true } }

    private fun getListItems() {


        var sorgu = referance.child("PlaceBasedReminder").child(crrtUsr!!.uid)
        sorgu.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){


                    Log.e("CountMain",": "+p0.childrenCount)
                    for (rmndr in p0.children){
                        var userReminder =  rmndr.getValue(NesneLocationBasedReminder::class.java)

                        var userReminderName =userReminder?.reminderName
                        var userReminderUserId =userReminder?.reminderUserID
                        var userReminderNote =userReminder?.reminderNote
                        var userReminderRadius =userReminder?.reminderRadius
                        var userReminderLatitude=userReminder?.reminderLocationLatitude
                        var latlong= LatLng(userReminder?.reminderLocationLatitude!!, userReminder?.reminderLocationLongitude!!)

                        ReminderNames.add(userReminderName.toString())


                    }
                    Log.e("Reminder List1: ",""+ReminderNames)
                }
                Log.e("Reminder List2: ",""+ReminderNames)
                getalistView(ReminderNames)
            }

        })



    }

    private fun getalistView(reminderNames: ArrayList<String>) {
        Log.e("Reminder List3: ",""+reminderNames)
        val adapter = ArrayAdapter(this,
            R.layout.items, reminderNames)



        val listView:ListView = findViewById(R.id.reminderList)
        listView.setAdapter(adapter)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long) {


                val itemValue = listView.getItemAtPosition(position) as String


                Toast.makeText(applicationContext,
                    "Position :$position\nItem Value : $itemValue", Toast.LENGTH_LONG).show()
            }
        }

    }


    private fun initMyListener() {
        myListener= object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici=p0.currentUser
                if(kullanici != null){

                }else{
                    var intnt=Intent(this@MainActivity,ActivityLogin::class.java)
                    intnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intnt)
                    finish()
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menuCıkısYap -> {
                cikisYap()
                return true
            }
            R.id.menuProfil ->{
                var openProfile=Intent(this@MainActivity,ActivityProfilePage::class.java)
                startActivity(openProfile)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun cikisYap() {
        FirebaseAuth.getInstance().signOut()
    }
    override fun onResume() {
        super.onResume()
        controlForUSer()
    }
    private fun controlForUSer() {
        var kullanici=FirebaseAuth.getInstance().currentUser
        if(kullanici == null){
            var CikisIntent=Intent(this@MainActivity,ActivityLogin::class.java)
            CikisIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(CikisIntent)
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener (myListener)
    }
    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(myListener) }

}
