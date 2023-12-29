package com.afifpermana.donor.model.lupa_password

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class resetPasswordRequest {

    @SerializedName("email")
    @Expose
    var email : String? = null

    @SerializedName("token")
    @Expose
    var token : String? = null

    @SerializedName("password")
    @Expose
    var password : String? = null
}