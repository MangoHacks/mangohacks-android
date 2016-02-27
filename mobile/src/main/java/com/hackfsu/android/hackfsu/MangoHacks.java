package com.hackfsu.android.hackfsu;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;


public class MangoHacks extends Application {

    public static final String PREFERENCES = "preferences";
    public static final String NOTIFICATIONS = "notifications";
    public static final String COUNTDOWN = "countdown";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(ScheduleFragment.ScheduleItem.class);
        ParseObject.registerSubclass(ScheduleFragment.ScheduleDivider.class);
        ParseObject.registerSubclass(UpdateFragment.UpdateItem.class);
        ParseObject.registerSubclass(SponsorsFragment.Sponsor.class);
        ParseObject.registerSubclass(MapsFragment.MapItem.class);
        ParseObject.registerSubclass(FeedFragment.CountdownItem.class);

        Parse.initialize(this, "GLMDltnGHl8uoy8M9tzTolYjCefsvlGoVLrRSXJV", "Y3DOrD8WJ4RSmwYq3uyz4J1EnpPOPQa3hGpSeYRA");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("updates");
    }
}
