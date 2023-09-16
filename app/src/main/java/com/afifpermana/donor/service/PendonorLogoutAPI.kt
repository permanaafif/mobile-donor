package com.afifpermana.donor.service

import com.afifpermana.donor.model.PendonorLogoutResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PendonorLogoutAPI {
    @GET("api/logout")
    fun logout(@Header("Authorization") auth_token : String): Call<PendonorLogoutResponse>
}