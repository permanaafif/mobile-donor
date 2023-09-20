package com.afifpermana.donor

import android.app.Activity
import android.content.ContentResolver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.model.UpdateProfileEditDataResponse
import com.afifpermana.donor.model.UpdateProfileRequestData
import com.afifpermana.donor.service.ProfileAPI
import com.afifpermana.donor.service.UpdateProfileAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ProfileEdit : AppCompatActivity(), UploadRequestBody.UploadCallback {
    var b : Bundle? = null
    private lateinit var fotoProfile : CircleImageView
    private lateinit var namaUser : TextView
    private lateinit var alamat : TextView
    private lateinit var kontak : TextView
    private lateinit var goldar : TextView
    private lateinit var berat_badan : TextView
    private lateinit var jenis_kelamin : String
    private lateinit var btn_simpan : Button
    private lateinit var progressBar : ProgressBar
    private lateinit var sharedPref: SharedPrefLogin

    private var selectedImageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPrefLogin(this)
        setContentView(R.layout.activity_profileedit)
        b = intent.extras
        fotoProfile = findViewById(R.id.foto)
        namaUser = findViewById(R.id.namauser)
        alamat = findViewById(R.id.alamat)

        kontak = findViewById(R.id.notelp)
        goldar = findViewById(R.id.goldar)
        berat_badan = findViewById(R.id.bb)
        btn_simpan = findViewById(R.id.btn_simpan)
        progressBar = findViewById(R.id.progress_bar)

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
            getImage()
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

        btn_simpan.setOnClickListener {
            doSimpanGambar()
            doSimpanData()
        }
    }

    private fun doSimpanData() {
        val data = UpdateProfileRequestData()
        data.nama = namaUser.text.toString()
        Log.e("doSmipanData", data.nama.toString())
        val retro = Retro().getRetroClientInstance().create(UpdateProfileAPI::class.java)
        retro.updateProfileData("Bearer ${sharedPref.getString("token")}",data).enqueue(object : Callback<UpdateProfileEditDataResponse> {
            override fun onResponse(
                call: Call<UpdateProfileEditDataResponse>,
                response: Response<UpdateProfileEditDataResponse>
            ) {
                if (response.isSuccessful){
                    Log.e("doSmipanData", "berhasil")
                }
            }

            override fun onFailure(call: Call<UpdateProfileEditDataResponse>, t: Throwable) {
                Log.e("doSmipanData", t.message.toString())
            }
        })
    }

    private fun doSimpanGambar() {
        if (selectedImageUri != null){
            val parcelFileDescriptor = contentResolver.openFileDescriptor(
                selectedImageUri!!, "r",null)?:return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir,contentResolver.getFileName(selectedImageUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            progressBar.progress = 0
            val gambar = UploadRequestBody(file,"image",this)

            val retro = Retro().getRetroClientInstance().create(UpdateProfileAPI::class.java)
            retro.updateProfileGambar("Bearer ${sharedPref.getString("token")}",
                MultipartBody.Part.createFormData("gambar",file.name,gambar)
            ).enqueue(object :
                Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful){
//                        Toast.makeText(applicationContext,"${response.body()?.succes}", Toast.LENGTH_LONG).show()
//                    finish()
                    }else{
                        Log.e("nonSuccess", "non succcess")
                    }
                    progressBar.progress = 100
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {

                }
            })
        }
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            selectedImageUri = data?.data
            fotoProfile.setImageURI(selectedImageUri)
        }
    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {
        var name =""
        val returnCursor = this.query(fileUri,null,null,null,null)
        if (returnCursor != null){
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    override fun onProgressUpdate(percentage: Int) {
        progressBar.progress = percentage
    }
}


