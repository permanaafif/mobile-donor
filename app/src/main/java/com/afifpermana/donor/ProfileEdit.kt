package com.afifpermana.donor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.provider.MediaStore
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar

class ProfileEdit : AppCompatActivity() {

    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileedit)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        val gantiFotoTextView = findViewById<TextView>(R.id.gantifoto)
        gantiFotoTextView.setOnClickListener {
            selectImageFromGallery()
        }

        val buttongantipassword = findViewById<ImageButton>(R.id.btnGantiPassword)
        buttongantipassword.setOnClickListener{
            val intent = Intent(this,GantiPassword::class.java)
            startActivity(intent)
        }

        val dropdownJekel : AutoCompleteTextView =
            findViewById(R.id.jekel)
        val items = listOf("Laki-Laki","Perempuan")
        val adapterDropdown =
            ArrayAdapter(this, R.layout.dropdown_jekel, items)
        dropdownJekel.setAdapter(adapterDropdown)

        dropdownJekel.onItemClickListener =
            AdapterView.OnItemClickListener{
                adapterView, view, i, l ->
                val itemSelected = adapterView.getItemAtPosition(i)

            Toast.makeText(this, "$itemSelected",Toast.LENGTH_LONG).show()
            }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // Update ImageView with the selected image
            val fotoImageView = findViewById<ImageView>(R.id.foto)
            fotoImageView.setImageBitmap(selectedImageBitmap)
        }
    }
}

