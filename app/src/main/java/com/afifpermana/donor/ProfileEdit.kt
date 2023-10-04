package com.afifpermana.donor

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileEdit : AppCompatActivity(), UploadRequestBody.UploadCallback {
    var b : Bundle? = null
    private lateinit var fotoProfile : CircleImageView
    private lateinit var namaUser : TextView
    private lateinit var email : TextView
    private lateinit var alamat : TextView
    private lateinit var tanggal_lahir : TextView
    private lateinit var kontak : TextView
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
        email = findViewById(R.id.email)
        alamat = findViewById(R.id.alamat)
        tanggal_lahir = findViewById(R.id.tanggal_lahir)

        kontak = findViewById(R.id.notelp)
        berat_badan = findViewById(R.id.bb)
        btn_simpan = findViewById(R.id.btn_simpan)
        progressBar = findViewById(R.id.progress_bar)

        Picasso.get().load(b!!.getString("gambar")).into(fotoProfile)
        namaUser.text = b!!.getString("nama")
        email.text = b!!.getString("email")
        tanggal_lahir.text = b!!.getString("tanggal_lahir")
        alamat.text = b!!.getString("alamat")
        kontak.text = b!!.getString("kontak")
        jenis_kelamin = b!!.getString("jenis_kelamin").toString()
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

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalender)
        }

        tanggal_lahir.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)).show()
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
                jenis_kelamin = itemSelected.toString().lowercase()

                Toast.makeText(this, "$itemSelected",Toast.LENGTH_LONG).show()
            }

        // Validasi input
        fun validateInput(): Boolean {
            val nama = namaUser.text.toString().trim()
            val almt = alamat.text.toString().trim()
            val kt = kontak.text.toString().trim()
            val bb = berat_badan.text.toString().trim()
            val em = email.text.toString().trim()

            if (nama.isEmpty()) {
                namaUser.error = "Nama tidak boleh kosong"
                return false
            }
            if (nama.length < 3) {
                namaUser.error = "Nama harus lebih dari 3 karakter"
                return false
            }
            if (almt.isEmpty()) {
                alamat.error = "Alamat tidak boleh kosong"
                return false
            }
            if (almt.length < 5) {
                alamat.error = "Alamat harus lebih dari 5 karakter"
                return false
            }
            if (kt.isEmpty()) {
                kontak.error = "Kontak tidak boleh kosong"
                return false
            }
            if (kt.length < 11) {
                kontak.error = "kontak harus lebih dari 10 karakter"
                return false
            }
            if (bb.isEmpty()) {
                berat_badan.error = "Berat Badan tidak boleh kosong"
                return false
            }
            // Validasi berat badan harus berupa angka
            val beratBadanInt = bb.toIntOrNull()
            if (beratBadanInt == null) {
                berat_badan.error = "Berat Badan harus berupa angka"
                return false
            }
            // Validasi kontak (alamat email) harus valid
            if (!isValidEmail(em)) {
                email.error = "Alamat Email tidak valid"
                return false
            }

            return true
        }

        // Tombol simpan diklik
        btn_simpan.setOnClickListener {
            if (validateInput()) {
                // Validasi sukses, lanjutkan dengan tindakan penyimpanan data
                doSimpanGambar()
                doSimpanData()
            }
        }

    }

    // Fungsi untuk memeriksa apakah sebuah string adalah alamat email yang valid
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun updateLable(myCalender: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        tanggal_lahir.setText(sdf.format(myCalender.time))
    }

    private fun doSimpanData() {
        val data = UpdateProfileRequestData()
        data.nama = namaUser.text.toString().trim()
        data.email = email.text.toString().trim()
        data.tanggal_lahir = tanggal_lahir.text.toString().trim()
        data.alamat_pendonor = alamat.text.toString().trim()
        data.jenis_kelamin = jenis_kelamin
        data.kontak_pendonor = kontak.text.toString().trim()
        data.berat_badan = berat_badan.text.toString().toInt()
        Log.e("doSmipanData", data.nama.toString())
        val retro = Retro().getRetroClientInstance().create(UpdateProfileAPI::class.java)
        retro.updateProfileData("Bearer ${sharedPref.getString("token")}",data).enqueue(object : Callback<UpdateProfileEditDataResponse> {
            override fun onResponse(
                call: Call<UpdateProfileEditDataResponse>,
                response: Response<UpdateProfileEditDataResponse>
            ) {
                val resCode = response.code()
//                Log.e("emaill",resCode.toString())
                if (resCode == 200){
                    Toast.makeText(applicationContext,"Simpan Berhasil",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<UpdateProfileEditDataResponse>, t: Throwable) {
                Log.e("doSmipanData", t.message.toString())
                email.error = "Email Sudah Digunakan"
            }
        })
    }

    private fun doSimpanGambar() {
        if (selectedImageUri != null) {
            val fileSize = getFileSize(selectedImageUri!!)
            val maxSizeInBytes = 1 * 1024 * 1024 // Ukuran maksimum 1 MB

            if (fileSize > maxSizeInBytes) {
                // Ukuran gambar terlalu besar
                Toast.makeText(this, "Ukuran gambar terlalu besar, Max 1MB", Toast.LENGTH_SHORT).show()
                return
            }

            // Lanjutkan dengan mengunggah gambar jika ukurannya sesuai
            val parcelFileDescriptor = contentResolver.openFileDescriptor(
                selectedImageUri!!, "r", null
            ) ?: return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            progressBar.visibility = View.VISIBLE
            progressBar.progress = 0
            val gambar = UploadRequestBody(file, "image", this)

            val retro = Retro().getRetroClientInstance().create(UpdateProfileAPI::class.java)
            retro.updateProfileGambar(
                "Bearer ${sharedPref.getString("token")}",
                MultipartBody.Part.createFormData("gambar", file.name, gambar)
            ).enqueue(object :
                Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        progressBar.progress = 100
                        Toast.makeText(applicationContext, "Simpan Gambar Berhasil", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("nonSuccess", "non succcess")
                    }

                    if (progressBar.progress == 100) {
                        progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("doSimpanGambar", t.message.toString())
                }
            })
        }
    }

    // Fungsi untuk mendapatkan ukuran file dari Uri
    private fun getFileSize(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                return it.getLong(sizeIndex)
            }
        }
        return 0
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


