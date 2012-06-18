package com.dnd.privacyapp;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.dnd.privacyapp.data.PrivacyAppSharedPrefs;
import com.dnd.privacyapp.receiver.AlarmReceiver;
import com.dnd.privacyapp.R;

public class ReferenceListPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                PrivacyAppSharedPrefs.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        if (PrivacyAppSharedPrefs.getBackgroundUpdateFlag(getApplicationContext())) {
            setRecurringAlarm(context);
        } else {
            cancelRecurringAlarm(context);
        }
    }

    private void cancelRecurringAlarm(Context context) {
        Intent downloader = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(recurringDownload);
    }

    private void setRecurringAlarm(Context context) {

        // we know mobiletuts updates at right around 1130 GMT.
        // let's grab new stuff at around 11:45 GMT, inexactly
        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        updateTime.set(Calendar.HOUR_OF_DAY, 11);
        updateTime.set(Calendar.MINUTE, 45);

        Intent downloader = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                recurringDownload);
    }

}
