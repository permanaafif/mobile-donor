package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("success")
    @Expose
    var succes : Boolean? = null

    @SerializedName("user")
    @Expose
    lateinit var user : Profile

    class Profile{
        @SerializedName("id")
        @Expose
        var id : Int? = null

        @SerializedName("gambar")
        @Expose
        var gambar : String? = null

        @SerializedName("nama")
        @Expose
        var nama : String? =null

        @SerializedName("email")
        @Expose
        var email : String? =null

        @SerializedName("kode_pendonor")
        @Expose
        var kode_pendonor : String? =null

        @SerializedName("id_golongan_darah")
        @Expose
        lateinit var id_golongan_darah : Goldar

        class Goldar{
            @SerializedName("id")
            @Expose
            var id : Int? =null

            @SerializedName("nama")
            @Expose
            var nama : String? =null
        }

        @SerializedName("berat_badan")
        @Expose
        var berat_badan : Int? =null

        @SerializedName("alamat_pendonor")
        @Expose
        var alamat_pendonor : String? =null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggal_lahir : String? =null

        @SerializedName("kontak_pendonor")
        @Expose
        var kontak_pendonor : String? =null

        @SerializedName("jenis_kelamin")
        @Expose
        var jenis_kelamin : String? =null
    }

}