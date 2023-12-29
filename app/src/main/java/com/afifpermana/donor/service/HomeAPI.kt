package com.afifpermana.donor.service

import com.afifpermana.donor.model.HomeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeAPI {
    @GET("api/home")
    fun home(@Header("Authorization") auth_token : String): Call<HomeResponse>
}