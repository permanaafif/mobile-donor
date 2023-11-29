package com.afifpermana.donor.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.ArtikelActivity
import com.afifpermana.donor.CommentsActivity
import com.afifpermana.donor.OtherDonorProfileActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Laporan
import com.afifpermana.donor.model.Post
import com.afifpermana.donor.model.PostFavorite
import com.afifpermana.donor.model.PostFavoriteResponse2
import com.afifpermana.donor.service.CallBackData
import com.afifpermana.donor.service.PostFavoriteAPI
import com.afifpermana.donor.util.ConnectivityChecker
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Callback

class PostAdapter(
    private val listPost : List<Post>,
    private val listPostFavorite : List<PostFavorite>,
    private val context: Context,
    private var dataCallBack: CallBackData,
    private var sharedPref: SharedPrefLogin
): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var id_pendonor_now = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_post_diskusi, parent, false)
    )

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = listPost[position]
        var path_foto_profile = "http://138.2.74.142/images/${post.foto_profile}"
        if (post.foto_profile != "null"){
            Picasso.get().load(path_foto_profile).into(holder.foto_profile)
            holder.foto_profile.setOnClickListener{
                showAlertGambar(holder.itemView.context,path_foto_profile)
            }
        }

        if (post.foto_profile == "null" || post.foto_profile.isNullOrEmpty()){
            holder.foto_profile.setImageResource(R.drawable.baseline_person_24)
        }
        Log.e("ibra",post.toString())
        holder.nama.text = post.nama.toString().capitalize()
        holder.nama.setOnClickListener {
            val id_pendonor = sharedPref.getInt("id")
            if (id_pendonor != post.id_pendonor){
                if (id_pendonor_now != post.id_pendonor){
                    val connectivityChecker = ConnectivityChecker(context)
                    if (connectivityChecker.isNetworkAvailable()){
                        //koneksi aktif
                        val context = it.context
                        val i = Intent(context, OtherDonorProfileActivity::class.java)
                        i.putExtra("id_pendonor",post.id_pendonor)
                        context.startActivity(i)
                    }else{
                        //koneksi tidak aktif
                        connectivityChecker.showAlertDialogNoConnection()
                    }
                }
            }
        }
        holder.upload.text = post.upload
        // Di dalam metode onBindViewHolder
        if (post.text.isNullOrEmpty() || post.text == "null") {
            holder.text?.visibility = View.GONE
        } else {
            holder.text.text = post.text
            holder.text.viewTreeObserver.addOnGlobalLayoutListener {
                val lineCount = holder.text.lineCount
                if (lineCount > 4 || lineCount == 4) {
                    holder.textButton.visibility = View.VISIBLE
                } else {
                    holder.textButton.visibility = View.GONE
                }
            }
        }

        var isExpanded = false // Status awal adalah teks dipotong

        holder.textButton.setOnClickListener {
            isExpanded = !isExpanded // Toggle status

            if (isExpanded) {
                // Jika saat ini dipotong, tampilkan seluruh teks
                holder.text.maxLines = Int.MAX_VALUE
                holder.text.ellipsize = null
                holder.textButton.text = "Sembunyikan"
            } else {
                // Jika saat ini ditampilkan secara penuh, potong teks
                holder.text.maxLines = 4
                holder.text.ellipsize = TextUtils.TruncateAt.END
                holder.textButton.text = "Selengkapnya"
            }
        }

        var path_gambar = "http://138.2.74.142/assets/post/${post.gambar}"
        holder.gambar.maxHeight = 250
        if (post.gambar.toString() != "null"){
            Picasso.get().load(path_gambar).into(holder.gambar)
            holder.gambar?.visibility = View.VISIBLE
            holder.gambar.setOnClickListener{
                showAlertGambar(holder.itemView.context,path_gambar)
            }
        }else{
            holder.gambar?.visibility = View.GONE
        }

        holder.jumlah_comment.text = "${post.jumlah_comment.toString()} comments"
        holder.btn_comment.setOnClickListener {
            val connectivityChecker = ConnectivityChecker(context)
            if (connectivityChecker.isNetworkAvailable()){
                //koneksi aktif
                val context = it.context
                val i = Intent(context, CommentsActivity::class.java)
                i.putExtra("id_post",post.id)
                i.putExtra("id_pendonor",id_pendonor_now)
//            i.putExtra("foto_profile",path_foto_profile)
//            i.putExtra("nama",post.nama)
//            i.putExtra("upload",post.upload)
//            i.putExtra("gambar",path_gambar)
//            i.putExtra("jumlah_comment",post.jumlah_comment.toString())
                context.startActivity(i)
            }else{
                //koneksi tidak aktif
                connectivityChecker.showAlertDialogNoConnection()
            }
        }

        holder.btn_report.setOnClickListener {
            showCostumeAlertDialog(holder.itemView.context, post.id)
        }

        var isExpandFavorite = false
        var id = listPostFavorite.any { it.id_post == post.id }
        Log.e("listfavprite", id.toString())
        if (id){
            isExpandFavorite = true
            holder.btn_favorite.setImageResource(R.drawable.baseline_simpan_post_ok)
        }else{
            isExpandFavorite = false
            holder.btn_favorite.setImageResource(R.drawable.baseline_simpan_post)
        }

        holder.btn_favorite.setOnClickListener {
            val connectivityChecker = ConnectivityChecker(context)
            if (connectivityChecker.isNetworkAvailable()){
                //koneksi aktif
                isExpandFavorite = !isExpandFavorite // Toggle

                if (isExpandFavorite) {
                    dataCallBack.onDataReceivedFavorite(post.id)
                    isExpandFavorite = true
                    holder.btn_favorite.setImageResource(R.drawable.baseline_simpan_post_ok)
                } else {
                    dataCallBack.onDeleteFavorite(post.id)
                    isExpandFavorite = false
                    holder.btn_favorite.setImageResource(R.drawable.baseline_simpan_post)
                }
            }else{
                //koneksi tidak aktif
//                connectivityChecker.showAlertDialogNoConnection()
                Toast.makeText(context, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }

        }

        if (post.status == false){
            holder.btn_delete.visibility = View.GONE
        }else if (post.status == true){
            holder.btn_delete.visibility = View.VISIBLE
            holder.btn_delete.setOnClickListener {
                val connectivityChecker = ConnectivityChecker(context)
                if (connectivityChecker.isNetworkAvailable()){
                    //koneksi aktif
                    dataCallBack.onDeletePost(post.id)
                }else{
                    //koneksi tidak aktif
                    Toast.makeText(context, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            holder.btn_delete.visibility = View.GONE
        }
    }

    override fun getItemCount()= listPost.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val foto_profile = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val upload = view.findViewById<TextView>(R.id.tanggal_upload)
        val text = view.findViewById<TextView>(R.id.text)
        val textButton = view.findViewById<TextView>(R.id.textButton)
        val gambar = view.findViewById<ImageView>(R.id.image_post)
        val btn_comment = view.findViewById<LinearLayout>(R.id.ll_comment)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete)
        val btn_report = view.findViewById<ImageView>(R.id.btn_report)
        val btn_favorite = view.findViewById<ImageView>(R.id.btn_favorit)
        val jumlah_comment = view.findViewById<TextView>(R.id.tv_jumlah_comment)
    }

    private fun showCostumeAlertDialog(context: Context, id:Int) {
        val builder = AlertDialog.Builder(context)
        val customeView = LayoutInflater.from(context).inflate(R.layout.alert_report,null)
        builder.setView(customeView)
        val dialog = builder.create()
        // Tambahkan ini untuk menghindari menutup dialog saat menekan di luar area dialog
        dialog.setCanceledOnTouchOutside(false)
        val close = customeView.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dialog.dismiss()
        }

        val text = customeView.findViewById<TextView>(R.id.pesan)
        val type = "Postingan"
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
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                } else {
                    // Panjang teks 0, atur backgroundTint ke warna lain atau null (kembalikan ke default)
                    post.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
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
                val laporan = Laporan(id,null,null,textLength,type)
                val connectivityChecker = ConnectivityChecker(context)
                if (connectivityChecker.isNetworkAvailable()){
                    //koneksi aktif
                    dataCallBack.onAddLaporan(laporan)
                    dialog.dismiss()
                }else{
                    //koneksi tidak aktif
                    connectivityChecker.showAlertDialogNoConnection()
                }
            }else{
                textHelper.text = "Tulis laporan ..."
                textHelper.visibility = View.VISIBLE
            }
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showAlertGambar(context: Context, path:String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val customeView = LayoutInflater.from(context).inflate(R.layout.alert_gambar,null)
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

    fun idPendonorNow(id: Int){
        id_pendonor_now = id
//        Log.e("balaskoment", id.toString())
//        Log.e("balaskoment", balasComment.toString())
//        notifyDataSetChanged()
    }
}
