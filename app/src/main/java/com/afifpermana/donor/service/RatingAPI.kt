package com.afifpermana.donor.service

import com.afifpermana.donor.model.RatingRequest
import com.afifpermana.donor.model.RatingResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RatingAPI {

    @POST("api/testimonial")
    fun testimonial(@Header("Authorization") auth_token : String,@Body req: RatingRequest): Call<RatingResponse>
}