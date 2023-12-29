package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serial

class PostFavoriteResponse {

    @SerializedName("id")
    @Expose
    var id : Int? = null

    @SerializedName("id_pendonor")
    @Expose
    var id_pendonor : Int? = null

    @SerializedName("id_post")
    @Expose
    var id_post : Int? = null

    @SerializedName("created_at")
    @Expose
    var created_at : String? = null

    @SerializedName("updated_at")
    @Expose
    var updated_at : String? = null
}