package com.afifpermana.donor.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.ArtikelActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Artikel
import com.afifpermana.donor.model.BeritaResponse
import com.squareup.picasso.Picasso

class ArtikelAdapter(
    private val listArtikel : List<Artikel>
//    private val listArtikel : List<BeritaResponse>
) : RecyclerView.Adapter<ArtikelAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_artikel, parent, false)
    )

    override fun getItemCount() = listArtikel.size

    override fun onBindViewHolder(holder: ArtikelAdapter.ViewHolder, position: Int) {
        val artikel = listArtikel[position]
//        holder.image.setImageResource(artikel.gambar)
        Picasso.get().load("http://10.0.2.2:8000/assets/img/${artikel.gambar}").into(holder.image)
        holder.judul.text = artikel.judul
        holder.deskripsi.text = artikel.deskripsi
        holder.tanggal.text = artikel.tanggal

        holder.card_artikel.setOnClickListener{
            val context = it.context
            val i = Intent(context,ArtikelActivity::class.java)
            i.putExtra("gambar",artikel.gambar)
            i.putExtra("judul",artikel.judul.toString())
            i.putExtra("deskripsi",artikel.deskripsi)
            i.putExtra("tanggal",artikel.tanggal)
            context.startActivity(i)
        }

    }

    class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.gambar_artikel)
        val judul = view.findViewById<TextView>(R.id.judul_artikel)
        val deskripsi = view.findViewById<TextView>(R.id.deskripsi_artikel)
        val tanggal = view.findViewById<TextView>(R.id.tanggal_artikel)
        val card_artikel = view.findViewById<CardView>(R.id.card_artikel)

    }
}