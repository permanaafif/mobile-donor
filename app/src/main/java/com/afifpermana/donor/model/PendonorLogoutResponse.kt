package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PendonorLogoutResponse {

    @SerializedName("success")
    @Expose
    var success : Boolean? =null

    @SerializedName("message")
    @Expose
    var message : String? =null
}