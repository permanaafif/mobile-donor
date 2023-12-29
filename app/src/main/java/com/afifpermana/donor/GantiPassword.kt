package com.afifpermana.donor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afifpermana.donor.model.GantiPasswordRequest
import com.afifpermana.donor.model.GantiPasswordResponse
import com.afifpermana.donor.service.PendonorLoginAPI
import com.afifpermana.donor.service.UpdateProfileAPI
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.example.belajarapi.model.PendonorLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPassword : AppCompatActivity() {
    private lateinit var password_lama : EditText
    private lateinit var password_baru : EditText
    private lateinit var confirm_password_baru : EditText
    private lateinit var btn_simpan : Button
    lateinit var sharedPref: SharedPrefLogin
    private lateinit var loadingProgressBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPrefLogin(this)
        setContentView(R.layout.activity_gantipassword)
        password_lama = findViewById(R.id.password_lama)
        password_baru = findViewById(R.id.password_baru)
        confirm_password_baru = findViewById(R.id.confirm_password_baru)
        btn_simpan = findViewById(R.id.btn_simpan)
        loadingProgressBar=findViewById(R.id.loadingProgressBar)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        btn_simpan.setOnClickListener {
            if (validationInput()){
                val connectivityChecker = ConnectivityChecker(this)
                if (connectivityChecker.isNetworkAvailable()){
                    //koneksi aktif
                    gantiPassword()
                }else{
                    //koneksi tidak aktif
                    connectivityChecker.showAlertDialogNoConnection()
                }
            }
        }
    }

    private fun gantiPassword() {
        loadingProgressBar.visibility = View.VISIBLE
        btn_simpan.visibility = View.GONE
        val data = GantiPasswordRequest()
        data.password_lama = password_lama.text.toString().trim()
        data.password_baru = password_baru.text.toString().trim()
        val retro = Retro().getRetroClientInstance().create(UpdateProfileAPI::class.java)
        retro.gantiPassword("Bearer ${sharedPref.getString("token")}",data).enqueue(object : Callback<GantiPasswordResponse> {
            override fun onResponse(
                call: Call<GantiPasswordResponse>,
                response: Response<GantiPasswordResponse>
            ) {
                if (response.isSuccessful){
                    if(response.body()!!.succes == true){
                        Toast.makeText(applicationContext, response.body()!!.message.toString(), Toast.LENGTH_LONG).show()
                        finish()
                    }else{
                        // Jika status false, tampilkan pesan kesalahan
                        showAlertDialog("Ganti Password Gagal, Password lama anda salah!!")
                        loadingProgressBar.visibility = View.GONE
                        btn_simpan.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<GantiPasswordResponse>, t: Throwable) {

            }
        })
    }

    private fun validationInput(): Boolean {
        if (password_lama.text.toString().trim().isEmpty()) {
            password_lama.error = "Passwword lama tidak boleh kosong"
            return false
        }
        if (password_baru.text.toString().trim().isEmpty()) {
            password_baru.error = "Password baru tidak boleh kosong"
            return false
        }
        if (confirm_password_baru.text.toString().trim().isEmpty()) {
            confirm_password_baru.error = "confirm password baru tidak boleh kosong"
            return false
        }

        if (password_baru.text.toString().trim() != confirm_password_baru.text.toString().trim()){
            confirm_password_baru.error = "confirm password baru salah"
            return false
        }
        if (password_baru.text.toString().trim().length < 5) {
            password_baru.error = "Password baru hasus lebih dari 5 karakter"
            return false
        }
        return true
    }

    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val customeView = LayoutInflater.from(this).inflate(R.layout.alert_gagal,null)
        builder.setView(customeView)
        val dialog = builder.create()

        val tv_alert = customeView.findViewById<TextView>(R.id.tv_alert_gagal)
        tv_alert.text = message
        val btnOke = customeView.findViewById<Button>(R.id.btn_oke)

        btnOke.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}