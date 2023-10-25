package com.afifpermana.donor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.R
import com.afifpermana.donor.model.BalasCommentTo
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class BalasCommentAdapter(
    private var listBalasComment : List<BalasCommentTo>
): RecyclerView.Adapter<BalasCommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_balas_comment, parent, false)
    )

    override fun onBindViewHolder(holder: BalasCommentAdapter.ViewHolder, position: Int) {
        val balasComment = listBalasComment[position]
        var path_foto_profile = "http://10.0.2.2:8000/images/${balasComment.gambar}"
        if (!balasComment.gambar.isNullOrEmpty() || balasComment.gambar != "null"){
            Picasso.get().load(path_foto_profile).into(holder.foto_profile)
        }
        if (balasComment.gambar == "null" || balasComment.gambar.isNullOrEmpty()){
            holder.foto_profile.setImageResource(R.drawable.baseline_person_24)
        }

        holder.nama.text = balasComment.nama
        holder.tv_comment.text = balasComment.text
        holder.tgl_comment.text = balasComment.updated_at
    }

    override fun getItemCount() = listBalasComment.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val foto_profile = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val tv_comment = view.findViewById<TextView>(R.id.tv_comment)
        val tgl_comment = view.findViewById<TextView>(R.id.tgl_comment)
        val btn_report = view.findViewById<ImageView>(R.id.btn_report)

    }
}