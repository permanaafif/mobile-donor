package com.afifpermana.donor

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.provider.MediaStore
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileEdit : AppCompatActivity() {
    var b : Bundle? = null
    private lateinit var fotoProfile : CircleImageView
    private lateinit var namaUser : TextView
    private lateinit var alamat : TextView
    private lateinit var kontak : TextView
    private lateinit var goldar : TextView
    private lateinit var berat_badan : TextView
    private lateinit var jenis_kelamin : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileedit)
        b = intent.extras
        fotoProfile = findViewById(R.id.foto)
        namaUser = findViewById(R.id.namauser)
        alamat = findViewById(R.id.alamat)

        kontak = findViewById(R.id.notelp)
        goldar = findViewById(R.id.goldar)
        berat_badan = findViewById(R.id.bb)

        Picasso.get().load(b!!.getString("gambar")).into(fotoProfile)
        namaUser.text = b!!.getString("nama")
        alamat.text = b!!.getString("alamat")
        kontak.text = b!!.getString("kontak")
        jenis_kelamin = b!!.getString("jenis_kelamin").toString()
        goldar.text = b!!.getString("goldar")
        berat_badan.text = b!!.getString("berat_badan")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        val gantiFotoTextView = findViewById<TextView>(R.id.gantifoto)
        gantiFotoTextView.setOnClickListener {

        }

        val buttongantipassword = findViewById<TextView>(R.id.password)
        buttongantipassword.setOnClickListener{
            val intent = Intent(this,GantiPassword::class.java)
            startActivity(intent)
        }

        val dropdownJekel : AutoCompleteTextView =
            findViewById(R.id.jekel)
        val itemsJekel = listOf("Laki-laki","Perempuan")

        val position = itemsJekel.indexOf(jenis_kelamin)
        if (position != -1) {
            dropdownJekel.setText(itemsJekel[position], false)
        }
        val adapterDropdownJekel =
            ArrayAdapter(this, R.layout.dropdown, itemsJekel)
        dropdownJekel.setAdapter(adapterDropdownJekel)

        dropdownJekel.onItemClickListener =
            AdapterView.OnItemClickListener{
                    adapterView, view, i, l ->
                val itemSelected = adapterView.getItemAtPosition(i)

                Toast.makeText(this, "$itemSelected",Toast.LENGTH_LONG).show()
            }

        goldar.setOnClickListener {
            Toast.makeText(this,"Tidak Boleh Tukar Golongan Darah", Toast.LENGTH_LONG).show() }
    }
}

