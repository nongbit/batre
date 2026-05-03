package com.nongbit

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.ServiceInfo
import android.os.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class BatteryService : Service() {

    companion object {
        private const val CHANNEL_ID = "BATTERY_CHAN"
        private const val NOTIF_ID = 1

        fun start(context: Context) {
            val intent = Intent(context, BatteryService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) updateAll(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ambil data terakhir secara instan saat service baru nyala
        val lastIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        lastIntent?.let { updateAll(it) } ?: runAsForeground(0, "Monitoring...")
        return START_STICKY
    }

    private fun updateAll(intent: Intent) {
        val data = BatteryUtils.parseIntent(intent)

        // 1. Update Widget
        val views = RemoteViews(packageName, R.layout.widget_layout).apply {
            setTextViewText(R.id.battery_level, "${data.level}%")
            setProgressBar(R.id.battery_progress, 100, data.level, false)
            setTextViewText(R.id.battery_status, "${data.statusText} | ${data.voltage}V")
            setOnClickPendingIntent(R.id.battery_level, getActivityIntent())
        }

        val widget = ComponentName(this, BatteryWidget::class.java)
        AppWidgetManager.getInstance(this).updateAppWidget(widget, views)

        // 2. Update Foreground Notification
        val content = "${data.statusText} | ${data.voltage}V"
        runAsForeground(data.level, content)
    }

    private fun runAsForeground(level: Int, content: String) {
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Level: $level%")
            .setContentText(content)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getActivityIntent())
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIF_ID, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, notif)
        }
    }

    private fun getActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        try { unregisterReceiver(receiver) } catch (e: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(CHANNEL_ID, "Battery Monitor", NotificationManager.IMPORTANCE_LOW)
            chan.setShowBadge(false)
            getSystemService(NotificationManager::class.java).createNotificationChannel(chan)
        }
    }
}