package com.afifpermana.donor.service

import com.afifpermana.donor.model.DaftarJadwalDonorRequest
import com.afifpermana.donor.model.DaftarJadwalDonorResponse
import com.afifpermana.donor.model.LokasiDonorResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JadwalUserAPI {
    @GET("api/jadwal-donor/{id}")
    fun jadwalDonor(@Path("id") id: Int): Call<List<LokasiDonorResponse>>

    @GET("api/jadwal-donor/{id}/{idl}")
    fun statusDaftar(@Path("id") id: Int, @Path("idl") idl:Int): Call<DaftarJadwalDonorResponse>

    @POST("api/jadwal-donor")
    fun daftar(@Body req: DaftarJadwalDonorRequest): Call<DaftarJadwalDonorResponse>
}