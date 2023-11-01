package com.afifpermana.donor.model

data class Post(
    val id : Int,
    val foto_profile : String,
    val nama : String,
    val upload : String,
    val text : String,
    val gambar : String,
    val jumlah_comment : Int,
    val status : Boolean? = null
)
