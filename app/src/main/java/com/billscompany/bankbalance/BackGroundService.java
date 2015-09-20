package com.billscompany.bankbalance;

    import android.app.AlarmManager;
    import android.app.PendingIntent;
    import android.app.Service;
    import android.content.Intent;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteException;
    import android.net.Uri;
    import android.os.IBinder;
    import android.os.SystemClock;
    import android.telephony.SmsManager;
    import android.util.Log;
    import android.view.View;
    import android.widget.CheckBox;

    /**
     * Created by walde_000 on 9/6/2015.
     */
    public class BackGroundService extends Service {

        private AlarmManager alarms;
        private PendingIntent alarmIntent;
        private static String active;

        public static String getActive() {
            return active;
        }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarms = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        active = "active";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null)
        {
            alarms.cancel(alarmIntent);
            Intent intentOnAlarm = new Intent(LaunchService.ACTION_ALARM);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intentOnAlarm, 0);
            alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 18000L, 18000L, alarmIntent);
        }
        else if (intent.getBooleanExtra("checked", false)) {
            System.out.println("service is started!!!!!!!!!!!!!!!!");
            alarms.cancel(alarmIntent);
            Intent intentOnAlarm = new Intent(LaunchService.ACTION_ALARM);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intentOnAlarm, 0);
            alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 18000L, 18000L, alarmIntent);
        } else {
            alarms.cancel(alarmIntent);
            System.out.println("service is stopping!!!!!!!!!!!!!!!!");
            this.stopSelf(); //Note: onDestroy() will be called automatically
        }
        return START_STICKY;
    }
        @Override
        public void onDestroy() {
            super.onDestroy();
            active = null;
        }

    }
