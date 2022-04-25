package com.example.newapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapplication.articlesdetails.ArticleDetails;
import com.example.newapplication.articlesdetails.ArticlesAdapter;
import com.example.newapplication.news.News;
import com.example.newapplication.news.Sources;
import com.example.newapplication.utils.Helper;
import com.example.newapplication.utils.NewsAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    News news;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    List<Sources> sourcesList;
    List<Sources> tempList;
    private ArrayAdapter<Sources> arrayAdapter;
    private ViewPager2 viewPager;
    TextView textView;
    private Menu menu;
    private static List<ArticleDetails> articleDetails = new ArrayList<>();
    ArticlesAdapter articlesAdapter;
    HashMap<String, String> color ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting Application Name
        setTitle("News Gateway");
        // Finding Components
        findingComponents();

        // Check Network Connection and Internet Permissions
        boolean isNetworkConnected = checkNetworkConnection();
        // check network is connected
        if(isNetworkConnected){
            getNewsData();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> selectItem(position)
        );

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.string.drawer_open,R.string.drawer_close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void getNewsData() {
        news = NewsAPI.getAPIData(this);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Important!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        if(item.getTitle().equals("all")){
            updateDrawerItems(sourcesList, item.getTitle().toString());
        }
        else if(item.getTitle() != null){
           List<Sources> updateDrawerItems =  NewsAPI.updateDrawerLayoutData(item.getTitle().toString());
           updateDrawerItems(updateDrawerItems, item.getTitle().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDrawerItems(List<Sources> updateDrawerItems, String title) {
        tempList = updateDrawerItems;
        arrayAdapter = new ArrayAdapter<Sources>(this, android.R.layout.simple_list_item_1, updateDrawerItems){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Sources newsSource = updateDrawerItems.get(position);
                View view = super.getView(position, convertView, parent);
                textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(newsSource.getName());
                if(title.equals("all")){
                    setTitle("News Gateway ("+updateDrawerItems.size()+")");
                    textView.setTextColor(Color.parseColor(color.get(newsSource.getCategory())));
                } else {
                    setTitle(updateDrawerItems.get(0).getCategory()+" ("+updateDrawerItems.size()+")");
                }
                textView.setTextColor(Color.parseColor(color.get(newsSource.getCategory())));
                return view;
            }
        };
        mDrawerList.setAdapter(arrayAdapter);
    }


    private void selectItem(int position) {
        //Log.d(TAG, "selectItem: " + sourcesList.get(position).getName());
        setTitle(tempList.get(position).getName());
        articleDetails = NewsAPI.getArticles(tempList.get(position).getId(), this);
        viewPager.setCurrentItem(position);

        mDrawerLayout.setBackgroundResource(0);
        viewPager.setBackground(null);
        mDrawerLayout.closeDrawer(mDrawerList);

    }


    private boolean checkNetworkConnection() {
        if(Helper.isNetworkConnected(this)){
            return true;
        }
        return false;
    }

    private void findingComponents() {
        // View Pager
        viewPager = findViewById(R.id.viewPager);
        ArticlesAdapter articlesAdapter = new ArticlesAdapter(this, articleDetails);
        // Drawer Layout
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

    }

    public void updateData(News news) {
        this.news = news;
        sourcesList = news.getSources();
        tempList = sourcesList;

        arrayAdapter = new ArrayAdapter<Sources>(this, android.R.layout.simple_list_item_1, this.sourcesList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Sources newsSource = sourcesList.get(position);
                View view = super.getView(position, convertView, parent);
                textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(newsSource.getName());
                setTitle("News Gateway ("+sourcesList.size()+")");
                textView.setTextColor(Color.parseColor(color.get(newsSource.getCategory())));
                return view;
            }
        };
        mDrawerList.setAdapter(arrayAdapter);
    }

    public void updateMenuItem(HashSet<String> menuCategorySet) {
        // Updating Menu items
        color = Helper.textColor(menuCategorySet);
        for (String menu: menuCategorySet) {
            if(!menu.equals("all")){
                SpannableString s = new SpannableString(menu);
                s.setSpan(new ForegroundColorSpan(Color.parseColor(color.get(menu))), 0, s.length(), 0);
                this.menu.add(s);
            } else {
                this.menu.add(menu);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void updateViewPager(List<ArticleDetails> articleDetails) {
        articlesAdapter = new ArticlesAdapter(this, articleDetails);
        viewPager.setAdapter(articlesAdapter);
        Log.d(TAG, "selectItem: " +articleDetails);

    }
}
