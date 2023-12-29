package com.afifpermana.donor.service

import com.afifpermana.donor.model.LatLongRequest
import com.afifpermana.donor.model.LokasiDonorResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LokasiDonorAPI {
    @POST("api/jadwal-donor-darah")
    fun lokasi(@Header("Authorization") auth_token : String,@Body req: LatLongRequest): Call<List<LokasiDonorResponse>>
}