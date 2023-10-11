package com.afifpermana.donor.model

data class Jadwal (
    val id: Int,
    val tanggal : String,
    val jam_mulai : String,
    val jam_selesai : String,
    val lokasi : String,
    val alamat : String,
    val kontak : String,
    val latitude : Double,
    val langitude : Double,
    val status : Boolean
)