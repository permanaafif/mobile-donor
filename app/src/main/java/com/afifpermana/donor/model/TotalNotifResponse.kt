package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TotalNotifResponse {

    @SerializedName("total_notif")
    @Expose
    var total_notif : Int? = null
}