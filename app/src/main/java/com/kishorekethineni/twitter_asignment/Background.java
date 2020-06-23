package com.kishorekethineni.twitter_asignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Background extends AppCompatActivity {
    Intent mServiceIntent;
    private YourService mYourService;
    List<Tweet_Data> list=new ArrayList<>();
    RecyclerView recyclerView;
    private SimpleLocation location;
    String URL="https://api.twitter.com/1.1/search/tweets.json?result_type=recent&geocode=17.78945680.78945610km&q=NTRTheKingOfTwitter";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        recyclerView=findViewById(R.id.recyclerView);
        location = new SimpleLocation(this);

        mYourService = new YourService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            new Background.GetTweets().execute();
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        super.onDestroy();
    }
    public class GetTweets extends AsyncTask<String,String,String>
    {
        String JsonData;
        ProgressDialog pd=new ProgressDialog(Background.this);

        @Override
        protected void onPreExecute() {
            pd.setTitle("Fetching Tweets.....");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            Log.i("s",s);
            if (!(s==null))
                try {
                    JSONObject js = new JSONObject(s);
                    JSONArray tweets = js.getJSONArray("statuses");
                    JSONObject tweet;
                    for (int i = 0; i < tweets.length(); i++) {
                        tweet = tweets.getJSONObject(i);
//                        System.out.println((i + 1) + ") " + tweet.getString("id") + " at " + tweet.getString("created_at"));
//                        System.out.println(tweets.getJSONObject(i).getString("text") + "\n");
                        Tweet_Data td=new Tweet_Data((i + 1),tweet.getString("id"),tweets.getJSONObject(i).getString("text") ,tweet.getString("created_at"));
                        list.add(td);
                    }
                    Log.i("listcount", String.valueOf(list.size()));
                    Recycler_View_Adapter rv=new Recycler_View_Adapter(list,Background.this);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(rv);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Background.this));
                }catch (JSONException je)
                {
                    Toast.makeText(Background.this, "No recent tweets with given location", Toast.LENGTH_SHORT).show();
                    Log.i("JSONException",je.getMessage());
                }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
//            String URL=strings[0];
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
            return JsonData;
        }
    }
    public void open(final double lat, final double lng){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Fetch Tweets");
        alertDialogBuilder.setPositiveButton("Current Location",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(Background.this,"You Selected to fetch tweets from your region",Toast.LENGTH_LONG).show();
                        new Background.GetTweets().execute("https://api.twitter.com/1.1/search/tweets.json?result_type=recent&geocode="+ lat+""+lng+"200km&q=NTRTheKingOfTwitter");
                    }
                });

        alertDialogBuilder.setNegativeButton("Random",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Background.this,"You Selected to fetch tweets randomly",Toast.LENGTH_LONG).show();
                new Background.GetTweets().execute("https://api.twitter.com/1.1/search/tweets.json?result_type=recent&q=NTRTheKingOfTwitter");
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public int getItemCount()
    {
      return list.size();
    }
    public void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Fetched "+list.size()+" tweets")
                        .setContentText("#HappyBirthdayNTR");

        Intent notificationIntent = new Intent(this, Background.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}