package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DaftarJadwalDonorRequest {
    @SerializedName("id_pendonor")
    @Expose
    var id_pendonor : Int? = null

    @SerializedName("id_jadwal_pendonor")
    @Expose
    var id_jadwal_pendonor : Int? = null
}