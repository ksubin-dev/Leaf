package com.subin.leafy.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.leafy.shared.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommunityUploadForegroundInfoProvider @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    fun create(progress: String): ForegroundInfo {
        val channelId = "post_upload_channel"
        val title = "Leafy 커뮤니티"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "게시글 업로드",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_leaf)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                CommunityUploadWorker.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(CommunityUploadWorker.NOTIFICATION_ID, notification)
        }
    }
}
