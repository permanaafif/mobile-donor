package com.afifpermana.donor.service

import com.afifpermana.donor.model.LokasiDonorResponse
import retrofit2.Call
import retrofit2.http.GET

interface LokasiDonorAPI {
    @GET("api/lokasi-donor")
    fun lokasi(): Call<List<LokasiDonorResponse>>
}