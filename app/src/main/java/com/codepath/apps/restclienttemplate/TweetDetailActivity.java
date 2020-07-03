package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;
    ImageView ivProfileImage;
    TextView tvScreenName;
    TextView tvBody;
    ImageView ivMedia1;
    ImageButton btnFavorite;
    ImageButton btnRetweet;
    Tweet updatedTweet;

    TwitterClient client;

    public static final String TAG = "TweetDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTweetDetailBinding binding = ActivityTweetDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfileImage = binding.ivProfileImage;
        ivMedia1 = binding.ivMedia1;
        btnFavorite = binding.btnFavorite;
        btnRetweet = binding.btnRetweet;

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        binding.tvScreenName.setText(tweet.user.screenName);
        binding.tvBody.setText(tweet.body);
        Glide.with(this).load(tweet.media1ImageUrl).into(ivMedia1);
        if (tweet.liked) {
            btnFavorite.setColorFilter(Color.RED);
        }

        if (tweet.retweeted) {
            btnRetweet.setColorFilter(Color.GREEN);
        }

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.liked == false) {
                    client.like(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btnFavorite.setColorFilter(Color.RED);
                            try {
                                updatedTweet = Tweet.fromJson(json.jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }

                client.unlike(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        btnFavorite.setColorFilter(Color.BLACK);
                        try {
                            updatedTweet = Tweet.fromJson(json.jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });
            }
        });

        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.retweeted == false) {
                    client.retweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btnRetweet.setColorFilter(Color.GREEN);
                            try {
                                updatedTweet = Tweet.fromJson(json.jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        // transmitting tweet object back to TimeLineActivity.java
        Intent i = new Intent();
        i.putExtra("updatedTweet", Parcels.wrap(updatedTweet));
        setResult(RESULT_OK, i);
        finish();
    }
}