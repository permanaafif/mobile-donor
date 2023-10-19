package com.afifpermana.donor.service

import com.afifpermana.donor.model.HomeResponse
import com.afifpermana.donor.model.PostRespone
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PostAPI {

    @GET("api/post")
    fun post(@Header("Authorization") auth_token : String): Call<List<PostRespone>>
}