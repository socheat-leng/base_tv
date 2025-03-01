package com.khmerpress.core.features.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.khmerpress.core.base.BaseActivity;
import com.khmerpress.core.features.models.Station;
import com.khmerpress.core.utils.AppUtil;
import com.khmerpress.core.utils.Constants;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.ArrayList;

import today.khmerpress.core.R;
import today.khmerpress.core.databinding.ActivityAtvPlayerBinding;

public class ATVPlayerActivity extends BaseActivity implements IVLCVout.Callback, IVLCVout.OnNewVideoLayoutListener {

    //Views
    private ActivityAtvPlayerBinding binding;
    private ImageButton btnPlay;
    private ImageButton btnSound;

    private ImageButton btnClose;
    private FrameLayout mVideoSurfaceFrame = null;
    private SurfaceView mVideoSurface = null;
    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private boolean isMuted = true;
    private Station stations;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAtvPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        setupEdgeToEdge();
        stations = (Station) getIntent().getSerializableExtra(Constants.STATION);
        initGUI();
        initEvent();
        url = stations.getUrl();
        setUp();
    }

    private void initGUI() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        btnPlay = findViewById(R.id.img_play);
        btnSound = findViewById(R.id.img_sound);
        btnClose = findViewById(R.id.img_close);
        btnSound.setImageResource(R.drawable.ic_volume_off);
        mVideoSurfaceFrame = (FrameLayout) findViewById(R.id.video_surface_frame);
        mVideoSurface = (SurfaceView) findViewById(R.id.video_surface);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        vlcVout.attachViews();

        mMediaPlayer.getVLCVout().addCallback(this);
    }

    private void initEvent() {
        btnPlay.setOnClickListener(onClickListener);
        btnSound.setOnClickListener(onClickListener);
        btnClose.setOnClickListener(onClickListener);
        mVideoSurfaceFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (null != mMediaPlayer) {
                    mMediaPlayer.getVLCVout().setWindowSize(AppUtil.getWidth(mActivity), mVideoSurfaceFrame.getMeasuredHeight());
                }
            }
        });
    }

    private void setUp() {
        initializePlayer();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == btnSound) {
                onBackPressed();
            }
            if (view == btnSound) {
                if (isMuted) {
                    mMediaPlayer.setVolume(100);
                    isMuted = false;
                    btnSound.setImageResource(R.drawable.ic_volume_up);
                } else {
                    mMediaPlayer.setVolume(0);
                    isMuted = true;
                    btnSound.setImageResource(R.drawable.ic_volume_off);
                }
            }
            if (view == btnPlay) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_play);
                } else {
                    mMediaPlayer.play();
                    btnPlay.setImageResource(R.drawable.ic_pause);
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.hold, R.anim.exit);
    }

    private void initializePlayer() {
        Media media = new Media(mLibVLC, Uri.parse(url));
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event.type == 267) {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                } else if (event.type == MediaPlayer.Event.EncounteredError) {
                    initializePlayer();
                } else {
                    btnPlay.setImageResource(R.drawable.ic_play);
                }
            }
        });
        mMediaPlayer.setVolume(0);
    }

    @Override
    protected void onDestroy() {
        mMediaPlayer.release();
        mLibVLC.release();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.getVLCVout().detachViews();
        mMediaPlayer.getVLCVout().removeCallback(this);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
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
