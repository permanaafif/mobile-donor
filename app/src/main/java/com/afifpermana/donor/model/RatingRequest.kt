package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RatingRequest {
    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("star")
    @Expose
    var star: Int? = null
}