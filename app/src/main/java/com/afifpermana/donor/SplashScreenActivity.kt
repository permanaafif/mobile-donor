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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sharedPref = SharedPrefLogin(this)

        val Handler = Handler(Looper.getMainLooper())
        Handler.postDelayed({
            if (sharedPref.getStatusWalkTrough() == true){
                val intent = Intent(this,MainActivity::class.java)
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