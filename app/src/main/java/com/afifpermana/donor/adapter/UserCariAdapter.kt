package com.afifpermana.donor.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.MainActivity
import com.afifpermana.donor.OtherDonorProfileActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.cari_user.User
import com.afifpermana.donor.util.SharedPrefLogin
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserCariAdapter(
    private var listUser: List<User>,
    private var sharedPref: SharedPrefLogin
): RecyclerView.Adapter<UserCariAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_list_user, parent, false)
        )

    override fun onBindViewHolder(holder: UserCariAdapter.ViewHolder, position: Int) {
        val user = listUser[position]
        var path_foto_profile = "http://138.2.74.142/images/${user.gambar}"
        if (user!!.gambar.toString() != "null"){
            Picasso.get().load(path_foto_profile).into(holder.foto)
        }else{
            holder.foto.setImageResource(R.drawable.baseline_person_24)
        }
        holder.nama.text = user.nama
        holder.kode_pendonor.text = user.kode_pendonor

        holder.ll_user.setOnClickListener {
            if (sharedPref.getInt("id") == user.id){
                val context = it.context
                val i = Intent(context, MainActivity::class.java)
                i.putExtra("my_profile","profile")
                context.startActivity(i)
            }else{
                val context = it.context
                val i = Intent(context, OtherDonorProfileActivity::class.java)
                i.putExtra("id_pendonor",user.id)
                context.startActivity(i)
            }
        }
    }

    override fun getItemCount() = listUser.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val foto = view.findViewById<CircleImageView>(R.id.foto_profile)
        val nama = view.findViewById<TextView>(R.id.nama)
        val kode_pendonor = view.findViewById<TextView>(R.id.tv_kodedonor)
        val ll_user = view.findViewById<LinearLayout>(R.id.ll_user)

    }
}