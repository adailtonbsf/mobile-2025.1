package me.daltonbsf.unirun.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

class CaronaReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Lembrete de Carona"
        val message = intent.getStringExtra("message") ?: "Sua carona sairá em 1 hora!"
        NotificationUtils.showNotification(context, title, message)
    }
}