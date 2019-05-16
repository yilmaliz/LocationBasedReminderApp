package com.ex.ali.bitirmeprojesi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_reminder_page.*
import java.io.IOException

class ActivityReminderPage : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var mRef =FirebaseDatabase.getInstance().reference
    private var mUser=FirebaseAuth.getInstance().currentUser
    val FriendList: ArrayList<String> = ArrayList()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_page)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        GetItemList()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        btnPlaceRmndr.setOnClickListener {
            constrainPlace.visibility = View.VISIBLE
            constrainFriend.visibility = View.INVISIBLE
            }
        btnFriendRmndr.setOnClickListener {
            constrainPlace.visibility = View.INVISIBLE
            constrainFriend.visibility = View.VISIBLE
            }
        btnSearchLctn.setOnClickListener {
            GetLocation() }
        btnAddPlcBsdRmndr.setOnClickListener {
            Toast.makeText(this@ActivityReminderPage, "Please select somewhere on map and fill blanks", Toast.LENGTH_SHORT).show() }

        btnProfilePage.setOnClickListener{
            var intent= Intent(this,ActivityProfilePage::class.java)
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
    }

    private fun GetItemList() {
        var sorguFriend=mRef.child("Friendship").child(mUser!!.uid)
        sorguFriend.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for (Frnd in p0.children){
                        //Log.e("FriedName"," d"+Frnd.key)
                        FriendList.add(Frnd.key.toString())}}
                getalistView(FriendList)}
            override fun onCancelled(p0: DatabaseError){}})}
    private fun getalistView(friendList: ArrayList<String>) {
        val adapter = ArrayAdapter(this,
            R.layout.items_for_friend, friendList)
        val listView: ListView = findViewById(R.id.FriendList)
        listView.setAdapter(adapter)





        listView.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                val itemValue = listView.getItemAtPosition(position) as String

                var FriendId=mRef.child("Friendship").child(mUser!!.uid).child(itemValue)

                FriendId.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(p0: DataSnapshot) {

                        if(p0.exists()){

                            for (FrndID in p0.children){

                                var choosenFriendId=FrndID.value.toString()

                                kullanıcıSecildi(choosenFriendId,itemValue) }}}

                    override fun onCancelled(p0: DatabaseError) {} } ) } } }

    private fun kullanıcıSecildi(choosenFriendId: String, itemValue: String) {
        Log.e("Butotttonmsahdsa",""+itemValue)
        textUyarı.visibility=View.INVISIBLE
        textFriendReminderNote.visibility=View.VISIBLE
        textFriendReminderRadius.visibility=View.VISIBLE
        BtnAddFrnRmndr.visibility=View.VISIBLE
        textSecilenKullanıcı.text="Seçilen Kullanıcı: "+ itemValue
        textSecilenKullanıcı.visibility=View.VISIBLE
        create_a_FriendBasedReminder(choosenFriendId)
    }
    private fun create_a_FriendBasedReminder(choosenFriendId: String) {
        BtnAddFrnRmndr.setOnClickListener{
            if (textFriendReminderNote.text.isNotEmpty()&& textFriendReminderRadius.text.isNotEmpty()){
                Log.e("Kullanıcı bazlı reminder oluşturma: ","Başarılı")
                var RemnderNote=textFriendReminderNote.text
                var RemnderRad=textFriendReminderRadius.text

                var kullanıcıBilgileriIcinSorgu=mRef.child("kullanici").child(choosenFriendId)
                    .addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.getValue() != null){
                                var secilenKullaniciBilgileri = p0.getValue(NesneKullanici::class.java)
                                var KullaniciTamIsim=secilenKullaniciBilgileri?.isim+" "+secilenKullaniciBilgileri?.soyisim
                                create_a_FriendBasedReminder2(KullaniciTamIsim ,choosenFriendId
                                ,secilenKullaniciBilgileri?.konumLognitude,secilenKullaniciBilgileri?.konumLatitude
                                ,secilenKullaniciBilgileri?.uName,RemnderNote,RemnderRad)
                            }
                        }override fun onCancelled(p0: DatabaseError){} })
            } else{Log.e("Kullanıcı bazlı reminder oluşturma: ","Başarılı değil") } } }
    private fun create_a_FriendBasedReminder2(Isim: String, Id: String, Konum_Lognitude: Double?, Konum_Latitude: Double?
                                              , uName: String?, remnderNote: Editable, remnderRad: Editable) {
        var rad=remnderRad.toString()
        var Rad=rad.toInt()
        var AddFrienRmndrToDB=NesneFriendBasedReminder()
        AddFrienRmndrToDB.reminderName=Isim
        AddFrienRmndrToDB.otherUserUname=uName
        AddFrienRmndrToDB.reminderLocationLongitude=Konum_Lognitude
        AddFrienRmndrToDB.reminderLocationLatitude=Konum_Latitude
        AddFrienRmndrToDB.otherUserID=Id
        AddFrienRmndrToDB.reminderNote= remnderNote.toString()
        AddFrienRmndrToDB.reminderRadius=Rad
        mRef.child("FrienLocationBasedReminder").child(mUser!!.uid).child(Id)
            .setValue(AddFrienRmndrToDB).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Log.e("FreinedBAsedReminder_Eklendi","Başarılı")
                }else {Log.e("FreinedBAsedReminder_Eklenemedi","Başarısız")} }
    }
    private fun GetLocation() {
        val location = textRmndrGetLocation.text.toString()
        if (location != null && location != "") {
            var adressList: List<Address>? = null
            val geocoder = Geocoder(this)
            try { adressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) { e.printStackTrace() }
            val address = adressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            val markerOptions = MarkerOptions().position(latLng).title("Burası $location")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap!!.addMarker(markerOptions) } }
    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.setOnMarkerClickListener(this)
        mMap!!.setOnMapClickListener { chosenLatLng ->
            val markerOptions = MarkerOptions()
            markerOptions.position(chosenLatLng)
            markerOptions.title("To add a reminder for this location use 'ADD REMİNDER' button on th bottom.")
            mMap!!.clear()
            getReminders()
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(chosenLatLng))
            mMap!!.addMarker(markerOptions)
            if (chosenLatLng != null) { btnAddPlcBsdRmndr.setOnClickListener { create_a_Reminder(chosenLatLng) }} }
        setUpMap()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap!!.isMyLocationEnabled = true
        } else { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_lOCATION) } } }
    private fun create_a_Reminder(chosenLatLng: LatLng?) {

        var addRmndrToDB = NesneLocationBasedReminder()
        addRmndrToDB.reminderUserID = FirebaseAuth.getInstance().currentUser?.uid
        addRmndrToDB.reminderNote = textPlaceRmndrNote.text.toString()
        addRmndrToDB.reminderRadius = Integer.parseInt(textPlaceRmndrRadius.text.toString())
        addRmndrToDB.reminderName = textPlacermndrName.text.toString()
        addRmndrToDB.reminderLocationLatitude = chosenLatLng!!.latitude
        addRmndrToDB.reminderLocationLongitude = chosenLatLng!!.longitude
        Log.e("Log 1 ", "name: " + addRmndrToDB.reminderName)
        mRef.child("PlaceBasedReminder").child(mUser?.uid.toString()).child(textPlacermndrName.text.toString())
            .setValue(addRmndrToDB).addOnCompleteListener { task ->
                if (task.isSuccessful) { Toast.makeText(this@ActivityReminderPage, "Reminder has saved", Toast.LENGTH_SHORT).show()
                } else { Toast.makeText(this@ActivityReminderPage, "HATAAAA", Toast.LENGTH_SHORT).show() } } }
    private fun getReminders() {
        var sorgu = mRef.child("PlaceBasedReminder").child(mUser!!.uid)
        sorgu.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    Log.e("Count",": "+p0.childrenCount)
                    for (rmndr in p0.children){
                        var userReminder =  rmndr.getValue(NesneLocationBasedReminder::class.java)
                        var userReminderName =userReminder?.reminderName
                        var userReminderUserId =userReminder?.reminderUserID
                        var userReminderNote =userReminder?.reminderNote
                        var userReminderRadius =userReminder?.reminderRadius
                        var latlong=LatLng(userReminder?.reminderLocationLatitude!!, userReminder?.reminderLocationLongitude!!)
                        Log.e("Lokasyonlar: ",""+latlong)
                        mMap?.addMarker(MarkerOptions().position(latlong).title(userReminderName))
                    }}}})}
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return }
        mMap!!.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f)) } }
        getReminders()}
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_lOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap!!.isMyLocationEnabled = true }
            } else { Toast.makeText(applicationContext, "Kullanıcı konum iznini vermedi", Toast.LENGTH_SHORT).show() } }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    companion object {
        private val REQUEST_lOCATION = 1
        private val LOCATION_PERMISSION_REQUEST_CODE = 2 }
}