package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GantiPasswordRequest {

    @SerializedName("password_lama")
    @Expose
    var password_lama : String? = null

    @SerializedName("password_baru")
    @Expose
    var password_baru : String? = null
}