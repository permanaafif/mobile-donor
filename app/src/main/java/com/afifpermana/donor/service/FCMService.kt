package com.afifpermana.donor.service

import com.afifpermana.donor.model.NotificationPayload
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMService {
    @Headers("Content-Type: application/json", "Authorization: key=AAAAbv6fc40:APA91bGibxNHSOQbsYMG9OiYDpDOnH-RqvH6sAZFUZOtUSJr1_iIN39Jm5dA8gM6Lr1fXgrB0svPaa0G8ShyLmxkH1p_BubbMzK4ZgaV7lPGH0MUhtXTZKwrmijJPbaCDyCy_jnh_unv")
    @POST("fcm/send")
    fun sendNotification(@Body payload: NotificationPayload): Call<Void>
}