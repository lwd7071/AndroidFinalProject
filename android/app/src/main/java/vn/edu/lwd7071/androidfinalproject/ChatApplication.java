package vn.edu.lwd7071.androidfinalproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.StrictMode;

import vn.edu.lwd7071.androidfinalproject.di.AppContainer;

public final class ChatApplication extends Application {
    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects().penaltyLog().build());
        }
        container = new AppContainer(this);
        getSystemService(NotificationManager.class).createNotificationChannel(new NotificationChannel(
                "messages", getString(R.string.notification_channel_messages),
                NotificationManager.IMPORTANCE_HIGH));
    }

    public AppContainer container() {
        return container;
    }
}
