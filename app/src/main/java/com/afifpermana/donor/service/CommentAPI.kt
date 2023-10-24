package com.afifpermana.donor.service

import com.afifpermana.donor.model.CommentResponse
import com.afifpermana.donor.model.PostRespone
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CommentAPI {

    @GET("api/post/{id}")
    fun findPost(@Header("Authorization") auth_token : String, @Path("id") id: Int): Call <PostRespone>

    @GET("api/comment/{id}")
    fun comment(@Header("Authorization") auth_token : String, @Path("id") id: Int): Call <List<CommentResponse>>
}