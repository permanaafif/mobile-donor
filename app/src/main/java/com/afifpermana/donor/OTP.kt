package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.afifpermana.donor.model.lupa_password.checkOtpRequest
import com.afifpermana.donor.model.lupa_password.checkOtpResponse
import com.afifpermana.donor.model.lupa_password.sendOtpResponse
import com.afifpermana.donor.service.LupaPasswordAPI
import com.afifpermana.donor.util.Retro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTP : AppCompatActivity() {
    var b : Bundle? = null
    private lateinit var kode1 : EditText
    private lateinit var kode2 : EditText
    private lateinit var kode3 : EditText
    private lateinit var kode4 : EditText
    private lateinit var btn_verifikasi : Button
    private lateinit var otp : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        b = intent.extras
        kode1 = findViewById(R.id.kode1)
        kode2 = findViewById(R.id.kode2)
        kode3 = findViewById(R.id.kode3)
        kode4 = findViewById(R.id.kode4)
        btn_verifikasi = findViewById(R.id.btnOTP)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        btn_verifikasi.setOnClickListener {
            if(valisasiInputOtp()){
                otp = "${kode1.text.trim()}${kode2.text.trim()}${kode3.text.trim()}${kode4.text.trim()}"
                checkOtp()
            }
        }

    }

    private fun checkOtp() {
        val data = checkOtpRequest()
        data.email = b!!.getString("email")
        data.token = b!!.getString("token")
        data.otp = otp
        val retro = Retro().getRetroClientInstance().create(LupaPasswordAPI::class.java)
        retro.checkOtp(data).enqueue(object : Callback<checkOtpResponse> {
            override fun onResponse(
                call: Call<checkOtpResponse>,
                response: Response<checkOtpResponse>
            ) {
                Log.e("kode otp", response.code().toString())
                if (response.isSuccessful){
                    val res = response.body()
                    if (res!!.success == true){
                        Log.e("kode otp", res.message.toString())
                        Toast.makeText(this@OTP, res.message.toString(), Toast.LENGTH_LONG).show()
                        var i = Intent(this@OTP,LupaPassword2::class.java)
                        i.putExtra("email",res.email.toString())
                        i.putExtra("token",res.token.toString())
                        startActivity(i)
                    }else{
                        Toast.makeText(this@OTP, res.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<checkOtpResponse>, t: Throwable) {

            }
        })
    }

    private fun valisasiInputOtp(): Boolean {
        if (kode1.text.isNullOrEmpty()){
            kode1.error = "otp"
            return false
        }
        if (kode2.text.isNullOrEmpty()){
            kode2.error = "otp"
            return false
        }
        if (kode3.text.isNullOrEmpty()){
            kode3.error = "otp"
            return false
        }
        if (kode4.text.isNullOrEmpty()){
            kode4.error = "otp"
            return false
        }
        return true
    }
}