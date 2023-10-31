package com.afifpermana.donor.service

import com.afifpermana.donor.model.Laporan

interface CallBackData {
    fun onDataReceived(nama: String, id:Int)
    fun onDataReceivedFavorite(id:Int)
    fun onDeleteFavorite(id:Int)
    fun onDeletePost(id:Int)

    fun onAddLaporan(laporan: Laporan)
}