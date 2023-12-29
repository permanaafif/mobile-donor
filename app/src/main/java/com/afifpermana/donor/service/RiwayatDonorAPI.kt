package com.afifpermana.donor.service

import com.afifpermana.donor.model.RiwayatDonorResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RiwayatDonorAPI {

    @GET("api/riwayat-donor-darah")
    fun riwayatDonor(@Header("Authorization") auth_token : String): Call<RiwayatDonorResponse>
}