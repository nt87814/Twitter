package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import okhttp3.Headers;

/**
 * Activity for displaying tweet details
 *
 * This activity is used to display the tweet with more details and allows
 * the user to like, unlike, favorite the tweet, and click on the link in the body.
 *
 * */
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

    private static final String TAG = "TweetDetailsActivity";
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
                            Log.e(TAG, "onFailure!" + response, throwable);            //throwable is the exception
                            Toast.makeText(TweetDetailActivity.this, "onFailure for like", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
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
                            Toast.makeText(TweetDetailActivity.this, "onFailure for unlike", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
                            Toast.makeText(TweetDetailActivity.this, "onFailure for retweet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // transmitting tweet object back to TimeLineActivity.java
        Intent i = new Intent();
        i.putExtra("updatedTweet", Parcels.wrap(updatedTweet));
        i.putExtra("originalTweet", Parcels.wrap(tweet));
        setResult(RESULT_OK, i);
        finish();
    }
}