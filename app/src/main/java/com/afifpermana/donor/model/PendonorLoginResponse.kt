package com.example.belajarapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PendonorLoginResponse {

    @SerializedName("success")
    @Expose
    var success : Boolean? =null

    @SerializedName("message")
    @Expose
    var message : String? =null

    @SerializedName("token")
    @Expose
    var token : String? =null
}