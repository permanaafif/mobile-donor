package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LokasiDonorResponse {
    @SerializedName("id")
    @Expose
    val id : Int? = null

    @SerializedName("lokasi")
    @Expose
    val lokasi : String? = null

    @SerializedName("alamat")
    @Expose
    val alamat : String? = null

    @SerializedName("tanggal_donor")
    @Expose
    val tanggal_donor : String? = null

    @SerializedName("jam_mulai")
    @Expose
    val jam_mulai : String? = null

    @SerializedName("jam_selesai")
    @Expose
    val jam_selesai : String? = null

    @SerializedName("kontak")
    @Expose
    val kontak : String? = null

    @SerializedName("latitude")
    @Expose
    val latitude : Double? = null

    @SerializedName("longitude")
    @Expose
    val longitude : Double? = null

    @SerializedName("status")
    @Expose
    val status : Boolean? = null
}