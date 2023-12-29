package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddBalasCommentRequest {

    @SerializedName("id_comment")
    @Expose
    var id_comment : Int? = null

    @SerializedName("text")
    @Expose
    var text : String? = null
}