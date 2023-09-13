package com.afifpermana.donor.service.login

import com.example.belajarapi.model.PendonorLoginRequest
import com.example.belajarapi.model.PendonorLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PendonorLoginAPI {
    @POST("api/pendonor/login")
    fun login(@Body req: PendonorLoginRequest): Call<PendonorLoginResponse>

}