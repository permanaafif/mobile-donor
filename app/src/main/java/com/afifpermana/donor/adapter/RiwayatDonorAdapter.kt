package com.afifpermana.donor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afifpermana.donor.R
import com.afifpermana.donor.model.RiwayatDonor

class RiwayatDonorAdapter(
    private val listRiwayatDonor : List<RiwayatDonor>
): RecyclerView.Adapter<RiwayatDonorAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_riwayat, parent, false)
    )

    override fun onBindViewHolder(holder: RiwayatDonorAdapter.ViewHolder, position: Int) {
        val riwayat = listRiwayatDonor[position]
        holder.tanggal_donor.text = riwayat.tanggal_donor
        holder.lokasi_donor.text = riwayat.lokasi
        holder.jumlah_donor.text = "${riwayat.jumlah_donor} Kantong"
    }

    override fun getItemCount() = listRiwayatDonor.size

    class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val tanggal_donor = view.findViewById<TextView>(R.id.tanggal_donor)
        val lokasi_donor = view.findViewById<TextView>(R.id.lokasi_donor)
        val jumlah_donor = view.findViewById<TextView>(R.id.jumlah_donor)
    }

}