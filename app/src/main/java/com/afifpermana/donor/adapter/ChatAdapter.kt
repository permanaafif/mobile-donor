package com.afifpermana.donor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Chat
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    private val context: Context,
    private var userId : String,
    private val chatList: ArrayList<Chat>,
//    private val img:String? = null
): RecyclerView.Adapter<ChatAdapter.viewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.viewHolder {
        if (viewType != MESSAGE_TYPE_LEFT){
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_left_chat, parent, false)
            return viewHolder(view)
        }else{
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_right_chat, parent, false)
            return viewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatAdapter.viewHolder, position: Int) {
        val chat = chatList[position]

        Log.e("chat",chat.senderId.toString())

//        if (img.toString() == "null"){
//            holder.imgUser.setImageResource(R.drawable.baseline_person_24)
//        }else{
//            Picasso.get().load(img).into(holder.imgUser)
//        }

        holder.message.text = chat.message
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class viewHolder(view : View):RecyclerView.ViewHolder(view) {
//        val imgUser = view.findViewById<CircleImageView>(R.id.foto)
        val message = view.findViewById<TextView>(R.id.message)
    }

    override fun getItemViewType(position: Int): Int {
        Log.e("chat2",chatList[position].senderId)
        if (chatList[position].senderId != userId){
            return MESSAGE_TYPE_RIGHT
        }else{
            return MESSAGE_TYPE_LEFT
        }
    }
}