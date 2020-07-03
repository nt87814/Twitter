package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.Viewholder>{

    private Context context;
    private List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate a the layout
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        // Modifying the exisitng referance to tweets rather than making a new one
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimestamp;
        ImageView ivMedia1;
        ImageButton btnFavorite;
        ImageButton btnRetweet;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivMedia1 = itemView.findViewById(R.id.ivMedia1);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
//            tvTimestamp.setText(tweet.createdAt);
            tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            Glide.with(context).load(tweet.media1ImageUrl).into(ivMedia1);
            if (tweet.liked) {
                btnFavorite.setColorFilter(Color.RED);
            }

            else {
                btnFavorite.setColorFilter(Color.BLACK);
            }

            if (tweet.retweeted) {
                btnRetweet.setColorFilter(Color.GREEN);
            }

            else {
                btnRetweet.setColorFilter(Color.BLACK);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // ensure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                // Create an Intent to display MovieDetailsActivity
                Intent intent = new Intent(context, TweetDetailActivity.class);
                // Pass the movie as an extra serialized
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // Show the activity
//                context.startActivity(intent);
                ((Activity) context).startActivityForResult(intent, 40);
            }
        }

        /**
         * Method that converts a JsonDate to a relative timestamp for each tweet "8m", "7h"
         * getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
         */
        private String getRelativeTimeAgo(String rawJsonDate) {
            if (rawJsonDate  == null) {
                return "NULL!!!!!";
            }
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }
    }
}
