package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RiwayatDonorResponse {

    @SerializedName("id_riwayat")
    @Expose
    var id : Int? = null

    @SerializedName("tanggal_donor")
    @Expose
    var tanggal_donor : String? = null

    @SerializedName("lokasi_donor")
    @Expose
    var lokasi_donor : String? = null

    @SerializedName("jumlah_donor")
    @Expose
    var jumlah_donor : Int? = null
}