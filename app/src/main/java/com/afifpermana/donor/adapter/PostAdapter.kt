package com.afifpermana.donor.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
        if (post.foto_profile != "null"){
            Picasso.get().load("http://10.0.2.2:8000/images/${post.foto_profile}").into(holder.foto_profile)
        }
        holder.nama.text = post.nama
        holder.upload.text = post.upload
        // Di dalam metode onBindViewHolder
        if (post.text.isNullOrEmpty()) {
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

        if (holder.gambar != null){
            Picasso.get().load("http://10.0.2.2:8000/assets/post/${post.gambar}").into(holder.gambar)
        }else{
            holder.gambar?.visibility = View.GONE
        }

        holder.jumlah_comment.text = "${post.jumlah_comment.toString()} comments"

    }

    override fun getItemCount()= listPost.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val foto_profile = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val upload = view.findViewById<TextView>(R.id.tanggal_upload)
        val text = view.findViewById<TextView>(R.id.text)
        val textButton = view.findViewById<TextView>(R.id.textButton)
        val gambar = view.findViewById<ImageView>(R.id.image_post)
        val jumlah_comment = view.findViewById<TextView>(R.id.tv_jumlah_comment)
    }
}
