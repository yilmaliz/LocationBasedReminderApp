package com.ex.ali.bitirmeprojesi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnSave.setOnClickListener {

            if(textRegisterGmail.text.isNotEmpty()&& textRegisterPass.text.isNotEmpty() &&  textRegisterPassAgain.text.isNotEmpty()
                && textRegisterName.text.isNotEmpty()&& textRegisterSurname.text.isNotEmpty()&& textRegisterUserName.text.isNotEmpty()){

                if (textRegisterPass.text.toString().equals(textRegisterPassAgain.text.toString())){
                    
                    registerNewMember(textRegisterGmail.text.toString(),textRegisterPass.text.toString())// New Function to save new NesneKullanici.

                }else{
                    Toast.makeText(this,"Passwords must be same",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this,"Must fill all blanks.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerNewMember(mail: String,password: String) {

        progresBarBir() // activate the progressbar

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,password)
            .addOnCompleteListener(object:OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {

                    if(p0.isSuccessful){


                        var goBackToLogin=Intent(this@ActivityRegister,ActivityLogin::class.java)
                        startActivity(goBackToLogin)

                        onayMaili()

                        var addUserToDataBase=NesneKullanici()
                        //addUserToDataBase.isim=textGmail.text.toString().substring(0,textGmail.text.toString().indexOf("@"))
                            // üstteki kod mail adresinde sıfırıncı elemandan '@' işaretine kadar olan alanı alır.

                        addUserToDataBase.ID=FirebaseAuth.getInstance().currentUser?.uid
                        addUserToDataBase.isim=textRegisterName.text.toString()
                        addUserToDataBase.mail=textRegisterGmail.text.toString()
                        addUserToDataBase.soyisim=textRegisterSurname.text.toString()
                        addUserToDataBase.konumLatitude=0.0
                        addUserToDataBase.konumLognitude=0.0
                        addUserToDataBase.uName=textRegisterUserName.text.toString()

                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            //.child(textRegisterUserName.text.toString())
                            .setValue(addUserToDataBase).addOnCompleteListener{task ->
                                if (task.isSuccessful){

                                    Toast.makeText(this@ActivityRegister,"User has saved",Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    logineGonder()
                                  }
                           }
                    }else{

                        Toast.makeText(this@ActivityRegister,"There is a trouble while new user saving. "+p0.exception?.message,Toast.LENGTH_SHORT).show()
                    }

                }
            })

        progresBarSifir()// disactivate the progressbar

    }

    private fun logineGonder() {
        var logineDon=Intent(this@ActivityRegister,ActivityLogin::class.java)
        startActivity(logineDon)
    }


    private fun progresBarBir(){
        progressBarRegister.visibility=View.VISIBLE
    }
    private fun progresBarSifir(){
        progressBarRegister.visibility=View.INVISIBLE
    }

    private fun onayMaili() {



        var kullanici=FirebaseAuth.getInstance().currentUser

        if (kullanici!=null){


            kullanici.sendEmailVerification()

                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful){
                            Toast.makeText(this@ActivityRegister,"Confirm your e-mail",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@ActivityRegister,"ERROR"+p0.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }
}
