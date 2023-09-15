package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DaftarJadwalDonorResponse {
    @SerializedName("status")
    @Expose
    var status : Boolean? = false

    @SerializedName("message")
    @Expose
    var message : Boolean? = false
}