package com.gajendra.nadiya;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String title;
        String message;

        title = intent.getStringExtra("title");
        message = intent.getStringExtra("endTime");


        assert title != null;
        assert message != null;
            if (message.isEmpty() && title.isEmpty() && title.isBlank() && message.isBlank()) {
                title = "reminder";
                message = "12:00 AM";
        }

    Intent alarmIntent = new Intent(context, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,alarmIntent,PendingIntent.FLAG_IMMUTABLE);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                "TASK_REMINDER_CHANNEL",
                "Task Reminder",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel for task reminders");
        notificationManager.createNotificationChannel(channel);

        // Build and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_REMINDER_CHANNEL")
                .setSmallIcon(R.mipmap.heart) // Add your custom icon
                .setContentTitle(title + message)
                .setContentText(message + title)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1001, builder.build());
    }

}
