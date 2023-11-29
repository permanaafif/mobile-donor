package com.afifpermana.donor.service

import com.afifpermana.donor.model.AddPostResponse
import com.afifpermana.donor.model.AddPostTextRequest
import com.afifpermana.donor.model.PostRespone
import com.afifpermana.donor.model.PostResponse2
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostAPI {

    @GET("api/post")
    fun post(@Header("Authorization") auth_token : String,@Query("page") page: Int): Call<List<PostRespone>>

    @GET("api/post-all")
    fun postAll(@Header("Authorization") auth_token : String): Call<List<PostRespone>>

    @GET("api/post/me")
    fun postMe(@Header("Authorization") auth_token : String,@Query("page") page: Int): Call<List<PostRespone>>

    @GET("api/post/other-donor/{id}")
    fun postOtherDonor(@Header("Authorization") auth_token : String, @Path("id") id: Int,@Query("page") page: Int): Call<List<PostRespone>>

    @DELETE("api/post/delete/{id}")
    fun deletePost(@Header("Authorization") auth_token : String,@Path("id") id: Int): Call<PostResponse2>


    @Multipart
    @POST("api/post/add")
    fun addPostWithImage(
        @Header("Authorization") auth_token: String,
        @Part gambar: MultipartBody.Part,
        @Part("text") text: RequestBody
    ): Call<AddPostResponse>

    @FormUrlEncoded
    @POST("api/post/add")
    fun addPostWithoutImage(
        @Header("Authorization") auth_token: String,
        @Field("text") text: String
    ): Call<AddPostResponse>


}