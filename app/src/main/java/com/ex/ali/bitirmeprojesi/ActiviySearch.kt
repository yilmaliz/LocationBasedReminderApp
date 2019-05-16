package com.ex.ali.bitirmeprojesi
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.ui.utils.ItemClickSupport
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_search_activiy.*
class ActiviySearch : AppCompatActivity() {
    val ALGOLIA_APP_ID = "SQDIZP0V33"
    val ALGOLIA_SEARCH_API_KEY = "ec37ffcc8dc687bc528a50bb9912d41c"
    val ALGOLIA_INDEX_NAME = "Location_Based_Reminder_App"
    lateinit var searcher: Searcher

    lateinit var mAuth : FirebaseAuth
    lateinit var mAuthListener : FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_activiy)

        setUpListener()
        mAuth=FirebaseAuth.getInstance()
        setUpAlgolia()
    }

    private fun setUpListener() {
        mAuthListener=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var user=FirebaseAuth.getInstance().currentUser

                if(user==null){
                    Log.e("HATA","Kullanıcı oturum açmamış")
                    var intent=Intent(this@ActiviySearch,ActivityLogin::class.java)
                    startActivity(intent)
                    finish()
                }else{
                }
            }
        }
    }
    private fun setUpAlgolia() {
        Log.e("USER NULL DEGİL","sads")
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        val helper = InstantSearch(this, searcher)
        //helper.search()
        hits.setOnItemClickListener(object:ItemClickSupport.OnItemClickListener{
            override fun onItemClick(recyclerView: RecyclerView?, position: Int, v: View?) {
                var secilenKullaniciID=hits.get(position).getString("id")
                Toast.makeText(this@ActiviySearch,"secilen ID:"+secilenKullaniciID,Toast.LENGTH_SHORT).show()
                if(secilenKullaniciID.equals(mAuth.currentUser?.uid.toString())){
                    var intent=Intent(this@ActiviySearch,ActivityProfilePage::class.java)
                    startActivity(intent)
                }else{
                    var intentElse=Intent(this@ActiviySearch,ActivityOtherUserProfile::class.java)
                    intentElse.putExtra("SecilenKullaniciID",secilenKullaniciID)
                    startActivity(intentElse)
                }
            }
        })
    }
    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }
    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }
    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }
}