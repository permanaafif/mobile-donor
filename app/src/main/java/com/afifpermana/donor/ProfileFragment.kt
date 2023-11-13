package com.afifpermana.donor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.PostAdapter
import com.afifpermana.donor.model.Laporan
import com.afifpermana.donor.model.LaporanRequest
import com.afifpermana.donor.model.LaporanResponse
import com.afifpermana.donor.model.PendonorLogoutResponse
import com.afifpermana.donor.model.Post
import com.afifpermana.donor.model.PostFavorite
import com.afifpermana.donor.model.PostFavoriteResponse
import com.afifpermana.donor.model.PostFavoriteResponse2
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.model.PostResponse2
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.model.TotalNotifResponse
import com.afifpermana.donor.service.CallBackData
import com.afifpermana.donor.service.LaporanAPI
import com.afifpermana.donor.service.NotifikasiAPI
import com.afifpermana.donor.service.PendonorLogoutAPI
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

class ProfileFragment : Fragment(), CallBackData {

    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var cl_post : ConstraintLayout
    private lateinit var loadingLottie : LottieAnimationView
    private lateinit var nodataLottie : LottieAnimationView
    private lateinit var edit : CardView
    private lateinit var ganti_password : CardView
    private lateinit var notif : CardView
    private lateinit var radioGroup: RadioGroup
    private lateinit var logout : ImageView
    private lateinit var sharedPref: SharedPrefLogin

    private lateinit var cv_notif_badge : CardView
    private lateinit var tv_text_notif_badge : TextView

    private lateinit var fotoProfile : CircleImageView
    private lateinit var nama : TextView
    private lateinit var email : String
    private lateinit var namaUser : String
    private lateinit var tanggal_lahir : String
    private lateinit var kode_pendonor : TextView
    private lateinit var alamat : String
    private lateinit var jenis_kelamin : String
    private lateinit var kontak : String
    private lateinit var berat_badan : String
    var pathFoto : String? = null

    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    var newData : ArrayList<Post> = ArrayList()
    var newDataPostFavorite : ArrayList<PostFavorite> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPrefLogin(requireActivity())
        profileView()
        val layoutManager = LinearLayoutManager(context)

        recyclerView = view.findViewById(R.id.rv_post_saya)
        cl_post = view.findViewById(R.id.cl_post_saya)
        loadingLottie = view.findViewById(R.id.loading)
        nodataLottie = view.findViewById(R.id.no_data)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        sharedPref = SharedPrefLogin(requireActivity())

        cv_notif_badge = view.findViewById(R.id.cv_notif_badge)
        tv_text_notif_badge = view.findViewById(R.id.tv_text_notif_badge)

        totalNotif()

        postViewMe()

        adapter = PostAdapter(newData,newDataPostFavorite,requireContext(),this)
        recyclerView.adapter = adapter

        postFavorite()

