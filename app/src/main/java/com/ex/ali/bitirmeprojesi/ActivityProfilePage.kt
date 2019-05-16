package com.ex.ali.bitirmeprojesi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile_page.*

class ActivityProfilePage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

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
        readUserInfo()

    }

    private fun readUserInfo() {
        var referance=FirebaseDatabase.getInstance().reference
        var usr=FirebaseAuth.getInstance().currentUser
        //textProfileUserEmail.text=usr?.email


        btnSearch.setOnClickListener{
            var intent=Intent(this@ActivityProfilePage,ActiviySearch::class.java)
            startActivity(intent)
        }
        var sorgu=referance.child("kullanici")
            .orderByKey()
            .equalTo(usr?.uid)
        Toast.makeText(this,""+sorgu,Toast.LENGTH_SHORT).show()
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.e("Budamı",": "+p0.childrenCount)
                for (singleSnapshot in p0.children){
                    var okunanKullanici=singleSnapshot.getValue(NesneKullanici::class.java)

                    textProfilePageName.setText(okunanKullanici?.isim)
                    textProfilePageSurname.setText(okunanKullanici?.soyisim)
                    Log.e("Firebase","Adı: "+okunanKullanici?.isim)

                }
            }
        })
    }
}

