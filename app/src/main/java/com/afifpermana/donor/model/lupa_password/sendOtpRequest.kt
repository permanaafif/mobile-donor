package com.afifpermana.donor.model.lupa_password

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class sendOtpRequest {

    @SerializedName("kode_pendonor")
    @Expose
    var kode_pendonor : String? = null
}