package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.PostAdapter
import com.afifpermana.donor.model.Laporan
import com.afifpermana.donor.model.LaporanRequest
import com.afifpermana.donor.model.LaporanResponse
import com.afifpermana.donor.model.Post
import com.afifpermana.donor.model.PostFavorite
import com.afifpermana.donor.model.PostFavoriteResponse
import com.afifpermana.donor.model.PostFavoriteResponse2
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.service.CallBackData
import com.afifpermana.donor.service.LaporanAPI
import com.afifpermana.donor.service.PostAPI
import com.afifpermana.donor.service.PostFavoriteAPI
import com.afifpermana.donor.service.ProfileAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherDonorProfileActivity : AppCompatActivity(), CallBackData {
    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var cl_post : ConstraintLayout
    private lateinit var loadingLottie : LottieAnimationView
    private lateinit var nodataLottie : LottieAnimationView
    private lateinit var fotoProfile : CircleImageView
    private lateinit var nama : TextView
    private lateinit var tv_golongan_darah : TextView
    private lateinit var tv_total_donor_darah : TextView
    private lateinit var sharedPref: SharedPrefLogin

    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    var newData : ArrayList<Post> = ArrayList()
    var newDataPostFavorite : ArrayList<PostFavorite> = ArrayList()

    var b : Bundle? =null
    var id_pendonor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_donor_profile)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }

        b = intent.extras
        id_pendonor = b!!.getInt("id_pendonor")

        fotoProfile = findViewById(R.id.foto)
        nama = findViewById(R.id.nama)
        tv_golongan_darah = findViewById(R.id.tv_golongan_darah)
        tv_total_donor_darah = findViewById(R.id.tv_total_donor_darah)

        sharedPref = SharedPrefLogin(this)
        val layoutManager = LinearLayoutManager(this)

        recyclerView = findViewById(R.id.rv_post_saya)
        cl_post = findViewById(R.id.cl_post_saya)
        loadingLottie = findViewById(R.id.loading)
        nodataLottie = findViewById(R.id.no_data)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        sharedPref = SharedPrefLogin(this)

        adapter = PostAdapter(newData,newDataPostFavorite,this,this,sharedPref)
        recyclerView.adapter = adapter
        adapter.idPendonorNow(id_pendonor)

        profileView(id_pendonor)
        postViewOtherDonor(id_pendonor)
        postFavorite()

        sw_layout = findViewById(R.id.swlayout)
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                profileView(id_pendonor)
                postViewOtherDonor(id_pendonor)
                postFavorite()
                sw_layout.isRefreshing = false
            }, 1000)
        }

    }

    private fun clearData() {
        newData.clear()
        newDataPostFavorite.clear()
        adapter.notifyDataSetChanged()
    }

    private fun profileView(id:Int) {
        val retro = Retro().getRetroClientInstance().create(ProfileAPI::class.java)
        retro.profileOtherDonor("Bearer ${sharedPref.getString("token")}",id).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                val resCode = response.code()
                if (resCode == 200){
                    Log.e("profilecaliak","success")
                    val res = response.body()!!
                    tv_golongan_darah.text = res.user.id_golongan_darah.nama
                    if (res.user.total_donor_darah.toString() != "null"){
                        if (res.user.total_donor_darah.toString().toInt() <= 0){
                            tv_total_donor_darah.text = res.user.total_donor_darah.toString()
                        }
                    }else{
                        tv_total_donor_darah.text = "0"
                    }
                    if(res.user.gambar.isNullOrEmpty()){
                        fotoProfile.setImageResource(R.drawable.baseline_person_24)
                    }else{
                        Picasso.get().load("http://138.2.74.142/images/${res.user!!.gambar}").into(fotoProfile)
                        val pathFoto = "http://138.2.74.142/images/${res.user!!.gambar}"
                        fotoProfile.setOnClickListener {
                            showAlertGambar(pathFoto!!)
                        }

                    }
                    nama.text = res.user.nama.toString().capitalize()
                }
                else{
                    sharedPref.setStatusLogin(false)
                    sharedPref.logOut()
                    val intent = Intent(this@OtherDonorProfileActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(this@OtherDonorProfileActivity,"Gagal", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun postViewOtherDonor(id: Int) {
        cl_post.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        loadingLottie.visibility = View.VISIBLE
        val retro = Retro().getRetroClientInstance().create(PostAPI::class.java)
        retro.postOtherDonor("Bearer ${sharedPref.getString("token")}", id).enqueue(object :
            Callback<List<PostRespone>> {
            override fun onResponse(
                call: Call<List<PostRespone>>,
                response: Response<List<PostRespone>>
            ) {
                Log.e("pesanden",response.code().toString())
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res.isNullOrEmpty()){
                        cl_post.visibility = View.VISIBLE
                        loadingLottie.visibility = View.GONE
                        nodataLottie.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }else{
                        // Menggunakan sortedByDescending untuk mengurutkan berdasarkan tanggal terbaru
                        for (i in res!!) {
                            val data = Post(
                                i.id.toString().toInt(),
                                i.id_pendonor.toString().toInt(),
                                i.gambar_profile.toString(),
                                i.nama.toString(),
                                i.updated_at.toString(),
                                i.text.toString(),
                                i.gambar.toString(),
                                i.jumlah_comment.toString().toInt()
                            )
                            newData.add(data)
                        }
                        adapter.notifyDataSetChanged()
                        cl_post.visibility = View.GONE
                        loadingLottie.visibility = View.VISIBLE
                        nodataLottie.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
                if(response.code() == 404){
                    cl_post.visibility = View.VISIBLE
                    loadingLottie.visibility = View.GONE
                    nodataLottie.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<PostRespone>>, t: Throwable) {
                Toast.makeText(this@OtherDonorProfileActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("pesanden", t.message.toString())
            }
        })
    }

    private fun postFavorite() {
        val retro = Retro().getRetroClientInstance().create(PostFavoriteAPI::class.java)
        retro.postFavorite("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<List<PostFavoriteResponse>> {
            override fun onResponse(
                call: Call<List<PostFavoriteResponse>>,
                response: Response<List<PostFavoriteResponse>>
            ) {
                if (response.isSuccessful){
                    var res = response.body()
                    if (!res.isNullOrEmpty()){
                        newDataPostFavorite.clear()
                        for (i in res!!){
                            val data = PostFavorite(
                                i.id!!,
                                i.id_pendonor!!,
                                i.id_post!!,
                                i.created_at!!,
                                i.updated_at!!
                            )
                            newDataPostFavorite.add(data)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }else{
                    Toast.makeText(this@OtherDonorProfileActivity,"terjaadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PostFavoriteResponse>>, t: Throwable) {
                Toast.makeText(this@OtherDonorProfileActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@OtherDonorProfileActivity, LoginActivity::class.java))
                finish()
            }
        })
    }

    override fun onDataReceived(nama: String, id: Int) {
        //
    }

    override fun onDataReceivedFavorite(id: Int) {
        addPostFavorite(id)
    }

    override fun onDeleteFavorite(id: Int) {
        deletePostFavorite(id)
    }

    override fun onDeletePost(id: Int) {
        //
    }

    override fun onAddLaporan(laporan: Laporan) {
        addLaporan(laporan)
    }

    private fun addPostFavorite(id:Int){
        val retro = Retro().getRetroClientInstance().create(PostFavoriteAPI::class.java)
        retro.addPostFavorite("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<PostFavoriteResponse2> {
            override fun onResponse(
                call: Call<PostFavoriteResponse2>,
                response: Response<PostFavoriteResponse2>
            ) {
                if (response.isSuccessful){
                    var res = response.body()
                    if (res?.success == true){
                        Toast.makeText(this@OtherDonorProfileActivity,"Simpan", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(this@OtherDonorProfileActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@OtherDonorProfileActivity, LoginActivity::class.java))
                finish()
            }
        })
    }

    private fun deletePostFavorite(id: Int) {
        val retro = Retro().getRetroClientInstance().create(PostFavoriteAPI::class.java)
        retro.deletePostFavorite("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<PostFavoriteResponse2> {
            override fun onResponse(
                call: Call<PostFavoriteResponse2>,
                response: Response<PostFavoriteResponse2>
            ) {
                if (response.isSuccessful){
                    var res = response.body()
                    if (res?.success == true){
                        Toast.makeText(this@OtherDonorProfileActivity,"Tidak si simpan", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(this@OtherDonorProfileActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@OtherDonorProfileActivity, LoginActivity::class.java))
                finish()
            }
        })
    }

    private fun addLaporan(laporan: Laporan) {
        var data = LaporanRequest()
        data.id_post = laporan.id_post
        data.id_comment = laporan.id_comment
        data.id_reply = laporan.id_reply
        data.text = laporan.text
        data.type = laporan.type
        val retro = Retro().getRetroClientInstance().create(LaporanAPI::class.java)
        retro.addLaporan("Bearer ${sharedPref.getString("token")}",data).enqueue(object :
            Callback<LaporanResponse> {
            override fun onResponse(
                call: Call<LaporanResponse>,
                response: Response<LaporanResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    if (res != null){
                        Toast.makeText(this@OtherDonorProfileActivity,"Laporan Dikirim",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@OtherDonorProfileActivity,"Laporan tidak terkirim",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LaporanResponse>, t: Throwable) {
                Toast.makeText(this@OtherDonorProfileActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@OtherDonorProfileActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
    private fun showAlertGambar(path:String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val customeView = LayoutInflater.from(this).inflate(R.layout.alert_gambar,null)
        builder.setView(customeView)
        val dialog = builder.create()

        val image = customeView.findViewById<ImageView>(R.id.dialogImageView)
        image.setOnClickListener {
            dialog.dismiss()
        }
        Picasso.get().load(path).into(image)
        dialog.window?.setDimAmount(1f)
        dialog.show()
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}