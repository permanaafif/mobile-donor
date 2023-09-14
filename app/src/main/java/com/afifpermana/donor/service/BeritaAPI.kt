package com.afifpermana.donor.service

import com.afifpermana.donor.model.BeritaResponse
import com.example.belajarapi.model.PendonorLoginRequest
import com.example.belajarapi.model.PendonorLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface BeritaAPI {
    @GET("api/berita")
    fun allBerita(): Call<List<BeritaResponse>>

//    @GET("api/berita/{id}")
//    fun Berita(@Path("id") id: Int): Call<BeritaResponse>
}