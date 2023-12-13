package com.afifpermana.donor.model

data class UserTokenFCM (
    val id:String,
    val token_fcm:String
){
    // Konstruktor tanpa argumen
    constructor() : this("", "")
}