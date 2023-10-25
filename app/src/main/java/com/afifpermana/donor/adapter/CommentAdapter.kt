package com.afifpermana.donor.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Comments
import com.afifpermana.donor.service.CallBackData
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(
    private var listComment : List<Comments>,
    private var dataCallBack: CallBackData
): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_comment, parent, false)
    )

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        val comment = listComment[position]
        var path_foto_profile = "http://10.0.2.2:8000/images/${comment.gambar}"
        if (!comment.gambar.isNullOrEmpty() || comment.gambar != "null"){
            Picasso.get().load(path_foto_profile).into(holder.foto_profile)
        }
        if (comment.gambar == "null" || comment.gambar.isNullOrEmpty()){
            holder.foto_profile.setImageResource(R.drawable.baseline_person_24)
        }

        holder.nama.text = comment.nama
        holder.tv_comment.text = comment.text
        holder.tgl_comment.text = comment.updated_at

        holder.balas.setOnClickListener {
            dataCallBack.onDataReceived(comment.nama,comment.id_comment)
        }

        var isExpanded = false // Status awal adalah teks dipotong
        if (comment.jumlah_balasan > 0 ){
            holder.lihat_balasan.visibility = View.VISIBLE
            holder.lihat_balasan.setOnClickListener {
                isExpanded = !isExpanded // Toggle status
                if (isExpanded) {
                    holder.rv_balas_comment.visibility = View.VISIBLE
                    holder.tv_lihat_balasan.text = "Sembunyinkan"
                    holder.icon_lihat_balasan.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                } else {
                    holder.rv_balas_comment.visibility = View.GONE
                    holder.tv_lihat_balasan.text = "Lihat Balasan"
                    holder.icon_lihat_balasan.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                }
                // Set adapter untuk RecyclerView balasan komentar
                val balasCommentAdapter = comment.balasCommentList?.let { it1 ->
                    BalasCommentAdapter(
                        it1
                    )
                }
                holder.rv_balas_comment.layoutManager = LinearLayoutManager(holder.rv_balas_comment.context)
                holder.rv_balas_comment.adapter = balasCommentAdapter
            }
        }else{
            holder.lihat_balasan.visibility = View.GONE
        }
    }

    override fun getItemCount() = listComment.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val foto_profile = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val tv_comment = view.findViewById<TextView>(R.id.tv_comment)
        val tgl_comment = view.findViewById<TextView>(R.id.tgl_comment)
        val balas = view.findViewById<TextView>(R.id.balas_comment)
        val lihat_balasan = view.findViewById<LinearLayout>(R.id.ll_lihat_balasan)
        val tv_lihat_balasan = view.findViewById<TextView>(R.id.lihat_balas_comment)
        val icon_lihat_balasan = view.findViewById<ImageView>(R.id.icon_lihat_balasan)
        val rv_balas_comment = view.findViewById<RecyclerView>(R.id.rv_balas_comment)

    }
}