package com.example.belajarapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PendonorLoginResponse {

    @SerializedName("success")
    @Expose
    var success : String? =null

    @SerializedName("message")
    @Expose
    var message : String? =null

    @SerializedName("user")
    @Expose
    lateinit var user : User

    @SerializedName("golongan_darah")
    @Expose
    lateinit var golongan_darah : Goldar

    @SerializedName("token")
    @Expose
    var token : String? =null

    class User{
        @SerializedName("id")
        @Expose
        var id : Int? =null

        @SerializedName("nama")
        @Expose
        var nama : String? =null

        @SerializedName("kode_pendonor")
        @Expose
        var kode_pendonor : String? =null

        @SerializedName("berat_badan")
        @Expose
        var berat_badan : Int? =null
    }

    class Goldar{
        @SerializedName("id")
        @Expose
        var id : Int? =null

        @SerializedName("name")
        @Expose
        var nama : String? =null
    }
}