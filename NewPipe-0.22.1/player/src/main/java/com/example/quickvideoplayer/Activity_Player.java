package com.example.quickvideoplayer;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.aware.plugin.awarelibrary.MainAware;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Activity_Player extends AppCompatActivity {

    long videoId;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Double time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initializeViews();
        videoId = getIntent().getExtras().getLong("videoId");
    }

    private void initializeViews() {
        playerView = findViewById(R.id.playerView);
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Uri videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId);
        MediaSource mediaSource = buildMediaSource(videoUri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private void releasePlayer(){

        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
        time = Double.valueOf(System.currentTimeMillis());

        /**Defining sensors to track and initializing data collection*/
        final List<String> sensors = new ArrayList<>();

        MainAware AWARE = new MainAware();
        sensors.add(AWARE.BATTERY);
        sensors.add(AWARE.ACTIVITY);
        sensors.add(AWARE.LOCATION);
        sensors.add(AWARE.LIGHT);


        AWARE.startAware(getApplicationContext(), getPackageName(), 3600, sensors);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        if(Util.SDK_INT<24){
            releasePlayer();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {

        /**Stopping sensor data collection and initializing data processing*/
        MainAware AWARE = new MainAware();
        try {
            AWARE.getData(getApplicationContext(), time);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        if(Util.SDK_INT>=24){
            releasePlayer();
        }
        super.onStop();
    }
}
