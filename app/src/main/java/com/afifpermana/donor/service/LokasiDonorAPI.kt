package com.afifpermana.donor.service

import com.afifpermana.donor.model.LokasiDonorResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface LokasiDonorAPI {
    @GET("api/jadwal-donor-darah")
    fun lokasi(@Header("Authorization") auth_token : String): Call<List<LokasiDonorResponse>>
}