package com.afifpermana.donor.service

import com.afifpermana.donor.model.AddBalasCommentRequest
import com.afifpermana.donor.model.AddBalasCommentResponse
import com.afifpermana.donor.model.BalasCommentRequest
import com.afifpermana.donor.model.BalasCommentResponse
import com.afifpermana.donor.model.BalasCommentTo
import com.afifpermana.donor.model.BalasCommentToResponse
import com.afifpermana.donor.model.CommentResponse
import com.afifpermana.donor.model.PostRespone
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentAPI {

    @GET("api/post/{id}")
    fun findPost(@Header("Authorization") auth_token : String, @Path("id") id: Int): Call <PostRespone>

    @GET("api/comment/{id}")
    fun comment(@Header("Authorization") auth_token : String, @Path("id") id: Int): Call <List<CommentResponse>>

    @POST("api/comment/add")
    fun addComment(@Header("Authorization") auth_token : String, @Body req: BalasCommentRequest ): Call <BalasCommentResponse>

    @GET("api/balas-comment/{id}")
    fun balasComment(@Header("Authorization") auth_token : String, @Path("id") id: Int): Call <List<BalasCommentToResponse>>

    @POST("api/balas-comment/add")
    fun addBalasComment(@Header("Authorization") auth_token : String, @Body req: AddBalasCommentRequest ): Call <AddBalasCommentResponse>
}