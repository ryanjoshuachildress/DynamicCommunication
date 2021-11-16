package com.ryanjoshuachildress.dynamiccommunication.models

import com.google.firebase.Timestamp

class NotificationInfo (
    var notificationID: String,
    var notificationTime: Timestamp,
    var notificationTitle: String,
    var notificationBody: String,
    var notificationActive: Boolean,
    var userID: String,
    var notificationType: Int
)