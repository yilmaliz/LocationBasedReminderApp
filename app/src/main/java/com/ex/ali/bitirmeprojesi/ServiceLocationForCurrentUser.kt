package com.ex.ali.bitirmeprojesi
import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ServiceLocationForCurrentUser : Service() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var mHandler1: Handler
    private lateinit var mRunnable1: Runnable
    private lateinit var mHandler2: Handler
    private lateinit var mRunnable2: Runnable
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var INTERVAL: Long = 2000
    var FASTEST_INTERVAL: Long = 1000
    private lateinit var mLocationRequest: LocationRequest
    lateinit var mLastLocation: Location
    private var mRef = FirebaseDatabase.getInstance().reference
    private var mUser = FirebaseAuth.getInstance().currentUser
    val TYPEOFPLACE="PLACE"
    val TYPEOFFRIEND="FRIEND"

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mLocationRequest = LocationRequest()
        mHandler = Handler()
        mRunnable = Runnable { startLocationUpdates() }
        mHandler.postDelayed(mRunnable, 1000)

        mHandler1 = Handler()
        mRunnable1 = Runnable { SendeLangt() }
        mHandler1.postDelayed(mRunnable1, 10000)

        mHandler2 = Handler()
        mRunnable2 = Runnable { UpdateLocationsforFriendRmndr() }
        mHandler2.postDelayed(mRunnable2, 3000)

        return Service.START_STICKY

    }

    private fun UpdateLocationsforFriendRmndr() {
        mRef.child("FrienLocationBasedReminder").child(mUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) { }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        for (kullaniciId in p0.children){
                            Log.e("FrienLocationBasedReminder"," "+kullaniciId.key)
                            KullanıcıLocasyonBilgileriniAl(kullaniciId.key) } } } })
        mHandler2.postDelayed(mRunnable2, 3000)}
    private fun KullanıcıLocasyonBilgileriniAl(ID: String?) {
        mRef.child("kullanici").child(ID!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        var kllnci=p0.getValue(NesneKullanici::class.java)
                        var LatOfKullanici=kllnci!!.konumLatitude
                        var LonOfKullanici=kllnci!!.konumLognitude

                        Log.e("bbbbLatOfKullanici",":"+LatOfKullanici)
                        Log.e("bbbbLonOfKullanici",":"+LonOfKullanici)
                        writeLoc(ID,LatOfKullanici,LonOfKullanici) }}})}
    private fun writeLoc(id: String, lat: Double?, lon: Double?) {
        mRef.child("FrienLocationBasedReminder").child(mUser!!.uid).child(id).child("reminderLocationLatitude")
            .setValue(lat)
        mRef.child("FrienLocationBasedReminder").child(mUser!!.uid).child(id).child("reminderLocationLongitude")
            .setValue(lon)
        mRef.child("FrienLocationBasedReminder").child(mUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                for (reminder in p0.children){
                    var rmdr = reminder.getValue(NesneFriendBasedReminder::class.java)
                    var nameOfReminder=rmdr!!.otherUserUname
                    var noteOfReminder=rmdr!!.reminderNote
                    var radOfReminder=rmdr!!.reminderRadius
                    var LatOfReminder=rmdr!!.reminderLocationLatitude
                    var LonOfReminder=rmdr!!.reminderLocationLongitude



                    distance(LatOfReminder!!,LonOfReminder!!, mLastLocation.latitude,mLastLocation.longitude
                        ,radOfReminder,nameOfReminder,noteOfReminder,TYPEOFFRIEND)
                    }}})}

    private fun SendeLangt() {
        UpdateLocation(mLastLocation.latitude, mLastLocation.longitude)
        mHandler1.postDelayed(mRunnable1, 10000)
    }

    private fun startLocationUpdates() {
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,Looper.myLooper())
    }
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        Log.e("BAK1_Lokasyonbilgileri"," Latitude: "+mLastLocation.latitude+"Longitude: "+mLastLocation.longitude)
        mHandler.postDelayed(mRunnable, 1000)

    }
    private fun UpdateLocation(latitude: Double, longitude: Double) {
        mRef.child("kullanici").child(mUser!!.uid).child("konumLatitude").setValue(latitude)
        mRef.child("kullanici").child(mUser!!.uid).child("konumLognitude").setValue(longitude)
        Log.e("BAK2_Lokasyonnnsadnmö0, ",""+latitude+" "+longitude)
        createReminder(latitude,longitude)
    }
    private fun createReminder(latitude: Double, longitude: Double) {
        var userLat=latitude
        var userlong=longitude
        var sorgu = mRef.child("PlaceBasedReminder").child(mUser!!.uid)
        sorgu.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                Log.e("CountMain",": "+p0.childrenCount)
                for (rmndr in p0.children) {
                    var userReminder = rmndr.getValue(NesneLocationBasedReminder::class.java)
                    var reminderLatitude=userReminder!!.reminderLocationLatitude
                    var reminderLongtitude=userReminder!!.reminderLocationLongitude
                    var reminderRadius=userReminder!!.reminderRadius
                    var reminderName=userReminder!!.reminderName
                    var reminderNote =userReminder!!.reminderNote

                    distance(userLat,userlong,reminderLatitude!!,reminderLongtitude!!,reminderRadius,reminderName,reminderNote,TYPEOFPLACE) } } })
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double,
        reminderRadius: Int?, reminderName: String?, reminderNote: String?, TYPE: String): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60.0 * 1.1515 * 1609.34
        if(dist <= reminderRadius!!){ Notification(reminderName,reminderNote,TYPE) }
        return dist
    }
    private fun deg2rad(deg: Double): Double { return deg * Math.PI / 180.0 }
    private fun rad2deg(rad: Double): Double { return rad * 180.0 / Math.PI }
    private fun Notification(reminderName: String?, reminderNote: String?, TYPE: String) {
        if (TYPE=="PLACE"){
            var addNotToDB = NesneReminderNotification()
            addNotToDB.reminderUserID = FirebaseAuth.getInstance().currentUser?.uid
            addNotToDB.reminderName = reminderName
            addNotToDB.reminderNote= reminderNote
            FirebaseDatabase.getInstance().reference
                .child("NotifictionForPlaceReminder")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child(reminderName!!)
                .setValue(addNotToDB).addOnCompleteListener { task ->
                    if (task.isSuccessful) { Log.e("BAKSOON_Loooooooog1 ", "name: " + addNotToDB.reminderName) }
                    else { Log.e("BurdaBiHataVar","Burda") } } }
        if(TYPE=="FRIEND"){
            var addNotToDB = NesneFriendBasedReminder()
            addNotToDB.reminderName = reminderName
            addNotToDB.reminderNote= reminderNote
            FirebaseDatabase.getInstance().reference
                .child("NotifictionForFriendReminder")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child(reminderName!!)
                .setValue(addNotToDB).addOnCompleteListener { task ->
                    if (task.isSuccessful) { Log.e("BAKSOON_Loooooooog1 ", "name: " + addNotToDB.reminderName) }
                    else { Log.e("BurdaBiHataVar","Burda") } }


        }
    }




}