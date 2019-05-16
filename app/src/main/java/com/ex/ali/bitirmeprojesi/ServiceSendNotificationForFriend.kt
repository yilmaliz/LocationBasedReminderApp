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

class ServiceSendNotificationForFriend : Service() {

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
        var sorgu = mRef.child("NotifictionForFriendReminder").child(mUser!!.uid)
        sorgu.addChildEventListener(object : ValueEventListener, ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.e("ChildMoved","sda")}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.e("ChildChanged","sda")}
            override fun onChildRemoved(p0: DataSnapshot) {
                Log.e("ChildRemoved","sda")}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if (p0.exists()){
                    var NotificationName=p0.child("reminderName").value.toString()
                    var NotificationNote=p0.child("reminderNote").value.toString()

                    Log.e("BununOlmasıLazımSuan",": "+p0.key)
                    sendNotifiction(NotificationName,NotificationNote)
                    Log.e("ACABABUNUKAC","KEREATIYOR") } }
            override fun onDataChange(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {} }) }

    private fun sendNotifiction(notificationName: String?, notificationNote: String?) {
        var manager: NotificationManager? = null
        val CHANNEL_ID = "Channel_ID"
        val CHANNEL_NAME = "Channel_Name"
        val NOTIFICATION_ID = 2
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(true)
        manager!!.createNotificationChannel(channel)
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationName+" isimli kullanıcı yakınlarında. ")
            .setContentText("Yapman Gerekenler: "+notificationNote)
            .setSmallIcon(R.drawable.ic_icon_for_noti)
            .setAutoCancel(true)
            .build()
        manager!!.notify(NOTIFICATION_ID, notification)
        //mRef.child("NotifictionForFriendReminder").child(mUser!!.uid).child(notificationName!!).removeValue()
        removeNotFromDB(notificationName!!)
    }

    private fun removeNotFromDB(notificationName: String) {
        mRef.child("FrienLocationBasedReminder").child(mUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        for (rmnd in p0.children){
                            var name=rmnd.child("otherUserUname").value
                            var id = rmnd.child("otherUserID").value.toString()
                            if (name==notificationName){
                                mRef.child("FrienLocationBasedReminder").child(mUser!!.uid).child(id)
                            }else{
                                Log.e("AAAADOGRUDEĞl",": "+name+" İD: "+id)
                            }

                        }
                    }
                }
            })
    }
}