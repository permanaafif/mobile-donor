package com.afifpermana.donor.util

import android.app.Application
import com.afifpermana.donor.service.FCMService
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainApplication : Application() {
    companion object {
        const val BASE_URL = "https://fcm.googleapis.com/"
    }
    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Firebase
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val fcmService = retrofit.create(FCMService::class.java)
}
