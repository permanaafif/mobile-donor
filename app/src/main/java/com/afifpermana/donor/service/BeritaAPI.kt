package com.afifpermana.donor.service

import com.afifpermana.donor.model.BeritaResponse
import com.example.belajarapi.model.PendonorLoginRequest
import com.example.belajarapi.model.PendonorLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface BeritaAPI {
    @GET("api/berita")
    fun allBerita(@Header("Authorization") auth_token : String, @Query("page") page: Int): Call<List<BeritaResponse>>

//    @GET("api/berita/{id}")
//    fun Berita(@Path("id") id: Int): Call<BeritaResponse>
}