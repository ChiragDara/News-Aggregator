package com.example.newapplication.articlesdetails;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newapplication.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    TextView articleHeadline, articleDate, articleAuthors, articleDesc, articleCount;
    ImageView articleImage;
    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        articleHeadline = itemView.findViewById(R.id.articleHeadline);
        articleDate = itemView.findViewById(R.id.articleDate);
        articleAuthors = itemView.findViewById(R.id.articleAuthors);
        articleDesc = itemView.findViewById(R.id.articleDesc);
        articleCount = itemView.findViewById(R.id.articleCount);
        articleImage = itemView.findViewById(R.id.articleImage);
        this.articleDesc.setMovementMethod(new ScrollingMovementMethod());
    }
}
