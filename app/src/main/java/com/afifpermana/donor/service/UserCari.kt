package com.afifpermana.donor.service

import com.afifpermana.donor.model.cari_user.UserRequest
import com.afifpermana.donor.model.cari_user.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserCari {
    @POST("api/profile-cari")
    fun cari(@Header("Authorization") auth_token : String, @Body req: UserRequest): Call<List<UserResponse>>
}