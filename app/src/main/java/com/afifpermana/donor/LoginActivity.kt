package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
            if(kodeDonor.text.isNullOrEmpty()){
                Toast.makeText(this@LoginActivity,"Isi Kode Pendonor", Toast.LENGTH_LONG).show()
            }else if(password.text.isNullOrEmpty()){
                Toast.makeText(this@LoginActivity,"Isi Password", Toast.LENGTH_LONG).show()
            }else{
                loginPendonor()
            }
        }
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
                if (res != null){
                    if(res.success == true){
                        sharedPref.setStatusLogin(true)
                        sharedPref.setToken(
//                            res.user.id!!.toInt(),
//                            res.user.nama.toString(),
//                            res.user.kode_pendonor.toString(),
//                            res.golongan_darah.nama.toString(),
//                            res.user.berat_badan!!.toInt(),
                            res.token.toString()
                        )
                        sharedPref.setStatusLogin(true)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }else{
                        Toast.makeText(this@LoginActivity,"Kode Pendonor atau password salah", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<PendonorLoginResponse>, t: Throwable) {
            }
        })
    }
}