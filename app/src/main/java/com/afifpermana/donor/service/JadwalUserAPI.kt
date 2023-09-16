package com.afifpermana.donor.service

import com.afifpermana.donor.model.DaftarJadwalDonorRequest
import com.afifpermana.donor.model.DaftarJadwalDonorResponse
import com.afifpermana.donor.model.LokasiDonorResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface JadwalUserAPI {
    @GET("api/jadwal-donor-pendonor/{id}")
    fun jadwalDonor(@Header("Authorization") auth_token : String,@Path("id") id: Int): Call<List<LokasiDonorResponse>>

    @GET("api/jadwal-donor-pendonor/{id}/{idl}")
    fun statusDaftar(@Header("Authorization") auth_token : String,@Path("id") id: Int, @Path("idl") idl:Int): Call<DaftarJadwalDonorResponse>

    @POST("api/jadwal-donor-pendonor")
    fun daftar(@Header("Authorization") auth_token : String,@Body req: DaftarJadwalDonorRequest): Call<DaftarJadwalDonorResponse>
}