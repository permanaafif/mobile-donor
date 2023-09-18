package com.afifpermana.donor.service

import com.afifpermana.donor.model.ProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileAPI {
    @GET("api/profile")
    fun profile(@Header("Authorization") auth_token : String): Call<ProfileResponse>
}