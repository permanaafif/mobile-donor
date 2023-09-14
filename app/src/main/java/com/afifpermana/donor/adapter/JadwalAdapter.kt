package com.afifpermana.donor.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.ArtikelActivity
import com.afifpermana.donor.MapsActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.Jadwal

class JadwalAdapter(
    private val listJadwal : List<Jadwal>,
): RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.card_location, parent,false)
    )

    override fun onBindViewHolder(holder: JadwalAdapter.ViewHolder, position: Int) {
        val jadwal = listJadwal[position]
        holder.tanggal.text = jadwal.tanggal
        holder.jam.text = "${jadwal.jam_mulai} - ${jadwal.jam_selesai}"
        holder.lokasi.text = jadwal.lokasi

        holder.card_location.setOnClickListener {
            val context = it.context
            val i = Intent(context, MapsActivity::class.java)
            i.putExtra("tanggal",jadwal.tanggal)
            i.putExtra("jam","${jadwal.jam_mulai} - ${jadwal.jam_selesai}")
            i.putExtra("lokasi",jadwal.lokasi)
            i.putExtra("alamat",jadwal.alamat)
            i.putExtra("kontak",jadwal.kontak)
            i.putExtra("latitude",jadwal.latitude)
            i.putExtra("longitude",jadwal.langitude)
            context.startActivity(i)
        }
    }

    override fun getItemCount() = listJadwal.size

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val tanggal = view.findViewById<TextView>(R.id.tanggal_jadwal_donor)
        val jam = view.findViewById<TextView>(R.id.jam)
        val lokasi = view.findViewById<TextView>(R.id.lokasi)
        val card_location = view.findViewById<CardView>(R.id.card_location)
    }

}