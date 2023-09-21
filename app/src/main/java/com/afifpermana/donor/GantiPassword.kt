package com.afifpermana.donor

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afifpermana.donor.model.GantiPasswordRequest
import com.afifpermana.donor.model.GantiPasswordResponse
import com.afifpermana.donor.service.PendonorLoginAPI
import com.afifpermana.donor.service.UpdateProfileAPI
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPrefLogin(this)
        setContentView(R.layout.activity_gantipassword)
        password_lama = findViewById(R.id.password_lama)
        password_baru = findViewById(R.id.password_baru)
        confirm_password_baru = findViewById(R.id.confirm_password_baru)
        btn_simpan = findViewById(R.id.btn_simpan)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        btn_simpan.setOnClickListener {
            if (validationInput()){
                gantiPassword()
            }
        }
    }

    private fun gantiPassword() {
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
                        showAlertDialog("Ganti Password Gagal", response.body()!!.message.toString())
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

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this@GantiPassword)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            // Tindakan setelah pengguna menekan tombol OK
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}