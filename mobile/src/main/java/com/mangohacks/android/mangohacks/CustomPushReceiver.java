package com.mangohacks.android.mangohacks;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by andrewsosa on 11/19/15.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Check for the setting
        SharedPreferences sp = context.getSharedPreferences(MangoHacks.PREFERENCES, Context.MODE_PRIVATE);
        if(sp.getBoolean(MangoHacks.NOTIFICATIONS, true)) {
            super.onReceive(context, intent);
        }

    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Notification notification = super.getNotification(context, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.color = context.getResources().getColor(R.color.mangohacks_green);
        }
        return notification;
    }
}
