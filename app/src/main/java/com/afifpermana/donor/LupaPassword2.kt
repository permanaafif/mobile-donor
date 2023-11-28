package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afifpermana.donor.model.lupa_password.resetPasswordRequest
import com.afifpermana.donor.model.lupa_password.resetPasswordResponse
import com.afifpermana.donor.service.LupaPasswordAPI
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPassword2 : AppCompatActivity() {
    private lateinit var password: EditText
    private lateinit var confirmpassword: EditText
    private lateinit var loadingProgressBar : ProgressBar
    private lateinit var ganti : Button
    var b : Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupapassword2)
        b = intent.extras
        password = findViewById(R.id.passwordbaru)
        confirmpassword = findViewById(R.id.passwordbaru1)
        loadingProgressBar=findViewById(R.id.loadingProgressBar)

        ganti = findViewById(R.id.btnUbah)
        ganti.setOnClickListener{
            val connectivityChecker = ConnectivityChecker(this)
            if (connectivityChecker.isNetworkAvailable()){
                //koneksi aktif
                if(validasiPasswdor()){
                    resetPassword()
                }
            }else{
                //koneksi tidak aktif
                connectivityChecker.showAlertDialogNoConnection()
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun resetPassword() {
        loadingProgressBar.visibility = View.VISIBLE
        ganti.visibility = View.GONE
        val data = resetPasswordRequest()
        data.email = b!!.getString("email")
        data.token = b!!.getString("token")
        data.password = password.text.toString().trim()
        val retro = Retro().getRetroClientInstance().create(LupaPasswordAPI::class.java)
        retro.resetPassword(data).enqueue(object : Callback<resetPasswordResponse> {
            override fun onResponse(
                call: Call<resetPasswordResponse>,
                response: Response<resetPasswordResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    if (res!!.success == true){
                        Toast.makeText(this@LupaPassword2, res.message.toString(),Toast.LENGTH_LONG).show()
                        var i = Intent(this@LupaPassword2,LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Toast.makeText(this@LupaPassword2, res.message.toString(),Toast.LENGTH_LONG).show()
                        loadingProgressBar.visibility = View.GONE
                        ganti.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<resetPasswordResponse>, t: Throwable) {

            }
        })
    }

    private fun validasiPasswdor(): Boolean {
        if (password.text.toString().trim().isNullOrEmpty()){
            password.error = "Masukkan Password"
            return false
        }
        if (password.text.toString().trim().length <= 8){
            password.error = "Password harus lebih dari 8 karakter"
            return false
        }
        if (confirmpassword.text.toString().trim().isNullOrEmpty()){
            confirmpassword.error = "Masukkan konfirmasi Password"
            return false
        }
        if (confirmpassword.text.toString().trim() != password.text.toString().trim()){
            confirmpassword.error = "Konfirmasi password salah"
            return false
        }
        return true
    }

}