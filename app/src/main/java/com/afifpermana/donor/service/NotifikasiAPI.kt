package com.afifpermana.donor.service

import com.afifpermana.donor.model.NotifikasiResponse
import com.afifpermana.donor.model.TotalNotifResponse
import com.afifpermana.donor.model.UpdateStatusNotifikasiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface NotifikasiAPI {

    @GET("api/notifikasi")
    fun notif(@Header("Authorization") auth_token : String): Call<List<NotifikasiResponse>>

    @GET("api/notifikasi/{id}")
    fun updateStatusNotif(@Header("Authorization") auth_token : String, @Path("id") id:Int): Call<UpdateStatusNotifikasiResponse>

    @GET("api/total-notif")
    fun totalNotif(@Header("Authorization") auth_token : String): Call<TotalNotifResponse>
}