package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateProfileEditDataResponse {
    @SerializedName("success")
    @Expose
    var succes : Boolean? = null

    @SerializedName("message")
    @Expose
    var message : String? = null
}