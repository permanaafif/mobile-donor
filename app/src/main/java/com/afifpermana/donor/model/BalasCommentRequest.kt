package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BalasCommentRequest {

    @SerializedName("id_post")
    @Expose
    var id_post : Int? = null

    @SerializedName("text")
    @Expose
    var text : String? = null
}