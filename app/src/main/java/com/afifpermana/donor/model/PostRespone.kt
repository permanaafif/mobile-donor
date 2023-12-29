package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostRespone {

    @SerializedName("id")
    @Expose
    val id : Int? = 0

    @SerializedName("id_pendonor")
    @Expose
    val id_pendonor : Int? = 0

    @SerializedName("gambar_profile")
    @Expose
    val gambar_profile : String? = null

    @SerializedName("nama")
    @Expose
    val nama : String? = null

    @SerializedName("text")
    @Expose
    val text : String? = null

    @SerializedName("gambar")
    @Expose
    val gambar : String? = null

    @SerializedName("jumlah_comment")
    @Expose
    val jumlah_comment : Int? = 0

    @SerializedName("updated_at")
    @Expose
    val updated_at : String? = null
}