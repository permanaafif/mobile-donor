package com.afifpermana.donor.model.cari_user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserRequest {
    @SerializedName("nama")
    @Expose
    var nama : String? = null
}