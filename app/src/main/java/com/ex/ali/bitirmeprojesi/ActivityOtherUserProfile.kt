package com.ex.ali.bitirmeprojesi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_other_user_profile.*

class ActivityOtherUserProfile : AppCompatActivity() {
    lateinit var secilenUserID: String
    lateinit var mRef: DatabaseReference
    var mUser = FirebaseAuth.getInstance().currentUser
    var ThisName : String?=null
    var ThisUserName : String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        secilenUserID = intent.getStringExtra("SecilenKullaniciID")
        mRef = FirebaseDatabase.getInstance().reference


        Log.e("Hata", "Other User Page")
        kullanıcıSayfasıOlustur()

        btnAddFriend.setOnClickListener {
            Log.e("NAME==="," s"+ThisName)

            if (mUser != null) {
                mRef.child("Friendship").child(mUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChild(secilenUserID)) {
                            mRef.child("Friendship").child(mUser!!.uid).child(ThisUserName.toString()).removeValue()
                            //mRef.child("Friendship").child(secilenUserID).child(mUser!!.uid).removeValue()
                            ekleButonu()

                        } else {
                            /*mRef.child("Arkaslik_istekleri").child(secilenUserID).child(mUser!!.uid).setValue(mUser!!.uid)
                            IstekGonderildiButonu()*/

                            mRef.child("Friendship").child(mUser!!.uid).child(ThisUserName.toString()).child(secilenUserID).setValue(secilenUserID)
                            //mRef.child("Friendship").child(secilenUserID).child(mUser!!.uid).setValue(secilenUserID)
                            cikarButonu()
                        }
                    }

                })
            }

        }
    }

    private fun kullanıcıSayfasıOlustur() {
        mRef.child("kullanici").child(secilenUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.getValue() != null) {
                    var secilenKullaniciBilgileri = p0.getValue(NesneKullanici::class.java)
                    textOtherName.setText(secilenKullaniciBilgileri?.uName)
                    textOtherUserEmail.setText(secilenKullaniciBilgileri?.mail)
                    textOtherUserID.setText(secilenKullaniciBilgileri?.ID)
                    textOtherUserName.setText(secilenKullaniciBilgileri?.isim)

                    Add(secilenKullaniciBilgileri?.isim,secilenKullaniciBilgileri?.uName)

                }


                takipBilgisi()

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun Add(isim: String?, uName: String?) {
        ThisUserName=uName
        ThisName=isim

    }

    private fun takipBilgisi() {


        mRef.child("Friendship").child(mUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(secilenUserID)) {
                    cikarButonu()
                } else {
                    ekleButonu()
                }
            }
        })
    }

    private fun ekleButonu() {
        btnAddFriend.setText("Ekle")
    }

    private fun cikarButonu() {
        btnAddFriend.setText("Çıkar")
    }
    private fun IstekGonderildiButonu() {
        btnAddFriend.setText("İstek Gönderildi")
    }


}
