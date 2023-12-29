package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.afifpermana.donor.service.PendonorLoginAPI
import com.afifpermana.donor.util.ConnectivityChecker
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
    private lateinit var iv_icon_pwd : ImageView
    private lateinit var info : Button
    private lateinit var lupa : Button
    private lateinit var login : Button
    private lateinit var loadingProgressBar : ProgressBar

    lateinit var sharedPref : SharedPrefLogin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = SharedPrefLogin(this)
        kodeDonor=findViewById(R.id.kodedonor)
        password=findViewById(R.id.password)
        iv_icon_pwd=findViewById(R.id.iv_eyes_visible)
        loadingProgressBar=findViewById(R.id.loadingProgressBar)
        info=findViewById(R.id.tentangdara)
        lupa=findViewById(R.id.lupapassword)
        login=findViewById(R.id.btnLogin)

        info.setOnClickListener{
            val intent = Intent(this,TentangDaraActivity::class.java)
            startActivity(intent)
        }

        lupa.setOnClickListener{
            val intent = Intent(this, LupaPassword::class.java)
            startActivity(intent)
        }

        login.setOnClickListener{
            val connectivityChecker = ConnectivityChecker(this)
            if (connectivityChecker.isNetworkAvailable()){
                //koneksi aktif
                if (validateInput()){
                    loginPendonor()
                }
            }else{
                //koneksi tidak aktif
                connectivityChecker.showAlertDialogNoConnection()
            }
        }

        iv_icon_pwd.setOnClickListener{
            if (password.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Jika sedang dalam mode tersembunyi, ubah ke tampilan karakter terlihat
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_icon_pwd.setImageResource(R.drawable.baseline_visibility_24)
            } else {
                // Jika sedang dalam mode tampilan karakter terlihat, ubah ke tersembunyi
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_icon_pwd.setImageResource(R.drawable.baseline_visibility_off_24)
            }
            // Setelah mengubah inputType, Anda mungkin perlu memindahkan kursor ke akhir teks
            password.setSelection(password.text.length)
        }
    }
    // Validasi input
    private fun validateInput(): Boolean {
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
        login.visibility = View.GONE
        loadingProgressBar.visibility = View.VISIBLE
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
                        sharedPref.setStatusWalkTrough(true)
                        sharedPref.setStatusLogin(true)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }else{
                    // Jika status false, tampilkan pesan kesalahan
                    showAlertDialog("Kombinasi kode pendonor dan password salah.")
                    loadingProgressBar.visibility = View.GONE
                    login.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<PendonorLoginResponse>, t: Throwable) {
                Log.e("Loginn", t.message.toString())
                loadingProgressBar.visibility = View.GONE
                login.visibility = View.VISIBLE
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