        sw_layout = view.findViewById(R.id.swlayout)
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                profileView()
                totalNotif()
                if (radioGroup.checkedRadioButtonId == R.id.btn_favorite){
                    Log.e("btnfav","fav")
                    postFavorite()
                }else{
                    clearData()
                    postViewMe()
                    postFavorite()
                }
                sw_layout.isRefreshing = false
            }, 1000)
        }

        radioGroup = view.findViewById(R.id.rg)
        radioGroup.check(R.id.btn_semua)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_semua -> {
                    clearData()
                    postViewMe()
                    postFavorite()
                }
                R.id.btn_favorite -> {
                    Log.e("abcd","1")
                    clearData()
                    postFavorite()
                    Log.e("abcd","2")
                }
            }
        }

        edit = view.findViewById(R.id.cv_edit_profile)
        logout = view.findViewById(R.id.btnLogOut)
        ganti_password = view.findViewById(R.id.cv_ganti_password)
        notif = view.findViewById(R.id.cv_notifikasi)
        fotoProfile = view.findViewById(R.id.foto)
        nama = view.findViewById(R.id.nama)
        kode_pendonor = view.findViewById(R.id.kode)

        edit.setOnClickListener{
            val i = Intent(context, ProfileEdit::class.java)
            i.putExtra("gambar", pathFoto.toString())
            i.putExtra("nama", nama.text)
            i.putExtra("email", email)
            i.putExtra("tanggal_lahir", tanggal_lahir)
            i.putExtra("alamat", alamat)
            i.putExtra("jenis_kelamin", jenis_kelamin)
            i.putExtra("kontak", kontak)
            i.putExtra("kode_pendonor", kode_pendonor.text)
            i.putExtra("berat_badan", berat_badan)
            startActivity(i)
        }

        ganti_password.setOnClickListener {
            val intent = Intent(context,GantiPassword::class.java)
            startActivity(intent)
        }

        notif.setOnClickListener{
            val intent = Intent(context,NotifikasiActivity::class.java)
            startActivity(intent)
        }

        logout.setOnClickListener{
            showCostumeAlertDialog("logout")
        }
    }
    private fun totalNotif() {
        val retro = Retro().getRetroClientInstance().create(NotifikasiAPI::class.java)
        retro.totalNotif("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<TotalNotifResponse> {
            override fun onResponse(
                call: Call<TotalNotifResponse>,
                response: Response<TotalNotifResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    if (res?.total_notif != 0){
                        cv_notif_badge.visibility = View.VISIBLE
                        tv_text_notif_badge.text = res!!.total_notif.toString()
                    }else{
                        cv_notif_badge.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<TotalNotifResponse>, t: Throwable) {
                Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("masalah", t.message.toString())
            }
        })
    }

    // masih bermasalah di fungsi ini
    private fun postViewFavorite() {
        var data : ArrayList<Post> = ArrayList()
        data = newData.toMutableList() as ArrayList<Post>
        newData.clear()
        Log.e("abcd","3")
        for(post in data){
            var i = newDataPostFavorite.any { it.id_post == post.id }
            Log.e("abcd",i.toString())
            if (i){
                val data = Post(
                    post.id.toString().toInt(),
                    post.foto_profile.toString(),
                    post.nama.toString(),
                    post.upload.toString(),
                    post.text.toString(),
                    post.gambar.toString(),
                    post.jumlah_comment.toString().toInt(),
                    false
                )
                newData.add(data)
                Log.e("abcd",data.toString())
            }
        }
        Log.e("abcd","5")
        cl_post.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        // Perbarui tampilan RecyclerView setelah memodifikasi newData
        adapter.notifyDataSetChanged()
    }

    private fun postView() {
        val retro = Retro().getRetroClientInstance().create(PostAPI::class.java)
        retro.post("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<List<PostRespone>> {
            override fun onResponse(
                call: Call<List<PostRespone>>,
                response: Response<List<PostRespone>>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    newData.clear()
                    // Menggunakan sortedByDescending untuk mengurutkan berdasarkan tanggal terbaru
                    for (i in res!!) {
                        val data = Post(
                            i.id.toString().toInt(),
                            i.gambar_profile.toString(),
                            i.nama.toString(),
                            i.updated_at.toString(),
                            i.text.toString(),
                            i.gambar.toString(),
                            i.jumlah_comment.toString().toInt()
                        )
                        newData.add(data)
                    }
                    postViewFavorite()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<PostRespone>>, t: Throwable) {
                Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("masalah", t.message.toString())
            }
        })
    }


    private fun clearData() {
        newData.clear()
        newDataPostFavorite.clear()
        adapter.notifyDataSetChanged()
    }

    private fun postViewMe() {
        cl_post.visibility = View.VISIBLE
        loadingLottie.visibility = View.VISIBLE
        loadingLottie.playAnimation()
        recyclerView.visibility = View.GONE
        val retro = Retro().getRetroClientInstance().create(PostAPI::class.java)
        retro.postMe("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<List<PostRespone>> {
            override fun onResponse(
                call: Call<List<PostRespone>>,
                response: Response<List<PostRespone>>
            ) {
                Log.e("paaaa",response.code().toString())
                if (response.isSuccessful) {
                    Log.e("paaaa","succes")
                    val res = response.body()
                    if (!res.isNullOrEmpty()){
                        // Menggunakan sortedByDescending untuk mengurutkan berdasarkan tanggal terbaru
                        for (i in res!!) {
                            val data = Post(
                                i.id.toString().toInt(),
                                i.gambar_profile.toString(),
                                i.nama.toString(),
                                i.updated_at.toString(),
                                i.text.toString(),
                                i.gambar.toString(),
                                i.jumlah_comment.toString().toInt(),
                                true
                            )
                            newData.add(data)
                        }
                        Log.e("paaaa","as")
                        adapter.notifyDataSetChanged()
                        cl_post.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
                if (response.code() == 404){
                    cl_post.visibility = View.VISIBLE
                    nodataLottie.visibility = View.VISIBLE
                    nodataLottie.playAnimation()
                    loadingLottie.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<PostRespone>>, t: Throwable) {
                Toast.makeText(requireActivity(), t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("masalah", t.message.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        profileView()
        totalNotif()
    }

    private fun profileView() {
        val retro = Retro().getRetroClientInstance().create(ProfileAPI::class.java)
        retro.profile("Bearer ${sharedPref.getString("token")}").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                val resCode = response.code()
                if (resCode == 200){
                    Log.e("profilecaliak","success")
                    val res = response.body()!!
                    if(res.user.gambar.isNullOrEmpty()){
                        fotoProfile.setImageResource(R.drawable.baseline_person_24)
                    }else{
                        Picasso.get().load("http://213.35.121.183/images/${res.user!!.gambar}").into(fotoProfile)
                        pathFoto = "http://213.35.121.183/images/${res.user!!.gambar}"
                    }
                    nama.text = res.user.nama
                    email = res.user.email.toString()
                    namaUser = res.user.nama.toString()
                    kode_pendonor.text = res.user.kode_pendonor
                    tanggal_lahir = res.user.tanggal_lahir.toString()
                    alamat = res.user.alamat_pendonor.toString()
                    jenis_kelamin = res.user.jenis_kelamin.toString().capitalize()
                    kontak = res.user.kontak_pendonor.toString()
                    berat_badan = "${res.user.berat_badan}"
                }
                else{
                    sharedPref.setStatusLogin(false)
                    sharedPref.logOut()
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Gagal", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun pendonorLogout() {
        val retro = Retro().getRetroClientInstance().create(PendonorLogoutAPI::class.java)
        retro.logout("Bearer ${sharedPref.getString("token")}").enqueue(object :
            Callback<PendonorLogoutResponse> {
            override fun onResponse(
                call: Call<PendonorLogoutResponse>,
                response: Response<PendonorLogoutResponse>
            ) {
                val res = response.code()
                if (res == 200){
                    sharedPref.setStatusLogin(false)
                    sharedPref.logOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(requireActivity(),"Gagal", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PendonorLogoutResponse>, t: Throwable) {
                //
            }
        })
    }

    private fun showCostumeAlertDialog(type: String, id: Int? = 0) {
        val builder = AlertDialog.Builder(requireActivity())
        val customeView = LayoutInflater.from(requireActivity()).inflate(R.layout.alert,null)
        builder.setView(customeView)
        val dialog = builder.create()

        val btnYes = customeView.findViewById<Button>(R.id.btn_yes)
        val btnNo = customeView.findViewById<Button>(R.id.btn_no)
        val tv_text = customeView.findViewById<TextView>(R.id.tv_text)

        if (type == "logout"){
            tv_text.text = "Apakah kamu yakin ingin keluar?"
        }else{
            tv_text.text = "Yakin hapus postingan?"
            tv_text.gravity = Gravity.CENTER_HORIZONTAL
        }


        btnYes.setOnClickListener {
           if (type == "logout"){
               activity?.finish()
               pendonorLogout()
               Toast.makeText(requireActivity(), "Log Out", Toast.LENGTH_LONG).show()
           }else{
               if (id != null){
                   Log.e("iddelete",id.toString())
                   deletePost(id)
               }
           }
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
                        Toast.makeText(requireActivity(),"Simpan", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(requireActivity(),"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                activity?.finish()
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
                        var newDataRemoveById = newDataPostFavorite.find { it.id_post == id }
                        if (newDataRemoveById != null){
                            newDataPostFavorite.remove(newDataRemoveById)
                        }
                        Toast.makeText(requireActivity(),"Tidak si simpan", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(requireActivity(),"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                activity?.finish()
            }
        })
    }
    private fun postFavorite() {
        cl_post.visibility = View.VISIBLE
        nodataLottie.visibility = View.GONE
        loadingLottie.visibility = View.VISIBLE
        loadingLottie.playAnimation()
        recyclerView.visibility = View.GONE
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
                        if (radioGroup.checkedRadioButtonId == R.id.btn_favorite){
                            Log.e("btnfav","fav")
                            postView()
                        }
                        adapter.notifyDataSetChanged()
                    }else{
                        if (radioGroup.checkedRadioButtonId == R.id.btn_favorite){
                            cl_post.visibility = View.VISIBLE
                            loadingLottie.visibility = View.GONE
                            nodataLottie.visibility = View.VISIBLE
                            nodataLottie.setAnimation(R.raw.animation_not_jadwal)
                            nodataLottie.playAnimation()
                            recyclerView.visibility = View.GONE
                        }

                    }
                }else{
                    Toast.makeText(requireActivity(),"terjaadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PostFavoriteResponse>>, t: Throwable) {
                Toast.makeText(requireActivity(),"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                activity?.finish()
            }
        })
    }

    //postView

    override fun onDataReceived(nama: String, id: Int) {
        // ngak perlu di isi
    }

    override fun onDataReceivedFavorite(id: Int) {
        addPostFavorite(id)
    }

    override fun onDeleteFavorite(id: Int) {
        deletePostFavorite(id)
    }

    override fun onDeletePost(id: Int) {
//        deletePost(id)
        showCostumeAlertDialog("delete",id)
    }

    override fun onAddLaporan(laporan: Laporan) {
        addLaporan(laporan)
    }

    private fun deletePost(id: Int) {
        val retro = Retro().getRetroClientInstance().create(PostAPI::class.java)
        retro.deletePost("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<PostResponse2> {
            override fun onResponse(
                call: Call<PostResponse2>,
                response: Response<PostResponse2>
            ) {
                Log.e("iddelete", "${response.code().toString()},${response.body().toString()}")
                if (response.isSuccessful){
                    var res = response.body()
                    if (res?.success == true){
                        Toast.makeText(requireActivity(),"Berhasil Hapus Postingan", Toast.LENGTH_SHORT).show()
                        clearData()
                        postViewMe()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostResponse2>, t: Throwable) {
                Toast.makeText(requireActivity(),"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                activity?.finish()
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
                        Toast.makeText(requireActivity(),"Laporan Dikirim",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireActivity(),"Laporan tidak terkirim",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LaporanResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                activity?.finish()
            }
        })
    }
}