package com.afifpermana.donor

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.model.UpdateProfileEditDataResponse
import com.afifpermana.donor.model.UpdateProfileRequestData
import com.afifpermana.donor.service.ProfileAPI
import com.afifpermana.donor.service.UpdateProfileAPI
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
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
    private lateinit var loadingProgressBar : ProgressBar

    private var selectedImageUri : Uri? = null

    companion object{
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102
    }
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
        loadingProgressBar=findViewById(R.id.loadingProgressBar)

        Picasso.get().load(b!!.getString("gambar")).into(fotoProfile)
        namaUser.text = b!!.getString("nama")
        email.text = b!!.getString("email")
        tanggal_lahir.text = b!!.getString("tanggal_lahir")
        alamat.text = b!!.getString("alamat")
        kontak.text = b!!.getString("kontak")
        jenis_kelamin = b!!.getString("jenis_kelamin").toString()
        berat_badan.text = b!!.getString("berat_badan").toString().replace(" Kg","")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        val gantiFotoTextView = findViewById<TextView>(R.id.gantifoto)
        gantiFotoTextView.setOnClickListener {
//            getImage()
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
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

        // Tombol simpan diklik
        btn_simpan.setOnClickListener {
            if (validateInput()) {
                // Validasi sukses, lanjutkan dengan tindakan penyimpanan data
                val connectivityChecker = ConnectivityChecker(this)
                if (connectivityChecker.isNetworkAvailable()){
                    //koneksi aktif
                    loadingProgressBar.visibility = View.VISIBLE
                    btn_simpan.visibility = View.GONE
                    doSimpanGambar()
                    doSimpanData()
                }else{
                    //koneksi tidak aktif
                    connectivityChecker.showAlertDialogNoConnection()
                }

            }
            val permissions = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE_PERMISSIONS
                )
            }

        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
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
                    Toast.makeText(applicationContext,"Simpan data Berhasil",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    email.error = "Email Sudah Digunakan"
                    loadingProgressBar.visibility = View.GONE
                    btn_simpan.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<UpdateProfileEditDataResponse>, t: Throwable) {
                Log.e("doSmipanData", t.message.toString())
                Toast.makeText(applicationContext,"Email Sudah Digunakan",Toast.LENGTH_SHORT).show()
                email.error = "Email Sudah Digunakan"
                loadingProgressBar.visibility = View.GONE
                btn_simpan.visibility = View.VISIBLE
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
                Toast.makeText(this, "Gambar profile tidak tersimpan", Toast.LENGTH_SHORT).show()
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


//    private fun getImage() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, 100)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == 100){
//            selectedImageUri = data?.data
//            fotoProfile.setImageURI(selectedImageUri)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
//            Log.e("foto_profile_uri",uri.toString())
            if (uri != null) {
                Crop.of(uri, Uri.fromFile(File(cacheDir, "cropped")))
                    .asSquare()
                    .start(this)
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            val croppedUri = Crop.getOutput(data)
            if ( croppedUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, croppedUri)
                val uri_gambar = bitmapToUri(bitmap)
                selectedImageUri = uri_gambar
//                Log.e("foto_profile_urigambar",uri_gambar.toString())
//                Log.e("foto_profile",croppedUri.toString())
//                Log.e("foto_profile_bitmap",bitmap.toString())
                fotoProfile.setImageURI(selectedImageUri)
//                imageView.setImageBitmap(bitmap)
//                saveImageToGallery(bitmap)
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
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


