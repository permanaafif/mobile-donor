package com.afifpermana.donor.model

data class RiwayatDonor (
    val total_donor_darah : Int,
    val riwayat: List<Riwayat>

){
    data class Riwayat(
        val tanggal_donor : String,
        val lokasi : String,
        val jumlah_donor : Int
    )
}