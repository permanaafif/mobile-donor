package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.CommentAdapter
import com.afifpermana.donor.adapter.NotifikasiAdapter
import com.afifpermana.donor.model.BalasCommentTo
import com.afifpermana.donor.model.BeritaResponse
import com.afifpermana.donor.model.Comments
import com.afifpermana.donor.model.Notifikasi
import com.afifpermana.donor.model.NotifikasiResponse
import com.afifpermana.donor.model.UpdateStatusNotifikasiResponse
import com.afifpermana.donor.service.BeritaAPI
import com.afifpermana.donor.service.CallBackNotif
import com.afifpermana.donor.service.NotifikasiAPI
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.airbnb.lottie.LottieAnimationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiActivity : AppCompatActivity(), CallBackNotif {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var cl_notifikasi : ConstraintLayout
    private lateinit var loadingLottie : LottieAnimationView
    private lateinit var nodataLottie : LottieAnimationView
    private lateinit var adapter: NotifikasiAdapter
    private lateinit var recyclerView : RecyclerView
    var newData : ArrayList<Notifikasi> = ArrayList()
    lateinit var sharedPref: SharedPrefLogin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi)

        sharedPref = SharedPrefLogin(this)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
        val layoutManager = LinearLayoutManager(this)
        sw_layout = findViewById(R.id.swlayout)
        cl_notifikasi = findViewById(R.id.cl_notifikasi)
        loadingLottie = findViewById(R.id.loading)
        nodataLottie = findViewById(R.id.no_data)
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        recyclerView = findViewById(R.id.rv_notifikasi)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NotifikasiAdapter(newData,this,this,sharedPref)
        recyclerView.adapter = adapter

        notifikasiView()
        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                notifikasiView()
                sw_layout.isRefreshing = false
            }, 1000)
        }
    }

    private fun notifikasiView() {
        cl_notifikasi.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        loadingLottie.visibility = View.VISIBLE
        nodataLottie.visibility = View.GONE
        val connectivityChecker = ConnectivityChecker(this)
        if (connectivityChecker.isNetworkAvailable()){
            //koneksi aktif
            val retro = Retro().getRetroClientInstance().create(NotifikasiAPI::class.java)
            retro.notif("Bearer ${sharedPref.getString("token")}").enqueue(object :
                Callback<List<NotifikasiResponse>> {
                override fun onResponse(
                    call: Call<List<NotifikasiResponse>>,
                    response: Response<List<NotifikasiResponse>>
                ) {
                    Log.e("responsecode",response.code().toString())
                    if (response.isSuccessful) {
                        val res = response.body()
                        if (!res.isNullOrEmpty()) {
                            for (i in res) {
                                if(i.id_pembalas!!.toInt() != sharedPref.getInt("id")){
                                    val pendonor = Notifikasi.PendonorItem(
                                        i.pendonor?.id!!.toInt(),
                                        i.pendonor.gambar,
                                        i.pendonor.nama!!
                                    )
                                    val data = Notifikasi(
                                        i.id!!,
                                        i.id_post!!,
                                        i.id_comment ?: 0,
                                        i.id_balas_comment ?: 0,
                                        i.status_read!!.toInt(),
                                        i.id_pembalas!!.toInt(),
                                        i.update.toString(),
                                        pendonor
                                    )
                                    newData.add(data)
                                }
                            }
                            adapter.notifyDataSetChanged()
                            cl_notifikasi.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            loadingLottie.visibility = View.GONE
                            nodataLottie.visibility = View.GONE
                        }else{
                            cl_notifikasi.visibility = View.VISIBLE
                            loadingLottie.visibility = View.GONE
                            nodataLottie.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }

                }

                override fun onFailure(call: Call<List<NotifikasiResponse>>, t: Throwable) {
                    Toast.makeText(this@NotifikasiActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                    sharedPref.logOut()
                    sharedPref.setStatusLogin(false)
                    startActivity(Intent(this@NotifikasiActivity, LoginActivity::class.java))
                    finish()
                }
            })
        }else{
            //koneksi tidak aktif
            connectivityChecker.showAlertDialogNoConnection()
            cl_notifikasi.visibility = View.VISIBLE
            loadingLottie.visibility = View.GONE
            nodataLottie.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

    }

    private fun clearData() {
        newData.clear()
        adapter.notifyDataSetChanged()
    }

    override fun updateStatusRead(id: Int) {
        UpdateStatusRead(id)
    }

    private fun UpdateStatusRead(id: Int) {
        val retro = Retro().getRetroClientInstance().create(NotifikasiAPI::class.java)
        retro.updateStatusNotif("Bearer ${sharedPref.getString("token")}", id).enqueue(object :
            Callback<UpdateStatusNotifikasiResponse> {
            override fun onResponse(
                call: Call<UpdateStatusNotifikasiResponse>,
                response: Response<UpdateStatusNotifikasiResponse>
            ) {
                if (response.isSuccessful){
                    var res = response.body()
                    if (res?.success == true){
//                        Toast.makeText(this@NotifikasiActivity,"berhasil", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<UpdateStatusNotifikasiResponse>, t: Throwable) {
                Toast.makeText(this@NotifikasiActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@NotifikasiActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
}