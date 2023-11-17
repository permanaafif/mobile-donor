package com.afifpermana.donor.service

import com.afifpermana.donor.model.ProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProfileAPI {
    @GET("api/profile")
    fun profile(@Header("Authorization") auth_token : String): Call<ProfileResponse>

    @GET("api/profile/{id}")
    fun profileOtherDonor(@Header("Authorization") auth_token : String, @Path("id") id:Int): Call<ProfileResponse>
}