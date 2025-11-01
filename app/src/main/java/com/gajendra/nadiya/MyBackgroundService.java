package com.gajendra.nadiya;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class MyBackgroundService extends Service {

    private static final String CHANNEL_ID = "BackgroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize resources if needed
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("My Background Service")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
                .build();

        startForeground(1, notification);

        // Perform your background task here
        new Thread(() -> {
            while (true) {
                try {
                    // Example: Log a message every 5 seconds
                    Thread.sleep(5000);
                    System.out.println("Background service is running...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();

        return START_STICKY; // Ensures the service restarts if terminated
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup resources if needed
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Return null since we don't bind this service
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
