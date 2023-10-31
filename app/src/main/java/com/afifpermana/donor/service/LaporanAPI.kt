package com.afifpermana.donor.service

import com.afifpermana.donor.model.LaporanRequest
import com.afifpermana.donor.model.LaporanResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LaporanAPI {

    @POST("api/laporan")
    fun addLaporan(@Header("Authorization") auth_token : String,@Body req: LaporanRequest):Call <LaporanResponse>
}