package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddPostTextRequest {

    @SerializedName("text")
    @Expose
    var text : String? = null
}