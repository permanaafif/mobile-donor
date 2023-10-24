package com.afifpermana.donor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.CommentAdapter
import com.afifpermana.donor.adapter.PostAdapter
import com.afifpermana.donor.model.CommentResponse
import com.afifpermana.donor.model.Comments
import com.afifpermana.donor.model.LokasiDonorResponse
import com.afifpermana.donor.model.Post
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.model.RiwayatDonor
import com.afifpermana.donor.service.CommentAPI
import com.afifpermana.donor.service.JadwalUserAPI
import com.afifpermana.donor.service.PostAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsActivity : AppCompatActivity() {
    private lateinit var gambar_profile : CircleImageView
    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var nama : TextView
    private lateinit var tgl_upload : TextView
    private lateinit var text : TextView
    private lateinit var ll_text : LinearLayout
    private lateinit var textButton : TextView
    private lateinit var gambar : ImageView
    private lateinit var jumlah_comment : TextView

    private lateinit var adapter: CommentAdapter
    private lateinit var recyclerView : RecyclerView
    var newData : ArrayList<Comments> = ArrayList()

    var b : Bundle? =null
    lateinit var sharedPref: SharedPrefLogin
    var id_post = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
        val layoutManager = LinearLayoutManager(this)
        sw_layout = findViewById(R.id.swlayout)
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)
        recyclerView = findViewById(R.id.rv_coments)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CommentAdapter(newData)
        recyclerView.adapter = adapter

        sharedPref = SharedPrefLogin(this)
        gambar_profile = findViewById(R.id.foto_profile)
        nama = findViewById(R.id.nama)
        tgl_upload = findViewById(R.id.tanggal_upload)
        text = findViewById(R.id.text)
        ll_text = findViewById(R.id.ll_text)
        textButton = findViewById(R.id.textButton)
        gambar = findViewById(R.id.image_post)
        jumlah_comment = findViewById(R.id.tv_jumlah_comment)
        b = intent.extras
        id_post = b!!.getInt("id_post")
        findPost(id_post)
        commentView(id_post)
        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                findPost(id_post)
                sw_layout.isRefreshing = false
            }, 1000)
        }
    }

    private fun clearData() {
        newData.clear()
        adapter.notifyDataSetChanged()
    }

    private fun commentView(id:Int) {
        val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
        retro.comment("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<List<CommentResponse>> {
            override fun onResponse(
                call: Call<List<CommentResponse>>,
                response: Response<List<CommentResponse>>
            ) {
                Log.e("masalahnya", response.code().toString())
                if (response.code() == 404){
                    Toast.makeText(this@CommentsActivity,"Comment Belum ada", Toast.LENGTH_SHORT).show()
                }
                if (response.isSuccessful) {
                    Log.e("masalahnya", "success")
                    val res = response.body()
                    // Menggunakan sortedByDescending untuk mengurutkan berdasarkan tanggal terbaru
                    for (i in res!!) {
                        val data = Comments(
                            i.id_post.toString().toInt(),
                            i.id_comment.toString().toInt(),
                            i.id_pendonor.toString().toInt(),
                            i.nama.toString(),
                            i.gambar.toString(),
                            i.text.toString(),
                            i.created_at.toString(),
                            i.update_at.toString()
                        )
                        newData.add(data)
                    }
                    adapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this@CommentsActivity,"Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
            }
            })
        }

    private fun findPost(id:Int) {
        val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
        retro.findPost("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<PostRespone> {
            override fun onResponse(call: Call<PostRespone>, response: Response<PostRespone>) {
                var res = response.body()
                if (response.isSuccessful){
                    if(res?.gambar_profile.toString() != "null"){
                        Picasso.get().load("http://10.0.2.2:8000/images/${res?.gambar_profile}").into(gambar_profile)
                    }
                    nama.text = res?.nama
                    tgl_upload.text = res?.updated_at
                    Log.e("tgl_upload",res?.updated_at.toString())

                    if (res?.text.toString() != "null"){
                        text.text = res?.text
                        ll_text.visibility = View.VISIBLE
                    }

                    if (res?.text.isNullOrEmpty()) {
                        text?.visibility = View.GONE
                    } else {
                        text.text = res?.text
                        text.viewTreeObserver.addOnGlobalLayoutListener {
                            val lineCount = text.lineCount
                            if (lineCount > 3) {
                                textButton.visibility = View.VISIBLE
                            } else {
                                textButton.visibility = View.GONE
                            }
                        }
                    }

                    var isExpanded = false // Status awal adalah teks dipotong
                    textButton.setOnClickListener {
                        isExpanded = !isExpanded // Toggle status

                        if (isExpanded) {
                            // Jika saat ini dipotong, tampilkan seluruh teks
                            text.maxLines = Int.MAX_VALUE
                            text.ellipsize = null
                            textButton.text = "Sembunyikan"
                        } else {
                            // Jika saat ini ditampilkan secara penuh, potong teks
                            text.maxLines = 4
                            text.ellipsize = TextUtils.TruncateAt.END
                            textButton.text = "Selengkapnya"
                        }
                    }

                    if (res?.gambar.toString() != "null"){
                        Picasso.get().load("http://10.0.2.2:8000/assets/post/${res?.gambar}").into(gambar)
                        gambar.visibility = View.VISIBLE
                    }

                    if (res?.jumlah_comment.toString() != "null"){
                        jumlah_comment.text = "${res?.jumlah_comment.toString()} Comments"
                    }
                }
            }

            override fun onFailure(call: Call<PostRespone>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
            }
        })
    }
}