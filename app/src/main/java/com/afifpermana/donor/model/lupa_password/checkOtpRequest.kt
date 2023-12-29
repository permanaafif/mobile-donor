package com.afifpermana.donor.model.lupa_password

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class checkOtpRequest {

    @SerializedName("email")
    @Expose
    var email : String? = null

    @SerializedName("token")
    @Expose
    var token : String? = null

    @SerializedName("otp")
    @Expose
    var otp : String? = null

}