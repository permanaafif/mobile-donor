package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.afifpermana.donor.util.SharedPrefLogin

class SplashScreenActivity : AppCompatActivity() {

    lateinit var sharedPref : SharedPrefLogin
    var b: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sharedPref = SharedPrefLogin(this)

        b = intent.extras
        val id_receiver = b?.getString("id_receiver").toString()
        val nama = b?.getString("nama").toString()
        val path = b?.getString("path").toString()

        Log.e("chatsaya","$id_receiver, $nama, $path")
        val Handler = Handler(Looper.getMainLooper())
        Handler.postDelayed({
            if (sharedPref.getStatusWalkTrough() == true){
                val intent = Intent(this,MainActivity::class.java)
                if (id_receiver != "null"){
                    intent.putExtra("id_receiver",id_receiver)
                    intent.putExtra("nama",nama)
                    intent.putExtra("path",path)
                }
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this,WalkThroughActivity::class.java)
                startActivity(intent)
                finish()
            }
        },2000)
    }
}