package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BeritaResponse {
        @SerializedName("id_berita")
        @Expose
        var id_berita : Int? = null

        @SerializedName("gambar")
        @Expose
        var gambar : String? = null

        @SerializedName("judul")
        @Expose
        var judul : String? = null

        @SerializedName("deskripsi")
        @Expose
        var deskripsi : String? = null

        @SerializedName("updated_at")
        @Expose
        var update_at : String? = null
}