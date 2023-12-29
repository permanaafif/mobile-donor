package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Notifikasi (
    val id: Int,
    val id_post: Int,
    val id_comment: Int,
    val id_balas_comment: Int? = null,
    val status_read: Int,
    val id_pembalas: Int,
    val update : String,
    val pendonor: PendonorItem
){
    data class PendonorItem(
        val id: Int,
        val gambar: String? = null,
        val nama: String
    )
}