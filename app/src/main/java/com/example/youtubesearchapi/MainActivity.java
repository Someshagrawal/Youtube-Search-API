package com.example.youtubesearchapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String youtube_API_KEY = "AIzaSyC5b7DkZUifliBcjzZ7tdvQJnZlhiZz3N4";
    private RequestQueue requestQueue ;
    private String Base_url = "https://www.googleapis.com/youtube/v3/search?part=snippet&key="+youtube_API_KEY+"&type=video&maxResults=15&q=";
    private JsonObjectRequest jsonObjectRequest;
    private List<Integer> videoLayoutsId = new ArrayList();
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);
        linearLayout = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) mSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 3) {
                    searchQuery(s);
                }
                else{
                    linearLayout.removeAllViews();
                }
                return true;
            }
        });

        return true;
    }

    private void searchQuery(String str){
        try {
            String url = Base_url + str;
            jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            linearLayout.removeAllViews();
                            videoLayoutsId.clear();
                            try {
                                JSONArray items = response.getJSONArray("items");
                                for(int i=0; i<items.length(); i++){

                                    LinearLayout videoLayout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.video_suggestions, null);
                                    videoLayout.setId(1000+i);
                                    videoLayoutsId.add(1000+i);

                                    JSONObject videoData = items.getJSONObject(i);
                                    JSONObject videoSnippet = videoData.getJSONObject("snippet");

                                    JSONObject thumbnails = videoSnippet.getJSONObject("thumbnails");
                                    JSONObject defaultThumbnail = thumbnails.getJSONObject("default");
                                    String defaultThumbnailUrl = defaultThumbnail.getString("url");
                                    Glide.with(getApplicationContext()).load(defaultThumbnailUrl).into((ImageView) videoLayout.findViewById(R.id.video_image));

                                    String videoTitle = videoSnippet.getString("title");
                                    ((TextView) videoLayout.findViewById(R.id.video_title)).setText(videoTitle);

                                    String channelTitle = videoSnippet.getString("channelTitle");
                                    ((TextView) videoLayout.findViewById(R.id.channel_title)).setText(channelTitle);

                                    String publishDate = videoSnippet.getString("publishTime");
                                    ((TextView) videoLayout.findViewById(R.id.published_date_time)).setText(publishDate.split("T", 2)[0]);

                                    linearLayout.addView(videoLayout);
                                    Log.e("Added: ", Integer.toString(i));
                                }

                            }
                            catch(Exception e){
                                Log.e("Response Error: ", e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error: ", error.toString());
                        }
                    })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };
        } catch (Exception e) {
            Log.e("Error: ", e.toString());
        }
        requestQueue.add(jsonObjectRequest);
    }

}
