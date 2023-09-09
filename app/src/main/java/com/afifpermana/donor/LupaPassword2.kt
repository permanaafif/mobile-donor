package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class LupaPassword2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupapassword2)

        val password = findViewById<EditText>(R.id.passwordbaru)
        val confirmpassword = findViewById<EditText>(R.id.passwordbaru1)

        val ganti = findViewById<Button>(R.id.btnUbah)
        ganti.setOnClickListener{
            val passwordText = password.text.toString()
            val passwordText1 = confirmpassword.text.toString()
            if (passwordText != passwordText1){
                Toast.makeText(this, "Password yang dimasukkan tidak sama!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Sukses!", Toast.LENGTH_SHORT).show();
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
    }

}