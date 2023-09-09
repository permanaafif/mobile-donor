package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val info=findViewById<Button>(R.id.tentangdara)
        val lupa=findViewById<Button>(R.id.lupapassword)
        val login=findViewById<Button>(R.id.btnLogin)

        info.setOnClickListener{
            val intent = Intent(this,TentangDaraActivity::class.java)
            startActivity(intent)
        }

        lupa.setOnClickListener{
            val intent = Intent(this, LupaPassword::class.java)
            startActivity(intent)
        }

        login.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}