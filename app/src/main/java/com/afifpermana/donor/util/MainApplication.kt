package com.afifpermana.donor.util

import android.app.Application
import com.afifpermana.donor.service.FCMService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainApplication : Application() {
    companion object {
        const val BASE_URL = "https://fcm.googleapis.com/"
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val fcmService: FCMService = retrofit.create(FCMService::class.java)
}