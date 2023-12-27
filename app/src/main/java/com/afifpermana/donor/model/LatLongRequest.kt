package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LatLongRequest {
    @SerializedName("lat")
    @Expose
    var lat : Double? = 0.0

    @SerializedName("long")
    @Expose
    var long : Double? = 0.0
}