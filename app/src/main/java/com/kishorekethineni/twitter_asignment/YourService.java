package com.kishorekethineni.twitter_asignment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YourService extends Service {
    public int counter=0;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Fetched "+new Background().getItemCount()+" tweets")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }
    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                String JsonData = null;
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://api.twitter.com/1.1/search/tweets.json?result_type=recent&q=NTRTheKingOfTwitter")
                        .method("GET", null)
                        .addHeader("Authorization", "OAuth oauth_consumer_key=\"6uInPt7U2UJ4LIV6HnkORnEND\",oauth_token=\"532187478-5jbyPoCKsbkma6EYvfvQLnVdJbxV0iocoGxRRsNP\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1592893346\",oauth_nonce=\"MLTOp6ISQGT\",oauth_version=\"1.0\",oauth_signature=\"h4xg4LHeUOSLQEDgu5BW%2BN%2BmjhQ%3D\"")
                        .addHeader("Cookie", "personalization_id=\"v1_naAzg3EcsFT/ngpcZXYgMQ==\"; guest_id=v1%3A159282940235730279; lang=en")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JsonData=response.body().string();
                    //   Log.i("Response",response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("Jsondatain thread",JsonData);
                Log.i("Count", "=========  "+ (counter++));
            }
        };
        timer.schedule(timerTask,1000*15); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}