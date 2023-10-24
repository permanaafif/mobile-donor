package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentResponse {

    @SerializedName("id_post")
    @Expose
    var id_post : Int? = null

    @SerializedName("id_comment")
    @Expose
    var id_comment : Int? = null

    @SerializedName("id_pendonor")
    @Expose
    var id_pendonor : Int? = null

    @SerializedName("text")
    @Expose
    var text : String? = null

    @SerializedName("created_at")
    @Expose
    var created_at : String? = null

    @SerializedName("updated_at")
    @Expose
    var update_at : String? = null

    @SerializedName("gambar")
    @Expose
    var gambar : String? = null

    @SerializedName("nama")
    @Expose
    var nama : String? = null
    }