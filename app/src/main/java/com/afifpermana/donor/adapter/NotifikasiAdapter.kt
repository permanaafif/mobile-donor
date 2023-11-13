package com.afifpermana.donor.adapter

import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.CommentsActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Notifikasi
import com.afifpermana.donor.service.CallBackNotif
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotifikasiAdapter(
    private val listNotif : List<Notifikasi>,
    private val dataCallBack : CallBackNotif
): RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_notifikasi, parent, false)
    )

    override fun onBindViewHolder(holder: NotifikasiAdapter.ViewHolder, position: Int) {
        val notif = listNotif[position]
        var path_foto_profile = "http://213.35.121.183/images/${notif.pendonor.gambar}"
        if (notif.pendonor.gambar != "null"){
            Picasso.get().load(path_foto_profile).into(holder.image)
        }
        if (notif.pendonor.gambar == "null" || notif.pendonor.gambar.isNullOrEmpty()){
            holder.image.setImageResource(R.drawable.baseline_person_24)
        }

        Log.e("notifikasi", notif.id_balas_comment.toString())
        if (notif.id_balas_comment == 0){
            val text = "${notif.pendonor.nama.uppercase()} membalas komentar anda"
            val spannable = SpannableStringBuilder(text)
            // Menambahkan teks tebal pada "nama"
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, notif.pendonor.nama.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            holder.notif.text = spannable
            holder.tanggal.text = notif.update
        }else{
            val text = "${notif.pendonor.nama.uppercase()} mengomentari postingan anda"
            val spannable = SpannableStringBuilder(text)
            // Menambahkan teks tebal pada "nama"
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, notif.pendonor.nama.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            holder.notif.text = spannable
            holder.tanggal.text = notif.update
        }

        if(notif.status_read == 0){
            holder.iconNotif.visibility = View.VISIBLE
        }else{
            holder.iconNotif.visibility = View.GONE
        }

        holder.ll_notif.setOnClickListener{
            holder.iconNotif.visibility = View.GONE
            if (notif.status_read == 0){
                Log.e("status_read", notif.status_read.toString())
                dataCallBack.updateStatusRead(notif.id)
            }
            val context = it.context
            val i = Intent(context, CommentsActivity::class.java)
            i.putExtra("id_post",notif.id_post)
            i.putExtra("id_comment",notif.id_comment)
            i.putExtra("id_balas_comment",notif.id_balas_comment ?: 0)
            context.startActivity(i)
        }
    }

    override fun getItemCount() = listNotif.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val image = view.findViewById<CircleImageView>(R.id.foto_profile)
        val notif = view.findViewById<TextView>(R.id.notifikasi)
        val tanggal = view.findViewById<TextView>(R.id.tanggal_upload)
        val iconNotif = view.findViewById<ImageView>(R.id.btn_notif)
        val ll_notif = view.findViewById<LinearLayout>(R.id.ll_notif)

    }
}