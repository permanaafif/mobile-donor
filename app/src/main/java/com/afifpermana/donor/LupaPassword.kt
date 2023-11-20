package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afifpermana.donor.model.HomeResponse
import com.afifpermana.donor.model.lupa_password.sendOtpRequest
import com.afifpermana.donor.model.lupa_password.sendOtpResponse
import com.afifpermana.donor.service.HomeAPI
import com.afifpermana.donor.service.LupaPasswordAPI
import com.afifpermana.donor.util.Retro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPassword : AppCompatActivity() {
    private lateinit var kode_pendonor: EditText
    private lateinit var btn_kirim_otp: Button
    private lateinit var loadingProgressBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupapassword)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        kode_pendonor = findViewById(R.id.kodedonor)
        btn_kirim_otp = findViewById(R.id.btnKirimOTP)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        btn_kirim_otp.setOnClickListener {
            if(validasiInput()){
                kirimOtp()
            }
        }
    }

    private fun validasiInput(): Boolean {
        if (kode_pendonor.text.isEmpty()){
            kode_pendonor.error = "Masukkan kode pendonor"
            return false
        }
        return true
    }

    private fun kirimOtp() {
        loadingProgressBar.visibility = View.VISIBLE
        btn_kirim_otp.visibility = View.GONE
        val data = sendOtpRequest()
        data.kode_pendonor = kode_pendonor.text.toString().trim()
        val retro = Retro().getRetroClientInstance().create(LupaPasswordAPI::class.java)
        retro.sendOtp(data).enqueue(object : Callback<sendOtpResponse> {
            override fun onResponse(
                call: Call<sendOtpResponse>,
                response: Response<sendOtpResponse>
            ) {
                Log.e("kode pendonor", response.code().toString())
                if (response.isSuccessful){
                    val res = response.body()
                    if (res!!.success == true){
                        Toast.makeText(this@LupaPassword, res.message.toString(),Toast.LENGTH_LONG).show()
                        var i = Intent(this@LupaPassword,OTP::class.java)
                        i.putExtra("email",res.email.toString())
                        i.putExtra("token",res.token.toString())
                        i.putExtra("kode_pendonor",kode_pendonor.text.toString())
                        startActivity(i)
                        loadingProgressBar.visibility = View.GONE
                        btn_kirim_otp.visibility = View.VISIBLE
                    }else{
                        Log.e("kode pendonor", res.message.toString())
                        Toast.makeText(this@LupaPassword, res.message.toString(),Toast.LENGTH_LONG).show()
                        loadingProgressBar.visibility = View.GONE
                        btn_kirim_otp.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<sendOtpResponse>, t: Throwable) {

            }
        })
    }


}
