package com.afifpermana.donor.model

data class NotificationData (
    val title: String,
    val body: String,
    val additionalData: Map<String, String> = emptyMap()
)