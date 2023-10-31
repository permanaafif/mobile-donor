package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LaporanRequest {
    @SerializedName("id_post")
    @Expose
    var id_post : Int? = null

    @SerializedName("id_comment")
    @Expose
    var id_comment : Int? = null

    @SerializedName("id_reply")
    @Expose
    var id_reply : Int? = null

    @SerializedName("text")
    @Expose
    var text : String? = null

    @SerializedName("type")
    @Expose
    var type : String? = null
}