package com.afifpermana.donor.service

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.INotificationSideChannel
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.afifpermana.donor.MainActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.model.HomeResponse
import com.afifpermana.donor.model.SendTokenFCMToServerResponse
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var sharedPref: SharedPrefLogin
    val TAG = "FirebaseMessagingService"

    override fun onCreate() {
        super.onCreate()
        sharedPref = SharedPrefLogin(this) // Inisialisasi objek sharedPref
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("tokenden", token.toString())
        Log.e("tokendensheref", sharedPref.getString("token").toString())
        sharedPref.setString("token_fcm",token)
        if(!sharedPref.getString("token").isNullOrEmpty()){
//            sendTokenToServer(token)
            Log.e("testing","send Token To Server")
        }
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("tokendensheref", sharedPref.getString("token").toString())
        Log.d(TAG, "Dikirim dari: ${remoteMessage.from}")

        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        Log.e("isi","$title dan $ $body")
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}