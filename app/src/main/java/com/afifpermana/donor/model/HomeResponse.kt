package com.afifpermana.donor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeResponse {

    @SerializedName("success")
    @Expose
    var success : Boolean? =null

    @SerializedName("user")
    @Expose
    lateinit var user : User

    class User{

        @SerializedName("id")
        @Expose
        var id : Int? =null
        
        @SerializedName("gambar")
        @Expose
        var gambar : String? =null

        @SerializedName("nama")
        @Expose
        var nama : String? =null

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

        @SerializedName("jadwal_terdekat")
        @Expose
        var jadwal_terdekat : JadwalTerdekat? = null

        class JadwalTerdekat{
            @SerializedName("id")
            @Expose
            val id : Int? = null

            @SerializedName("lokasi")
            @Expose
            val lokasi : String? = null

            @SerializedName("alamat")
            @Expose
            val alamat : String? = null

            @SerializedName("tanggal_donor")
            @Expose
            val tanggal_donor : String? = null

            @SerializedName("jam_mulai")
            @Expose
            val jam_mulai : String? = null

            @SerializedName("jam_selesai")
            @Expose
            val jam_selesai : String? = null

            @SerializedName("kontak")
            @Expose
            val kontak : String? = null

            @SerializedName("jumlah_pendonor")
            @Expose
            val jumlah_pendonor : Int? = null

            @SerializedName("latitude")
            @Expose
            val latitude : Double? = null

            @SerializedName("longitude")
            @Expose
            val longitude : Double? = null
        }
    }
}