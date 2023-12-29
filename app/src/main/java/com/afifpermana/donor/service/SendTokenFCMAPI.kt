package com.afifpermana.donor.service

import com.afifpermana.donor.model.SendTokenFCMToServerResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SendTokenFCMAPI {

    @FormUrlEncoded
    @POST("api/token-fcm")
    fun senToken(
        @Header("Authorization") auth_token : String,
        @Field("token") token: String
    ):Call <SendTokenFCMToServerResponse>
}