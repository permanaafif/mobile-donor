package com.afifpermana.donor.model

data class PostFavorite(
    val id : Int,
    val id_pendonor : Int,
    val id_post : Int,
    val created_at : String,
    val updated_at : String
)
