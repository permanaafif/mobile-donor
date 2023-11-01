package com.afifpermana.donor.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.CommentsActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Comments
import com.afifpermana.donor.model.Laporan
import com.afifpermana.donor.service.CallBackData
import com.airbnb.lottie.LottieAnimationView
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
        var path_foto_profile = "http://213.35.121.183/images/${comment.gambar}"
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
                    holder.cl_balas_comment.visibility = View.VISIBLE
                    holder.loadingLottie.visibility = View.VISIBLE
                    Handler().postDelayed({
                        holder.cl_balas_comment.visibility = View.GONE
                        holder.rv_balas_comment.visibility = View.VISIBLE
                        holder.tv_lihat_balasan.text = "Sembunyinkan"
                        holder.icon_lihat_balasan.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                    },2000)
                } else {
                    holder.rv_balas_comment.visibility = View.GONE
                    holder.cl_balas_comment.visibility = View.GONE
                    holder.tv_lihat_balasan.text = "Lihat Balasan"
                    holder.icon_lihat_balasan.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                }
                // Set adapter untuk RecyclerView balasan komentar
                val balasCommentAdapter = comment.balasCommentList?.let { it1 ->
                    BalasCommentAdapter(
                        it1,dataCallBack
                    )
                }
                holder.rv_balas_comment.layoutManager = LinearLayoutManager(holder.rv_balas_comment.context)
                holder.rv_balas_comment.adapter = balasCommentAdapter
            }
        }else{
            holder.lihat_balasan.visibility = View.GONE
        }

        holder.btn_report.setOnClickListener {
            showCostumeAlertDialog(holder.itemView.context,comment.id_comment)
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
        val btn_report = view.findViewById<ImageView>(R.id.btn_report)
        val cl_balas_comment = view.findViewById<ConstraintLayout>(R.id.cl_balas_comment)
        val loadingLottie = view.findViewById<LottieAnimationView>(R.id.loading)
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
        val type = "Komentar"
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
                val laporan = Laporan(null,id,null,textLength,type)
                dataCallBack.onAddLaporan(laporan)
                dialog.dismiss()
            }else{
                textHelper.text = "Tulis laporan ..."
                textHelper.visibility = View.VISIBLE
            }
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}