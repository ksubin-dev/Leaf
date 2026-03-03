package com.subin.leafy.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.subin.leafy.MainActivity
import com.subin.leafy.R
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LeafyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userUseCases: UserUseCases

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_LOG", "메시지 수신 데이터: ${message.data}")

        val title = message.notification?.title ?: message.data["title"] ?: "Leafy"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        val type = message.data["type"] ?: ""
        val targetPostId = message.data["targetPostId"] ?: ""
        val senderId = message.data["senderId"] ?: ""

        if (title.isNotBlank() && body.isNotBlank()) {
            showNotification(title, body, type, targetPostId, senderId)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_LOG", "새로운 토큰: $token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                userUseCases.updateFcmToken(true)
            } catch (e: Exception) {
                Log.e("FCM_LOG", "토큰 업데이트 실패", e)
            }
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        type: String,
        targetPostId: String,
        senderId: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = "ACTION_FCM_NOTIFICATION_${System.currentTimeMillis()}"
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            putExtra("type", type)
            putExtra("targetPostId", targetPostId)
            putExtra("senderId", senderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "leafy_notification_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Leafy 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}