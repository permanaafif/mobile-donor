package com.example.belajarapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PendonorLoginRequest {
    @SerializedName("kode_pendonor")
    @Expose
    var kode_pendonor : String? =null

    @SerializedName("password")
    @Expose
    var password : String? =null
}