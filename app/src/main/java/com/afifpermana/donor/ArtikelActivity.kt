package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class ArtikelActivity : AppCompatActivity() {

    var b : Bundle? = null
    private lateinit var gambar : ImageView
    private lateinit var judul : TextView
    private lateinit var deskripsi : TextView
    private lateinit var tanggal : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artikel)
        b = intent.extras

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        initView()
    }

    private fun initView() {
        gambar = findViewById(R.id.gambar_artikel)
        judul = findViewById(R.id.judul_artikel)
        tanggal = findViewById(R.id.tanggal_artikel)
        deskripsi = findViewById(R.id.deskripsi_artikel)

        gambar.setImageResource(b!!.getInt("gambar"))
        judul.text = b!!.getString("judul")
        tanggal.text = b!!.getString("tanggal")
        deskripsi.text = b!!.getString("deskripsi")
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
}