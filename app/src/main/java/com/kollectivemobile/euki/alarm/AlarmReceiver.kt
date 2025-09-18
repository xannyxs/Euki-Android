package com.kollectivemobile.euki.alarm

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.kollectivemobile.euki.App
import com.kollectivemobile.euki.R
import com.kollectivemobile.euki.ui.SplashActivity

class AlarmReceiver : BroadcastReceiver() {
  companion object {
    private const val CHANNEL_ID = "com.kollectivemobile.euki.channelId"
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                      PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    val notificationIntent = Intent(context, SplashActivity::class.java)
    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addParentStack(SplashActivity::class.java)
    stackBuilder.addNextIntent(notificationIntent)

    val flag =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

      val pendingIntent = stackBuilder.getPendingIntent(0, flag)
    val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val channel =
              NotificationChannel(
                      CHANNEL_ID,
                      "NotificationEuki",
                      NotificationManager.IMPORTANCE_DEFAULT
              )
      notificationManager.createNotificationChannel(channel)

      val notification =
        Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(
                        App.getContext()?.getString(R.string.local_notification_message)
                )
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentIntent(pendingIntent)
                .build()

      notificationManager.notify(0, notification)
  }
}
