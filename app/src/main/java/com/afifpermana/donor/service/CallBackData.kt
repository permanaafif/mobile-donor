package com.afifpermana.donor.service

interface CallBackData {
    fun onDataReceived(nama: String, id:Int)
    fun onDataReceivedFavorite(id:Int)
    fun onDeleteFavorite(id:Int)

    fun onDeletePost(id:Int)
}