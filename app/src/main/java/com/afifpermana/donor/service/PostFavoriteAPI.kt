package com.afifpermana.donor.service

import com.afifpermana.donor.model.PostFavoriteResponse
import com.afifpermana.donor.model.PostFavoriteResponse2
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PostFavoriteAPI {

    @GET("api/post-favorite")
    fun postFavorite(@Header("Authorization") auth_token: String): Call <List<PostFavoriteResponse>>

    @GET("api/post-favorite/check/{id}")
    fun checkPostFavorite(@Header("Authorization") auth_token: String, @Path("id") id: Int): Call <PostFavoriteResponse2>

    @POST("api/post-favorite/add/{id}")
    fun addPostFavorite(@Header("Authorization") auth_token: String, @Path("id") id: Int): Call <PostFavoriteResponse2>

    @DELETE("api/post-favorite/delete/{id}")
    fun deletePostFavorite(@Header("Authorization") auth_token: String, @Path("id") id: Int): Call <PostFavoriteResponse2>
}