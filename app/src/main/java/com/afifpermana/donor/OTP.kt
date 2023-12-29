package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.afifpermana.donor.model.lupa_password.checkOtpRequest
import com.afifpermana.donor.model.lupa_password.checkOtpResponse
import com.afifpermana.donor.model.lupa_password.sendOtpRequest
import com.afifpermana.donor.model.lupa_password.sendOtpResponse
import com.afifpermana.donor.service.LupaPasswordAPI
import com.afifpermana.donor.util.ConnectivityChecker
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
    private lateinit var resend_otp : TextView
    private lateinit var btn_verifikasi : Button
    private lateinit var otp : String
    private lateinit var loadingProgressBar : ProgressBar
    var i = 30
    var kode_pendonor = ""
    var email = ""
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        b = intent.extras
        kode_pendonor = b!!.getString("kode_pendonor").toString()
        email = b!!.getString("email").toString()
        token = b!!.getString("token").toString()
        kode1 = findViewById(R.id.kode1)
        kode2 = findViewById(R.id.kode2)
        kode3 = findViewById(R.id.kode3)
        kode4 = findViewById(R.id.kode4)
        resend_otp = findViewById(R.id.resend_otp)
        btn_verifikasi = findViewById(R.id.btnOTP)
        loadingProgressBar=findViewById(R.id.loadingProgressBar)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        // Menambahkan TextWatcher ke setiap EditText
        kode1.addTextChangedListener(OTPTextWatcher(kode1, kode2))
        kode2.addTextChangedListener(OTPTextWatcher(kode2, kode3))
        kode3.addTextChangedListener(OTPTextWatcher(kode3, kode4))
        kode4.addTextChangedListener(OTPTextWatcher(kode4, null)) // Tidak ada EditText berikutnya

        // Pengaturan fokus awal pada kode1
        kode1.requestFocus()

        btn_verifikasi.setOnClickListener {
            val connectivityChecker = ConnectivityChecker(this)
            if (connectivityChecker.isNetworkAvailable()){
                //koneksi aktif
                if(valisasiInputOtp()){
                    otp = "${kode1.text.trim()}${kode2.text.trim()}${kode3.text.trim()}${kode4.text.trim()}"
                    checkOtp()
                }
            }else{
                //koneksi tidak aktif
                connectivityChecker.showAlertDialogNoConnection()
            }
        }
        updateTextResendOTP()

        resend_otp.setOnClickListener {
            val textColor = resend_otp.currentTextColor
            val blackColor = ContextCompat.getColor(this, R.color.black)
            if (textColor == blackColor) {
                ResendOtp()
                resend_otp.setTextColor(getResources().getColor(R.color.grey))
                i = 30
                updateTextResendOTP()
            }
        }

    }

    fun updateTextResendOTP() {
        resend_otp.text = "Resend OTP ($i)"
        i--

        if (i >= 0) {
            Handler().postDelayed({
                updateTextResendOTP()
            }, 1000) // 1000 milidetik = 1 detik
        } else {
            resend_otp.text = "Resend OTP"
            resend_otp.setTextColor(getResources().getColor(R.color.black))
        }
    }

    private fun checkOtp() {
        loadingProgressBar.visibility = View.VISIBLE
        btn_verifikasi.visibility = View.GONE
        val data = checkOtpRequest()
        data.email = email
        data.token = token
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
                        loadingProgressBar.visibility = View.GONE
                        btn_verifikasi.visibility = View.VISIBLE
                    }else{
                        Toast.makeText(this@OTP, res.message.toString(), Toast.LENGTH_LONG).show()
                        loadingProgressBar.visibility = View.GONE
                        btn_verifikasi.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<checkOtpResponse>, t: Throwable) {

            }
        })
    }

    private fun ResendOtp() {
        val data = sendOtpRequest()
        data.kode_pendonor = kode_pendonor
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
                        email = res.email.toString()
                        token = res.token.toString()
                        Toast.makeText(this@OTP, "Kode OTP berhasil di kirim",Toast.LENGTH_LONG).show()
                    }else{
                        Log.e("kode pendonor", res.message.toString())
                        Toast.makeText(this@OTP, res.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<sendOtpResponse>, t: Throwable) {
                Toast.makeText(this@OTP, t.message.toString(),Toast.LENGTH_LONG).show()
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

    // TextWatcher kustom untuk mengatur fokus ke EditText berikutnya
    private inner class OTPTextWatcher(private val currentEditText: EditText, private val nextEditText: EditText?) :
        TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.length == 1 && nextEditText != null) {
                nextEditText.requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Tidak diperlukan
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // Tidak diperlukan
        }
    }
}