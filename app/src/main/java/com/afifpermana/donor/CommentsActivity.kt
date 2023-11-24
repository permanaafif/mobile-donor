package com.afifpermana.donor

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afifpermana.donor.adapter.CommentAdapter
import com.afifpermana.donor.model.AddBalasCommentRequest
import com.afifpermana.donor.model.AddBalasCommentResponse
import com.afifpermana.donor.model.BalasCommentRequest
import com.afifpermana.donor.model.BalasCommentResponse
import com.afifpermana.donor.model.BalasCommentTo
import com.afifpermana.donor.model.Comments
import com.afifpermana.donor.model.Laporan
import com.afifpermana.donor.model.LaporanRequest
import com.afifpermana.donor.model.LaporanResponse
import com.afifpermana.donor.model.PostFavoriteResponse2
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.service.CallBackData
import com.afifpermana.donor.service.CommentAPI
import com.afifpermana.donor.service.LaporanAPI
import com.afifpermana.donor.service.PostFavoriteAPI
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.airbnb.lottie.LottieAnimationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsActivity : AppCompatActivity(), CallBackData {
    private lateinit var balas_comment_to : TextView
    private lateinit var et_comment : EditText
    private lateinit var send_comment : ImageView

    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var cl_comment : ConstraintLayout
    private lateinit var loadingLottie : LottieAnimationView
    private lateinit var nodataLottie : LottieAnimationView

    private lateinit var gambar_profile : CircleImageView
    private lateinit var sw_layout : SwipeRefreshLayout
    private lateinit var nama : TextView
    private lateinit var tgl_upload : TextView
    private lateinit var text : TextView
    private lateinit var textHelper : TextView
    private lateinit var ll_text : LinearLayout
    private lateinit var textButton : TextView
    private lateinit var gambar : ImageView
    private lateinit var jumlah_comment : TextView

    private lateinit var btn_comment : ImageView
    private lateinit var btn_report : ImageView
    private lateinit var btn_favorit : ImageView

    private lateinit var ll_comment : LinearLayout
    private lateinit var ll_balas_comment : LinearLayout
    private lateinit var close : ImageView

    private lateinit var adapter: CommentAdapter
    private lateinit var recyclerView : RecyclerView
    var newData : ArrayList<Comments> = ArrayList()
    var newDataBalasComment : ArrayList<BalasCommentTo> = ArrayList()
    var b : Bundle? =null
    lateinit var sharedPref: SharedPrefLogin
    var id_post = 0
    var id_pendonor_now = 0
    var id_comment = 0
    var id_balas_comment = 0
    var id_commentar : Int? = null
    var statusComment = false
    var statusPostFavorit = false


    val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        sharedPref = SharedPrefLogin(this)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { onBackPressed() }
        val layoutManager = LinearLayoutManager(this)
        sw_layout = findViewById(R.id.swlayout)
        sw_layout.setColorSchemeResources(R.color.blue,R.color.red)

        nestedScrollView = findViewById(R.id.nested)
        cl_comment = findViewById(R.id.cl_comment)
        loadingLottie = findViewById(R.id.loading)
        nodataLottie = findViewById(R.id.no_data)

        btn_comment = findViewById(R.id.btn_comment)
        btn_report = findViewById(R.id.btn_report)
        btn_favorit = findViewById(R.id.btn_favorit)
        recyclerView = findViewById(R.id.rv_coments)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CommentAdapter(newData,this,sharedPref)
        ll_comment = findViewById(R.id.ll)
        ll_balas_comment = findViewById(R.id.ll_balas_comment)
        close = findViewById(R.id.close)
        recyclerView.adapter = adapter

        b = intent.extras
        id_post = b!!.getInt("id_post")
        id_pendonor_now = b!!.getInt("id_pendonor")

        checkPostFavorite(id_post)
        btn_comment.setOnClickListener {
            // Meminta fokus ke EditText
            et_comment.requestFocus()

            // Tampilkan keyboard (jika diperlukan)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT)
        }
        btn_report.setOnClickListener {
            showCostumeAlertDialog(id_post)
        }
        btn_favorit.setOnClickListener {
            statusPostFavorit = !statusPostFavorit

            if(statusPostFavorit){
                addPostFavorite(id_post)
                btn_favorit.setImageResource(R.drawable.baseline_simpan_post_ok)
            }else{
                deletePostFavorite(id_post)
                btn_favorit.setImageResource(R.drawable.baseline_simpan_post)
            }
        }

        balas_comment_to = findViewById(R.id.balas_comment_to)
        textHelper = findViewById(R.id.helper)
        et_comment = findViewById(R.id.pesan)
        et_comment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                if (textLength > 0) {
                    // Panjang teks lebih dari 0, atur backgroundTint ke warna yang Anda inginkan
                    send_comment.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CommentsActivity, R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    send_comment.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CommentsActivity, R.color.grey))
                }

                if (textLength >= 300){
                    textHelper.visibility = View.VISIBLE
                    textHelper.text = "Komentar tidak boleh lebih dari 300 karakter"
                }else{
                    textHelper.visibility = View.GONE
                }
            }
        })

        send_comment = findViewById(R.id.send)
        send_comment.setOnClickListener {
            val textValue = et_comment.text.toString().trim()
            if (textValue.isNotBlank() ){
                if (statusComment == false){
                    addComment(id_post,textValue)
                }else{
                    balasComment(id_commentar!!.toInt(),textValue)
                }
            }else{
                textHelper.visibility = View.VISIBLE
                textHelper.text = "Tulis Komentar kamu ..."
            }
        }
        gambar_profile = findViewById(R.id.foto_profile)
        nama = findViewById(R.id.nama)
        tgl_upload = findViewById(R.id.tanggal_upload)
        text = findViewById(R.id.text)
        ll_text = findViewById(R.id.ll_text)
        textButton = findViewById(R.id.textButton)
        gambar = findViewById(R.id.image_post)
        jumlah_comment = findViewById(R.id.tv_jumlah_comment)
        findPost(id_post)

        commentView(id_post, scope)

        sw_layout.setOnRefreshListener{
            val Handler = Handler(Looper.getMainLooper())
            Handler().postDelayed(Runnable {
                clearData()
                id_comment = 0
                findPost(id_post)
                commentView(id_post,scope)
                sw_layout.isRefreshing = false
            }, 1000)
        }

        close.setOnClickListener {
            ll_balas_comment.visibility = View.GONE
            balas_comment_to.text = ""
            statusComment = false
            Log.e("statuscomment", "false")
        }

        id_comment = b!!.getInt("id_comment")
        id_balas_comment = b!!.getInt("id_balas_comment")

        if (id_balas_comment != 0){
            adapter.balasanComment(id_comment)
        }

        if (id_pendonor_now != 0){
            adapter.idPendonorNow(id_pendonor_now)
        }

        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // scrollY berisi posisi scroll vertikal
            // Lakukan sesuatu dengan nilai scrollY
            // Misalnya, tampilkan dalam log
            id_comment = 0
           Log.e("nested",scrollY.toString())
        }


        //berjalan ketika ui sudah selesai di buat
        nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener {
        if(id_comment != 0){
            val targetItemId = id_comment // Gantilah dengan ID item yang sesuai
            if (layoutManager != null && layoutManager is LinearLayoutManager) {
                val adapter = recyclerView.adapter
                if (adapter != null) {
                    val itemCount = adapter.itemCount
                    for (i in 0 until itemCount) {
                        val item = newData[i] // Gantilah newData dengan sumber data Anda
                        if (item.id_comment == targetItemId) {
                            // Temukan posisi item yang sesuai, lalu geser scroll ke posisi tersebut
                            val position = i
                            Log.e("nestedddd",position.toString())
                            nestedScrollView.post {
                                nestedScrollView.smoothScrollTo(0, recyclerView.getChildAt(position)?.top ?: 0)
                                // atau nestedScrollView.scrollTo(0, recyclerView.getChildAt(position)?.top ?: 0)
                            }
                            break
                        }
                    }
                }
            }
        }
        }
    }

    private fun checkPostFavorite(id: Int) {
        val retro = Retro().getRetroClientInstance().create(PostFavoriteAPI::class.java)
        retro.checkPostFavorite("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<PostFavoriteResponse2> {
            override fun onResponse(
                call: Call<PostFavoriteResponse2>,
                response: Response<PostFavoriteResponse2>
            ) {
                if (response.isSuccessful){
                    var res = response.body()
                    if (res?.success == true){
                        statusPostFavorit = true
                        btn_favorit.setImageResource(R.drawable.baseline_simpan_post_ok)
                    }else{
                        statusPostFavorit = false
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
            }
        })
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
                        Toast.makeText(this@CommentsActivity,"Simpan", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
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
                        Toast.makeText(this@CommentsActivity,"Tidak si simpan", Toast.LENGTH_SHORT).show()
                    }else{
                        //
                    }
                }
            }

            override fun onFailure(call: Call<PostFavoriteResponse2>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
            }
        })
    }

    override fun onDataReceived(nama: String, id:Int) {
        balas_comment_to.text = "Balas ke $nama"
        ll_balas_comment.visibility = View.VISIBLE
        if (balas_comment_to.text.trim().isNotEmpty()){
            statusComment = true
            id_commentar = id
        }else{
            statusComment = false
            id_commentar = null
        }
    }

    override fun onDataReceivedFavorite(id: Int) {
        // ngak perlu di isi
    }

    override fun onDeleteFavorite(id: Int) {
        //
    }

    override fun onDeletePost(id: Int) {
        // ngak perlu di isi
    }

    override fun onAddLaporan(laporan: Laporan) {
        addLaporan(laporan)
    }

    private fun balasComment(id: Int, text:String) {
        var data = AddBalasCommentRequest()
        data.id_comment = id
        data.text = text
        val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
        retro.addBalasComment("Bearer ${sharedPref.getString("token")}",data).enqueue(object :
            Callback<AddBalasCommentResponse> {
            override fun onResponse(
                call: Call<AddBalasCommentResponse>,
                response: Response<AddBalasCommentResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    Log.e("balasComment", res!!.message.toString())
                    if (res!!.success == true){
                        Toast.makeText(this@CommentsActivity,"Berhasil", Toast.LENGTH_SHORT).show()
                    }else{
//                        Toast.makeText(this@CommentsActivity,"Gagal", Toast.LENGTH_SHORT).show()
                        Log.e("balasComment2", res!!.message.toString())
                    }
                    et_comment.text.clear()
                    clearData()
                    findPost(id)
                    commentView(id_post,scope)
                }else{
                    Toast.makeText(this@CommentsActivity,"Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddBalasCommentResponse>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                Log.e("balasComment3",t.message.toString())
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
            }
        })
    }

    private fun addComment(id:Int, text:String) {
        var data = BalasCommentRequest()
        data.id_post = id
        data.text = text
        Log.e("pesan",text)
        val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
        retro.addComment("Bearer ${sharedPref.getString("token")}",data).enqueue(object :
            Callback<BalasCommentResponse> {
            override fun onResponse(
                call: Call<BalasCommentResponse>,
                response: Response<BalasCommentResponse>
            ) {
                if (response.isSuccessful){
                    val res = response.body()
                    Log.e("pesan", res!!.message.toString())
                    if (res!!.success == true){
                        Toast.makeText(this@CommentsActivity,"Berhasil", Toast.LENGTH_SHORT).show()
                    }else{
//                        Toast.makeText(this@CommentsActivity,"Gagal", Toast.LENGTH_SHORT).show()
                        Log.e("pesan", res!!.message.toString())
                    }
                    et_comment.text.clear()
                    clearData()
                    findPost(id)
                    commentView(id,scope)
                }else{
                    Toast.makeText(this@CommentsActivity,"Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BalasCommentResponse>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
            }
        })
    }

    private fun clearData() {
        newData.clear()
        newDataBalasComment.clear()
        adapter.notifyDataSetChanged()
    }

    private fun commentView(id: Int, scope: CoroutineScope) {
        cl_comment.visibility = View.VISIBLE
        loadingLottie.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        scope.launch {
            try {
                val postResponse = withContext(Dispatchers.IO) {
                    val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
                    val response = retro.comment("Bearer ${sharedPref.getString("token")}", id).execute()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        null
                    }
                }

                if (postResponse == null) {
                    cl_comment.visibility = View.VISIBLE
                    loadingLottie.visibility = View.GONE
                    nodataLottie.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    // Handle kesalahan jika diperlukan
                    return@launch
                }

                val comments = postResponse.map { commentResponse ->
                    val balasComments = viewBalasComment(commentResponse.id_comment!!.toInt(), scope)
                    Comments(
                        commentResponse.id_post!!.toInt(),
                        commentResponse.id_comment!!.toInt(),
                        commentResponse.id_pendonor!!.toInt(),
                        commentResponse.nama.toString(),
                        commentResponse.gambar.toString(),
                        commentResponse.text.toString(),
                        commentResponse.created_at.toString(),
                        commentResponse.update_at.toString(),
                        commentResponse.jumlah_balasan!!.toInt(),
                        balasComments
                    )
                }

                newData.addAll(comments)
                adapter.notifyDataSetChanged()
                cl_comment.visibility = View.GONE
                loadingLottie.visibility = View.GONE
                nodataLottie.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } catch (e: Exception) {
                // Handle kesalahan jika diperlukan
            }
        }
    }

    private suspend fun viewBalasComment(id: Int, scope: CoroutineScope): List<BalasCommentTo> = withContext(Dispatchers.IO) {
        val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
        val response = retro.balasComment("Bearer ${sharedPref.getString("token")}", id).execute()
        if (response.isSuccessful) {
            val res = response.body()
            res?.map { balasCommentToResponse ->
                BalasCommentTo(
                    balasCommentToResponse.id!!.toInt(),
                    balasCommentToResponse.id_comment!!.toInt(),
                    balasCommentToResponse.id_pendonor!!.toInt(),
                    balasCommentToResponse.nama.toString(),
                    balasCommentToResponse.gambar.toString(),
                    balasCommentToResponse.text.toString(),
                    balasCommentToResponse.created_at.toString(),
                    balasCommentToResponse.update_at.toString()
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun findPost(id:Int) {
        val retro = Retro().getRetroClientInstance().create(CommentAPI::class.java)
        retro.findPost("Bearer ${sharedPref.getString("token")}",id).enqueue(object :
            Callback<PostRespone> {
            override fun onResponse(call: Call<PostRespone>, response: Response<PostRespone>) {
                var res = response.body()
                if (response.isSuccessful){
                    if(res?.gambar_profile.toString() != "null"){
                        val path = "http://138.2.74.142/images/${res?.gambar_profile}"
                        Picasso.get().load(path).into(gambar_profile)
                        gambar_profile.setOnClickListener {
                            showAlertGambar(path)
                        }
                    }
                    nama.text = res?.nama.toString().capitalize()
                    nama.setOnClickListener {
                        val id_pendonor = sharedPref.getInt("id")
                        if (id_pendonor != res?.id_pendonor){
                            if (id_pendonor_now != res?.id_pendonor){
                                val context = it.context
                                val i = Intent(context, OtherDonorProfileActivity::class.java)
                                i.putExtra("id_pendonor",res?.id_pendonor)
                                context.startActivity(i)
                            }
                        }
                    }
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
                        Picasso.get().load("http://138.2.74.142/assets/post/${res?.gambar}").into(gambar)
                        gambar.visibility = View.VISIBLE
                    }

                    if (res?.jumlah_comment.toString() != "null"){
                        jumlah_comment.text = "${res?.jumlah_comment.toString()} Comments"
                    }
                }
            }

            override fun onFailure(call: Call<PostRespone>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                Log.e("masalah",t.message.toString())
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
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
    private fun showCostumeAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        val customeView = LayoutInflater.from(this).inflate(R.layout.alert_report,null)
        builder.setView(customeView)
        val dialog = builder.create()
        // Tambahkan ini untuk menghindari menutup dialog saat menekan di luar area dialog
        dialog.setCanceledOnTouchOutside(false)
        val close = customeView.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog.dismiss()
        }

        val text = customeView.findViewById<TextView>(R.id.pesan)
        val type ="Postingan"
        val textHelper = customeView.findViewById<TextView>(R.id.helper)
        val post = customeView.findViewById<ImageView>(R.id.send)
        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                if (textLength > 0) {
                    // Panjang teks lebih dari 0, atur backgroundTint ke warna yang Anda inginkan
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CommentsActivity, R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CommentsActivity, R.color.grey))
                }

                if (textLength >= 300){
                    textHelper.visibility = View.VISIBLE
                    textHelper.text = "laporan hanya boleh 300 karakter"
                }else{
                    textHelper.visibility = View.GONE
                }
            }
        })

        post.setOnClickListener {
            val textLength = text.text.toString().trim()
            if (textLength.isNotBlank()){
                val laporan = Laporan(id,null,null,textLength,type )
                addLaporan(laporan)
                dialog.dismiss()
            }else{
                textHelper.text = "Tulis laporan ..."
                textHelper.visibility = View.VISIBLE
            }
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
                        Toast.makeText(this@CommentsActivity,"Laporan Dikirim",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@CommentsActivity,"Laporan tidak terkirim",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LaporanResponse>, t: Throwable) {
                Toast.makeText(this@CommentsActivity,"Sesi kamu habis", Toast.LENGTH_SHORT).show()
                sharedPref.logOut()
                sharedPref.setStatusLogin(false)
                startActivity(Intent(this@CommentsActivity, LoginActivity::class.java))
                finish()
                Log.e("masalah",t.message.toString())
            }
        })
    }
}