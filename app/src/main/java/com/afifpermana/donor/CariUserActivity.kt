package com.afifpermana.donor

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.adapter.ChatAdapter
import com.afifpermana.donor.adapter.CommentAdapter
import com.afifpermana.donor.adapter.UserCariAdapter
import com.afifpermana.donor.model.BalasCommentResponse
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.model.cari_user.User
import com.afifpermana.donor.model.cari_user.UserRequest
import com.afifpermana.donor.model.cari_user.UserResponse
import com.afifpermana.donor.service.CommentAPI
import com.afifpermana.donor.service.UserCari
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CariUserActivity : AppCompatActivity() {
    var userList = ArrayList<User>()
    lateinit var sharedPref: SharedPrefLogin
    private lateinit var ed_cari : EditText
    private lateinit var btn_cari : ImageView
    private lateinit var backButton : ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var cl_loading: ConstraintLayout
    private lateinit var no_user: CardView

    private lateinit var adapter: UserCariAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cari_user)
        sharedPref = SharedPrefLogin(this)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
        ed_cari = findViewById(R.id.cari_user)
        btn_cari = findViewById(R.id.btn_cari)
        cl_loading = findViewById(R.id.cl_loading)
        no_user = findViewById(R.id.no_user)

        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rv_user)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = UserCariAdapter(userList,sharedPref)
        recyclerView.adapter = adapter

        ed_cari.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                if (textLength > 0) {
                    // Panjang teks lebih dari 0, atur backgroundTint ke warna yang Anda inginkan
                    btn_cari.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CariUserActivity, R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    btn_cari.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CariUserActivity, R.color.grey))
                }
            }
        })

        btn_cari.setOnClickListener {
            val textValue = ed_cari.text.toString().trim()
            if (textValue.isNotBlank() ){
                val connectivityChecker = ConnectivityChecker(this)
                if (connectivityChecker.isNetworkAvailable()){
                    //koneksi aktif
                    ed_cari.text.clear()
                    cl_loading.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    no_user.visibility = View.GONE
                    cariUserView(textValue)

                }else{
                    //koneksi tidak aktif
                    connectivityChecker.showAlertDialogNoConnection()
                }
            }
        }
    }

    private fun cariUserView(nm:String) {
        userList.clear()
        adapter.notifyDataSetChanged()
        val nama = UserRequest()
        nama.nama = nm.trim()
        Log.e("afiff",nm)
        val retro = Retro().getRetroClientInstance().create(UserCari::class.java)
        retro.cari("Bearer ${sharedPref.getString("token")}",nama).enqueue(object :
            Callback<List<UserResponse>> {
            override fun onResponse(
                call: Call<List<UserResponse>>,
                response: Response<List<UserResponse>>
            ) {
                if (response.code()==200){
                    val res = response.body()
                    if (!res!!.isEmpty()){
                        for (i in res!!){
                            val data = User(
                                i.id.toString().toInt(),
                                i.nama.toString(),
                                i.gambar.toString(),
                                i.kode_pendonor.toString()
                            )
                            Log.e("afiff",data.toString())
                            userList.add(data)
                        }
//                        adapter.notifyDataSetChanged()
                        cl_loading.visibility = View.GONE
                        no_user.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }else{
                        cl_loading.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        no_user.visibility = View.VISIBLE
                    }
                }

                if (response.code() == 403){
                    cl_loading.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    no_user.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CariUserActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
}