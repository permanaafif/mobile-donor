package com.afifpermana.donor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.CommentsActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.model.Notifikasi
import com.afifpermana.donor.service.CallBackNotif
import com.afifpermana.donor.util.ConnectivityChecker
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotifikasiAdapter(
    private val listChat : List<Chat>,
    private val context : Context,
    private val dataCallBack : CallBackNotif
): RecyclerView.Adapter<com.afifpermana.donor.adapter.ListUserChatAdapter.NotifikasiAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_list_user_chat, parent, false)
    )

    override fun onBindViewHolder(holder: com.afifpermana.donor.adapter.ListUserChatAdapter.NotifikasiAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount() = listChat.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val image = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val pesan = view.findViewById<TextView>(R.id.tv_pesan)
        val waktu = view.findViewById<TextView>(R.id.waktu)
        val ll_notif = view.findViewById<LinearLayout>(R.id.ll_chat)

    }
}