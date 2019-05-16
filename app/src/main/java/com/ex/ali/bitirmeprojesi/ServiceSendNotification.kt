package com.ex.ali.bitirmeprojesi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ServiceSendNotification : Service() {
    private var mRef = FirebaseDatabase.getInstance().reference
    private var mUser = FirebaseAuth.getInstance().currentUser

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        listenerForChildofPlace()
        return Service.START_STICKY
    }
    private fun listenerForChildofPlace() {
        var sorgu = mRef.child("NotifictionForPlaceReminder").child(mUser!!.uid)
        sorgu.addChildEventListener(object : ValueEventListener, ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {Log.e("ChildMoved","sda")}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {Log.e("ChildChanged","sda")}
            override fun onChildRemoved(p0: DataSnapshot) {Log.e("ChildRemoved","sda")}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if (p0.exists()){
                    var NotificationName=p0.child("reminderName").value.toString()
                    var NotificationNote=p0.child("reminderNote").value.toString()
                    
                    sendNotifiction(NotificationName,NotificationNote)
                    Log.e("ACABABUNUKAC","KEREATIYOR") } }
            override fun onDataChange(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {} }) }

    private fun sendNotifiction(notificationName: String?, notificationNote: String?) {
        var manager: NotificationManager? = null
        val CHANNEL_ID = "Channel_ID"
        val CHANNEL_NAME = "Channel_Name"
        val NOTIFICATION_ID = 1
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(true)
        manager!!.createNotificationChannel(channel)
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Hatırlaman Gereken birşey var: "+notificationName)
            .setContentText("Bunları yapman gerekiyor: "+notificationNote)
            .setSmallIcon(R.drawable.ic_icon_for_noti)
            .setAutoCancel(true)
            .build()
        manager!!.notify(NOTIFICATION_ID, notification)
        Log.e("NotifiationForPlace--","Name: "+notificationName)
        mRef.child("PlaceBasedReminder").child(mUser!!.uid).child(notificationName!!).removeValue()
        mRef.child("NotifictionForPlaceReminder").child(mUser!!.uid).child(notificationName!!).removeValue()
    }
}
