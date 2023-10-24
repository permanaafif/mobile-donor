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
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.ArtikelActivity
import com.afifpermana.donor.CommentsActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Post
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(
    private val listPost : List<Post>
): RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_post_diskusi, parent, false)
    )

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = listPost[position]
        var path_foto_profile = "http://10.0.2.2:8000/images/${post.foto_profile}"
        if (post.foto_profile != "null"){
            Picasso.get().load(path_foto_profile).into(holder.foto_profile)
        }

        if (post.foto_profile == "null" || post.foto_profile.isNullOrEmpty()){
            holder.foto_profile.setImageResource(R.drawable.baseline_person_24)
        }
        Log.e("ibra",post.toString())
        holder.nama.text = post.nama
        holder.upload.text = post.upload
        // Di dalam metode onBindViewHolder
        if (post.text.isNullOrEmpty() || post.text == "null") {
            holder.text?.visibility = View.GONE
        } else {
            holder.text.text = post.text
            holder.text.viewTreeObserver.addOnGlobalLayoutListener {
                val lineCount = holder.text.lineCount
                if (lineCount > 3) {
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

        var path_gambar = "http://10.0.2.2:8000/assets/post/${post.gambar}"
        if (holder.gambar != null){
            Picasso.get().load(path_gambar).into(holder.gambar)
        }else{
            holder.gambar?.visibility = View.GONE
        }

        holder.jumlah_comment.text = "${post.jumlah_comment.toString()} comments"
        holder.btn_comment.setOnClickListener {
            val context = it.context
            val i = Intent(context, CommentsActivity::class.java)
            i.putExtra("id_post",post.id)
//            i.putExtra("foto_profile",path_foto_profile)
//            i.putExtra("nama",post.nama)
//            i.putExtra("upload",post.upload)
//            i.putExtra("gambar",path_gambar)
//            i.putExtra("jumlah_comment",post.jumlah_comment.toString())
            context.startActivity(i)
        }

        holder.btn_report.setOnClickListener {
            showCostumeAlertDialog(holder.itemView.context)
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
        val btn_comment = view.findViewById<ImageView>(R.id.btn_comment)
        val btn_report = view.findViewById<ImageView>(R.id.btn_report)
        val jumlah_comment = view.findViewById<TextView>(R.id.tv_jumlah_comment)
    }

    private fun showCostumeAlertDialog(context: Context) {
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

            }else{
                textHelper.text = "Tulis laporan ..."
                textHelper.visibility = View.VISIBLE
            }
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
