package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotifikasiResponse {

    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("id_post")
    @Expose
    val id_post: Int? = null

    @SerializedName("id_comment")
    @Expose
    val id_comment: Int? = null

    @SerializedName("id_balas_comment")
    @Expose
    val id_balas_comment: Int? = null

    @SerializedName("status_read")
    @Expose
    val status_read: Int? = null

    @SerializedName("update")
    @Expose
    val update: String? = null

    @SerializedName("pendonor")
    @Expose
    val pendonor: PendonorItem? = null

    class PendonorItem{

        @SerializedName("id")
        @Expose
        val id: Int? = null

        @SerializedName("gambar")
        @Expose
        val gambar: String? = null

        @SerializedName("nama")
        @Expose
        val nama: String? = null
    }
}