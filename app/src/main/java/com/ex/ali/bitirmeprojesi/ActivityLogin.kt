package com.ex.ali.bitirmeprojesi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
class ActivityLogin : AppCompatActivity() {

    lateinit var myListener:FirebaseAuth.AuthStateListener // Mail onaylanma durumunu süekli olarak kontrol etmesi için
    //Burada herhangi bir atama oladığından dolayı lateinit.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initMyListener()

        textRegisterLink.setOnClickListener{
            var intentRegister=Intent(this,ActivityRegister::class.java)
            startActivity(intentRegister)



        }

        btnLogin.setOnClickListener{

            if (textMail.text.isNotEmpty()&&textRegisterPass.text.isNotEmpty()){

                progresBarBir()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(textMail.text.toString(),textRegisterPass.text.toString())

                    .addOnCompleteListener(object:OnCompleteListener<AuthResult>{
                        override fun onComplete(p0: Task<AuthResult>) {
                            if(p0.isSuccessful){
                                progresBarSifir()
                            }else{
                                Toast.makeText(this@ActivityLogin,"Ansuccesfully: "+p0.exception?.message,Toast.LENGTH_SHORT).show()
                                Log.e("Login","Error",p0.exception)
                                progresBarSifir()
                            }
                        }
                    })
            }else{
                Toast.makeText(this@ActivityLogin,"Fill the blanks ",Toast.LENGTH_SHORT).show()
                progresBarSifir()
            }
        }
    }
    private fun progresBarBir(){
        progressBarLogin.visibility= View.VISIBLE
    }
    private fun progresBarSifir(){
        progressBarLogin.visibility= View.INVISIBLE
    }
    private fun initMyListener(){
        myListener= object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici=p0.currentUser
                if (kullanici != null){
                    if (kullanici.isEmailVerified){
                        var loginTheSystem=Intent(this@ActivityLogin,MainActivity::class.java)
                        startActivity(loginTheSystem)
                        finish()
                    }else{
                        Toast.makeText(this@ActivityLogin,"Varify your mail ",Toast.LENGTH_SHORT).show()
                    }
                    progresBarSifir()
                }
            }
        }

    }
    override fun onStart(){
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myListener)
    }
    override fun onStop(){
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(myListener)
    }
}