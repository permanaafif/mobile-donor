package com.afifpermana.donor.model.lupa_password

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class sendOtpResponse {

    @SerializedName("success")
    @Expose
    var success : Boolean? = null

    @SerializedName("message")
    @Expose
    var message : String? = null

    @SerializedName("email")
    @Expose
    var email : String? = null

    @SerializedName("token")
    @Expose
    var token : String? = null
}