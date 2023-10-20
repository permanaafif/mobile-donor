package com.afifpermana.donor.service

import com.afifpermana.donor.model.AddPostResponse
import com.afifpermana.donor.model.AddPostTextRequest
import com.afifpermana.donor.model.PostRespone
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PostAPI {

    @GET("api/post")
    fun post(@Header("Authorization") auth_token : String): Call<List<PostRespone>>

    @Multipart
    @POST("api/post/add")
    fun addPost(
        @Header("Authorization") auth_token: String,
        @Part gambar: MultipartBody.Part,
        @Part("text") text: RequestBody
    ): Call<AddPostResponse>

}