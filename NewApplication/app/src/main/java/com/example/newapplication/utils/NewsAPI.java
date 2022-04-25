package com.example.newapplication.utils;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newapplication.MainActivity;
import com.example.newapplication.articlesdetails.ArticleDetails;
import com.example.newapplication.news.News;
import com.example.newapplication.news.Sources;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NewsAPI {
    private static final String TAG = "";
    private static MainActivity mainActivity;
    //News URL
    private static final String NEWSSOURCE ="https://newsapi.org/v2/sources";
    private static final String APIKEY = "ab3bc785966c436ab14ff8642a25391d";

    private static final String SOURCEAPIURL = "https://newsapi.org/v2/top-headlines";
    private static HashMap<String, List<String>> menuDetailsWithCategory = new HashMap<>();

    private static RequestQueue queue, queue1;
    private static News news;
    private static List<ArticleDetails> articleDetails;

    private static ArrayList<String> menuItems;
    private static HashSet<String> menuCategorySet = new HashSet<>();
    private static List<Sources> sources;
    private static HashMap<String, String> colorHashMap = new HashMap<>();


    public static News getAPIData(MainActivity mainActivityIn){
        mainActivity = mainActivityIn;
        queue = Volley.newRequestQueue(mainActivity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, getURL(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(mainActivityIn, response.toString(), Toast.LENGTH_LONG).show();
                        news = parseJson(response);
                        mainActivityIn.updateData(news);

                        menuItems = new ArrayList<>();
                        menuCategorySet.add("all");
                        sources = news.getSources();
                        for (Sources source: sources){
                            menuCategorySet.add(source.getCategory());
                        }

                        mainActivityIn.updateMenuItem(menuCategorySet);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mainActivityIn, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return news;
    }

    private static News parseJson(JSONObject response) {
       try{
           List<Sources> sources = new ArrayList<>();
           // Status
           String status  = response.getString("status");

           if(response.getJSONArray("sources") != null){
               JSONArray sourcesArray = response.getJSONArray("sources");
               for (int i = 0; i < sourcesArray.length(); i++) {
                   JSONObject subSourceItem = sourcesArray.getJSONObject(i);
                   String id = subSourceItem.getString("id");
                   String name = subSourceItem.getString("name");
                   String desc = subSourceItem.getString("category");
                   sources.add(new Sources(id, name, desc));
               }
           }
           news = new News(status, sources);
       } catch (Exception e){
           e.printStackTrace();
       }
        return news;
    }

    private static String getURL() {
        Uri.Builder buildURL = Uri.parse(NEWSSOURCE).buildUpon();
        buildURL.appendQueryParameter("apiKey", APIKEY);
        return buildURL.build().toString();
    }

    public static List<ArticleDetails> getArticles(String source, MainActivity mainActivityIn){
        mainActivity = mainActivityIn;
        queue1 = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(SOURCEAPIURL).buildUpon();
        buildURL.appendQueryParameter("sources", source);
        buildURL.appendQueryParameter("apiKey", APIKEY);

        Log.d(TAG, "getArticles URL: "+ buildURL.build().toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, buildURL.build().toString(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(mainActivityIn, response.toString(), Toast.LENGTH_LONG).show();
                        articleDetails = parseArticleDetails(response);
                        mainActivityIn.updateViewPager(articleDetails);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mainActivityIn, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "");
                return headers;
            }
        };
        queue1.add(jsonObjectRequest);
        return articleDetails;
    }

    private static List<ArticleDetails> parseArticleDetails(JSONObject response) {
        List<ArticleDetails> result = new ArrayList<>();
        try{
            JSONArray articlesJsonArray = response.getJSONArray("articles");
            for(int i = 0; i < articlesJsonArray.length(); i++){
                ArticleDetails articleDetails = new ArticleDetails();
                JSONObject jsonItem = articlesJsonArray.getJSONObject(i);
                String title = jsonItem.getString("title").toString();
                articleDetails.setTitle(title);
                String author = jsonItem.getString("author").toString();
                articleDetails.setAuthor(author);
                String url = jsonItem.getString("url");
                articleDetails.setUrl(url);
                String description = jsonItem.getString("description");
                articleDetails.setDescription(description);
                String urlToImage = jsonItem.getString("urlToImage").toString();
                articleDetails.setUrlToImage(urlToImage);
                String publishedAt = jsonItem.getString("publishedAt");
                articleDetails.setPublishedAt(publishedAt);
                result.add(articleDetails);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static List<Sources> updateDrawerLayoutData(String title) {
        List<Sources> result = new ArrayList<>();
        for (int i =0 ; i < sources.size(); i++){
            if(sources.get(i).getCategory().equalsIgnoreCase(title)){
                result.add(sources.get(i));
            }
        }
        return result;
    }
}
