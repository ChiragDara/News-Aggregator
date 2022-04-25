package com.example.newapplication.articlesdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapplication.MainActivity;
import com.example.newapplication.R;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private final MainActivity mainActivity;
    private final List<ArticleDetails> articleDetailsList;

    public ArticlesAdapter(MainActivity mainActivity, List<ArticleDetails> articleDetailsList) {
        this.mainActivity = mainActivity;
        this.articleDetailsList = articleDetailsList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_article_layout, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleDetails articleDetails = articleDetailsList.get(position);
        if (articleDetails.getTitle() != null){
            holder.articleHeadline.setText(articleDetails.getTitle());
            holder.articleHeadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(articleDetails.getUrl()));
                    mainActivity.startActivity(intent);
                }
            });
        }

        // Article Date
        if(articleDetails.getPublishedAt() != null){
            String articleDate = getArticeDate(articleDetails.getPublishedAt());
            holder.articleDate.setText(articleDate);
        }

        // Article Author
        if(articleDetails.getAuthor().isEmpty() || articleDetails.getAuthor().equals("null")) {
            holder.articleAuthors.setVisibility(View.GONE);
        } else {
            holder.articleAuthors.setText(articleDetails.getAuthor());
        }

        // URL Image
        if (articleDetails.getUrl() != null){
            Picasso picasso = Picasso.with(mainActivity);
            picasso.setLoggingEnabled(true);
            picasso.load(articleDetails.getUrlToImage())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.loading)
                    .into(holder.articleImage);

            holder.articleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(articleDetails.getUrl()));
                    mainActivity.startActivity(intent);
                }
            });

        } else {
            holder.articleImage.setImageResource(R.drawable.noimage);
        }

        // Article Description
        if(articleDetails.getDescription().isEmpty() || articleDetails.getDescription().equals("null")){
            holder.articleDesc.setVisibility(View.GONE);
        } else {
            holder.articleDesc.setText(articleDetails.getDescription());
            holder.articleDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(articleDetails.getUrl()));
                    mainActivity.startActivity(intent);
                }
            });
        }
        // Article Count
        holder.articleCount.setText(String.format("%d of %d", (position + 1), articleDetailsList.size()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getArticeDate(String publishedAt) {
        String articleDate = "";
        try{
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_INSTANT;
            TemporalAccessor accessor = timeFormatter.parse(publishedAt);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
            LocalDateTime localDateTime =
                    LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
            articleDate = localDateTime.format(dateTimeFormatter);
        } catch (Exception e){
            e.printStackTrace();
        }


        if(articleDate.isEmpty()){
            try{
                DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                TemporalAccessor accessor = timeFormatter.parse(publishedAt);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
                LocalDateTime localDateTime =
                        LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
                articleDate = localDateTime.format(dateTimeFormatter);
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        return articleDate;
    }

    @Override
    public int getItemCount() {
        return articleDetailsList.size();
    }

}
