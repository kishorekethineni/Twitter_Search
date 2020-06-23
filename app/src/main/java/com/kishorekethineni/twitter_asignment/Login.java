package com.kishorekethineni.twitter_asignment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class Login extends Activity {
    private static final String API_KEY = "6uInPt7U2UJ4LIV6HnkORnEND";
    private static final String API_SECRET = "TTNR38wfA0DmoZMo1lQ4qnrCkFWd7tJc2Z69k8y6ckaV60GorW";
    List<Tweet_Data> list=new ArrayList<>();
    RecyclerView recyclerView;
    private SimpleLocation location;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        recyclerView=findViewById(R.id.recyclerView);
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }else {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            open(latitude,longitude);}

    }
    public class GetTweets extends AsyncTask<String,String,String>
    {
        String JsonData;
        ProgressDialog pd=new ProgressDialog(Login.this);
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
                    Recycler_View_Adapter rv=new Recycler_View_Adapter(list,Login.this);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(rv);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Login.this));
                }catch (JSONException je)
                {
                    Toast.makeText(Login.this, ""+je.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("JSONException",je.getMessage());
                }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String URL=strings[0];
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(URL)
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
                                Log.i("lat lng", String.valueOf(lat+""+lng));
                                Toast.makeText(Login.this,"You Selected to fetch tweets from your region",Toast.LENGTH_LONG).show();
                                new GetTweets().execute("https://api.twitter.com/1.1/search/tweets.json?result_type=recent&geocode="+ lat+""+lng+"200km&q=NTRTheKingOfTwitter");
                            }
                        });

        alertDialogBuilder.setNegativeButton("Random",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Login.this,"You Selected to fetch tweets randomly",Toast.LENGTH_LONG).show();
                new GetTweets().execute("https://api.twitter.com/1.1/search/tweets.json?result_type=recent&q=NTRTheKingOfTwitter");
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
