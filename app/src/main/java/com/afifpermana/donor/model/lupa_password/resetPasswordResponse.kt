package com.afifpermana.donor.model.lupa_password

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class resetPasswordResponse {

    @SerializedName("success")
    @Expose
    var success : Boolean? = null

    @SerializedName("message")
    @Expose
    var message : String? = null
}