package com.example.gimmedamoney.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gimmedamoney.core.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class AppFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FCM", "new token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val d = message.data
        val type = d["type"] ?: return

        val (title, body, route, groupId) = when (type) {
            "NEW_MESSAGE" -> Quad(
                d["groupName"] ?: "New message",
                "${d["senderName"] ?: "Someone"}: ${d["text"] ?: ""}",
                "group_chat",
                d["groupId"]
            )
            "NEW_EXPENSE" -> Quad(
                d["groupName"] ?: "New expense",
                d["description"] ?: "An expense was added",
                "group_expenses",
                d["groupId"]
            )
            "ADDED_TO_GROUP" -> Quad(
                "Added to group",
                "You were added to ${d["groupName"] ?: "a group"}",
                "group_overview",
                d["groupId"]
            )
            else -> return
        }

        showNotification(title, body, route, groupId)
    }

    private fun showNotification(title: String, body: String, route: String, groupId: String?){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", route)
            if (groupId != null) putExtra("groupId", groupId)
        }

        val pending = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(this, NotifChannels.GENERAL)
            //.setSMallIcon()
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        NotificationManagerCompat.from(this).notify(Random.nextInt(), notif)
    }

    private data class Quad(val a: String, val b : String, val c: String, val d: String?)
}