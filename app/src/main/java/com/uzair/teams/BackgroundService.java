package com.uzair.teams;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;

public class BackgroundService extends Service {
    private static final String CH = "teamsgo_bg";
    private PowerManager.WakeLock wl;
    private WifiManager.WifiLock wf;

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel(CH, "TeamsGo background", NotificationManager.IMPORTANCE_LOW));
        Intent open = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pi = open == null ? null : PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE);
        Notification n = new Notification.Builder(this, CH)
                .setContentTitle("TeamsGo is running")
                .setContentText("Calls and meetings stay alive in the background")
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
        startForeground(1, n);
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                if (wl == null) {
                    wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "teamsgo:bg");
                    wl.setReferenceCounted(false);
                }
                wl.acquire(4 * 60 * 60 * 1000L);
            }
            WifiManager w = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (w != null) {
                if (wf == null) {
                    wf = w.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "teamsgo:wifi");
                    wf.setReferenceCounted(false);
                }
                if (!wf.isHeld()) wf.acquire();
            }
        } catch (Exception e) { }
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        try {
            if (wl != null && wl.isHeld()) wl.release();
            if (wf != null && wf.isHeld()) wf.release();
        } catch (Exception e) { }
        super.onDestroy();
    }
}
