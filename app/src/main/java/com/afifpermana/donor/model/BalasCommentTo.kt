package com.afifpermana.donor.model

data class BalasCommentTo(
    var id: Int,
    var id_comment: Int,
    var id_pendonor: Int,
    var nama: String,
    var gambar: String,
    var text: String,
    var created_at: String,
    var updated_at: String
)
