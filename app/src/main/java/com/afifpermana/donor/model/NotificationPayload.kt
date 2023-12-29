package com.afifpermana.donor.model

data class NotificationPayload(
    val to: String,
    val notification: NotificationData,
    val data: Map<String, String> = emptyMap()
)