package com.afifpermana.donor.service

import com.afifpermana.donor.model.GantiPasswordRequest
import com.afifpermana.donor.model.GantiPasswordResponse
import com.afifpermana.donor.model.ProfileResponse
import com.afifpermana.donor.model.UpdateProfileEditDataResponse
import com.afifpermana.donor.model.UpdateProfileRequestData
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UpdateProfileAPI {

    @Multipart
    @POST("api/profile-edit-gambar")
    fun updateProfileGambar(
        @Header("Authorization") auth_token : String,
        @Part profile : MultipartBody.Part
    ):Call<ProfileResponse>

    @POST("api/profile-edit-data")
    fun updateProfileData(
        @Header("Authorization") auth_token : String,
        @Body req: UpdateProfileRequestData
    ):Call<UpdateProfileEditDataResponse>

    @POST("api/profile-edit-password")
    fun gantiPassword(
        @Header("Authorization") auth_token : String,
        @Body req:GantiPasswordRequest): Call<GantiPasswordResponse>
}