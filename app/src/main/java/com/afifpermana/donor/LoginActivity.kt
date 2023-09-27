package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.afifpermana.donor.service.PendonorLoginAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.example.belajarapi.model.PendonorLoginRequest
import com.example.belajarapi.model.PendonorLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var kodeDonor : EditText
    private lateinit var password : EditText
    lateinit var sharedPref : SharedPrefLogin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = SharedPrefLogin(this)
        kodeDonor=findViewById(R.id.kodedonor)
        password=findViewById(R.id.password)
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
            if (validateInput()){
                loginPendonor()
            }
        }
    }
    // Validasi input
    public fun validateInput(): Boolean {
        if (kodeDonor.text.trim().isNullOrEmpty()) {
            kodeDonor.error = "Kode Pendonor tidak boleh kosong"
            return false
        }
        if (password.text.trim().isNullOrEmpty()) {
            password.error = "Password tidak boleh kosong"
            return false
        }
        return true
    }

    private fun loginPendonor() {
        var form = PendonorLoginRequest()
        form.kode_pendonor = kodeDonor.text.toString().trim()
        form.password = password.text.toString().trim()
        val retro = Retro().getRetroClientInstance().create(PendonorLoginAPI::class.java)
        retro.login(form).enqueue(object : Callback<PendonorLoginResponse> {
            override fun onResponse(
                call: Call<PendonorLoginResponse>,
                response: Response<PendonorLoginResponse>
            ) {
                val res = response.body()
                if (response.code() == 200){
                    if(res!!.success == true){
                        Log.e("Login", res.message.toString())
                        sharedPref.setStatusLogin(true)
                        sharedPref.setToken(
                            res.token.toString()
                        )
                        sharedPref.setStatusLogin(true)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                }else{
                    // Jika status false, tampilkan pesan kesalahan
                    showAlertDialog("Kombinasi kode pendonor dan password salah.")
                }
            }

            override fun onFailure(call: Call<PendonorLoginResponse>, t: Throwable) {
            }
        })
    }
    // Fungsi untuk menampilkan AlertDialog
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