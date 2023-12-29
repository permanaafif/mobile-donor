package com.afifpermana.donor.util

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retro {
    fun getRetroClientInstance(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("http://138.2.74.142/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}