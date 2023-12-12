package com.afifpermana.donor.adapter

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.ChatActivity
import com.afifpermana.donor.CommentsActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.model.Notifikasi
import com.afifpermana.donor.model.UserChat
import com.afifpermana.donor.service.CallBackNotif
import com.afifpermana.donor.util.CheckWaktu
import com.afifpermana.donor.util.ConnectivityChecker
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ListChatAdapter (
    private val listChat : List<UserChat>,
    private val context : Context,
): RecyclerView.Adapter<ListChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_list_user_chat, parent, false)
    )

    override fun onBindViewHolder(holder: ListChatAdapter.ViewHolder, position: Int) {
        val chat = listChat[position]
        if (chat.foto_profile != "null"){
            Picasso.get().load(chat.foto_profile).into(holder.image)
        }else{
            holder.image.setImageResource(R.drawable.baseline_person_24)
        }
        holder.nama.text = chat.nama
        if (chat.kamu == false){
            holder.pesan.text = chat.pesan
        }else{
            holder.pesan.text = "Kamu: ${chat.pesan}"
        }
        var waktu = ""
        var timeNow = CheckWaktu()
        timeNow.getTime()
        timeNow.setWaktu(timeNow.getWaktu())

        Log.e("testWaktu",timeNow.getTanggal())

        var time = CheckWaktu()
        time.setWaktu(chat.waktu)
        var banding = timeNow.bandingTanggal(timeNow.getTanggal(),time.getTanggal())
        Log.e("testWaktu",banding.toString())
//
        if (banding == true){
            waktu = time.getJam()
        }else{
            waktu = time.getTanggal()
        }

        holder.waktu.text = waktu

        holder.ll_chat.setOnClickListener{
            var i = Intent(context, ChatActivity::class.java)
            i.putExtra("id_receiver",chat.id.toString())
            i.putExtra("nama",chat.nama.toString())
            i.putExtra("path",chat.foto_profile.toString())
            context.startActivity(i)
        }

    }

    override fun getItemCount() = listChat.size

    class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        val image = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val pesan = view.findViewById<TextView>(R.id.tv_pesan)
        val waktu = view.findViewById<TextView>(R.id.waktu)
        val ll_chat = view.findViewById<LinearLayout>(R.id.ll_chat)

    }

}