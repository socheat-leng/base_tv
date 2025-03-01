package com.khmerpress.core.features.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.PlayerView;

import com.khmerpress.core.base.BaseActivity;
import com.khmerpress.core.features.models.Station;
import com.khmerpress.core.utils.Constants;

import today.khmerpress.core.R;
import today.khmerpress.core.databinding.ActivityTvPlayerBinding;

@UnstableApi
public class TVPlayerActivity extends BaseActivity {


    //Views
    private ActivityTvPlayerBinding binding;
    private PlayerView playerView;

    //Objects
    private Station stations;
    private ExoPlayer player;

    private ImageView fullscreenButton;
    private ImageView muteButton;

    private LinearLayout prev;
    private LinearLayout rew;
    private LinearLayout toggle;
    private LinearLayout fast;
    private LinearLayout next;
    private LinearLayout layoutProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTvPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        setupEdgeToEdge();
        stations = (Station) getIntent().getSerializableExtra(Constants.STATION);
        initGUI();
        initEvent();
        setUp();
        showLive(true);
    }

    private void initGUI() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        playerView = findViewById(R.id.player_view);
        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        muteButton = playerView.findViewById(R.id.exo_mute_icon);
        layoutProgress = playerView.findViewById(R.id.layout_progress);

        prev = findViewById(R.id.prev);
        rew = findViewById(R.id.rew);
        toggle = findViewById(R.id.toggle);
        fast = findViewById(R.id.fast);
        next = findViewById(R.id.next);
    }

    private void initEvent() {
        muteButton.setOnClickListener(view -> {
            if (player.getVolume() > 0) {
                player.setVolume(0);
                muteButton.setImageResource(R.drawable.ic_volume_off);
            } else {
                player.setVolume(1);
                muteButton.setImageResource(R.drawable.ic_volume_up);
            }
        });

        fullscreenButton.setOnClickListener(v -> onBackPressed());
    }

    public void mute() {
        player.setVolume(0);
        muteButton.setImageResource(R.drawable.ic_volume_off);
    }

    private void setUp() {
        initializePlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.hold, R.anim.exit);
    }

    public void showLive(boolean show) {
        if (show) {
            toggle.setVisibility(View.GONE);
            prev.setVisibility(View.INVISIBLE);
            rew.setVisibility(View.INVISIBLE);
            fast.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            layoutProgress.setVisibility(View.INVISIBLE);
        } else {
            toggle.setVisibility(View.GONE);
            layoutProgress.setVisibility(View.VISIBLE);
        }
    }

    private void initializePlayer() {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(mActivity, "JPTV"));
        Uri hlsUri = Uri.parse(stations.getUrl());
        HlsMediaSource hlsMediaSource =
                new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(hlsUri));
        player = new ExoPlayer.Builder(this).build();
        player.setPlayWhenReady(true);
        player.setMediaSource(hlsMediaSource);
        player.prepare();
        playerView.setPlayer(player);
        playerView.setUseController(true);
        player.setVolume(0f);
        player.addListener(new Player.Listener() {

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                initializePlayer();
            }
        });

    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mute();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Nullable
    @Override
    protected View getRootView() {
        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Toolbar getAppBarLayout() {
        return null;
    }
}
