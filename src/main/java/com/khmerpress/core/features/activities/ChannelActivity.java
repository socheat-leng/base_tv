package com.khmerpress.core.features.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.widget.Toolbar;
import androidx.media3.common.util.UnstableApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.khmerpress.core.base.BaseActivity;
import com.khmerpress.core.features.models.Station;
import com.khmerpress.core.network.Config;
import com.khmerpress.core.network.DataManager;
import com.khmerpress.core.utils.AppSharedPreferences;
import com.khmerpress.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import today.khmerpress.core.R;
import today.khmerpress.core.databinding.ActivityChannelBinding;

public class ChannelActivity extends BaseActivity {

    //Views
    private ActivityChannelBinding binding;

    //Objects
    private String TAG = ChannelActivity.this.getClass().getSimpleName();
    private Station tvs;

    private InterstitialAd interstitialAd;
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;

    private NativeBannerAd nativeBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = this;
        setupEdgeToEdge();
        tvs = (Station) getIntent().getSerializableExtra(Constants.STATION);
        initGUI();
        initEvent();
        if (DataManager.Companion.getInstance(this).getAdStatus() == "1") {
            initAd();
        }
    }

    private void initGUI() {
        binding.toolBars.txtMediaListTitle.setText(tvs.getName());
        binding.txtInfo.setText(tvs.getContent());
        binding.txtCountry.setText(Config.Companion.getName());
        binding.txtName.setText(tvs.getName());
        RequestOptions options = new RequestOptions();
        Glide.with(this).load(tvs.getImage()).placeholder(R.drawable.ic_holder).error(R.drawable.ic_holder).into(binding.imgLogo);
        if (DataManager.Companion.getInstance(mActivity).getReview() == 1) {
            if (true) {
                binding.layoutLive.setVisibility(View.VISIBLE);
            } else {
                binding.layoutLive.setVisibility(View.GONE);
            }
        } else {
            binding.layoutLive.setVisibility(View.VISIBLE);
        }
    }

    private void initEvent() {
        binding.toolBars.btnBack.setOnClickListener(onClickListener);
        binding.btnWatchLive.setOnClickListener(new View.OnClickListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onClick(View view) {
                String url = tvs.getUrl();
                if (tvs.getExternal().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(mActivity, ATVPlayerActivity.class);
                    intent.putExtra(Constants.STATION, tvs);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.enter, R.anim.hold);
                } else {
                    if (url.endsWith(".ts")) {
                        Intent intent = new Intent(mActivity, ATVPlayerActivity.class);
                        intent.putExtra(Constants.STATION, tvs);
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.enter, R.anim.hold);
                    } else {
                        Intent intent = new Intent(mActivity, TVPlayerActivity.class);
                        intent.putExtra(Constants.STATION, tvs);
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.enter, R.anim.hold);
                    }
                }
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == binding.toolBars.btnBack) {
                onBackPressed();
            }

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int adCount = AppSharedPreferences.getConstant(mActivity).getInt(Constants.AD_COUNT);
        int frequency = AppSharedPreferences.getConstant(mActivity).getInt(Constants.AD_FREQUENCY);
        if (null != interstitialAd && interstitialAd.isAdLoaded()) {
            if (adCount >= frequency) {
                AppSharedPreferences.getConstant(mActivity).setInt(Constants.AD_COUNT, 0);
                interstitialAd.show();
            }
        }
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.hold, R.anim.exit);
    }

    private void initAd() {
        loadFBNativeBanner();

        loadNative();

        int adCount = AppSharedPreferences.getConstant(mActivity).getInt(Constants.AD_COUNT);
        int frequency = AppSharedPreferences.getConstant(mActivity).getInt(Constants.AD_FREQUENCY);
        if (adCount >= frequency) {
            loadFB();
        }
    }

    private void loadFBNativeBanner() {
        String id = DataManager.Companion.getInstance(this).getNativeBanner();
        nativeBannerAd = new NativeBannerAd(this, id);
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e("TAG", "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e("TAG", "Native ad failed to load: " + adError.getErrorMessage());
                loadBanner();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d("TAG", "Native ad is loaded and ready to be displayed!");
                if (nativeBannerAd == null || !nativeBannerAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if (nativeBannerAd.isAdInvalidated()) {
                    return;
                }
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d("TAG", "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d("TAG", "Native ad impression logged!");
            }
        };
        nativeBannerAd.loadAd(
                nativeBannerAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_unit, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    private void loadBanner() {
        binding.layoutAdTop.removeAllViews();
        AdView adViewTop = new AdView(mActivity, DataManager.Companion.getInstance(mActivity).getBanner(), AdSize.BANNER_HEIGHT_50);
        binding.layoutAdTop.addView(adViewTop);
        adViewTop.loadAd(adViewTop.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d("TAG", adError.getErrorMessage().toString());

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        }).build());

    }

    private void loadFB() {
        interstitialAd = new InterstitialAd(this, DataManager.Companion.getInstance(mActivity).getInterstitial());
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        }).build());
    }

    private void loadNative() {
        String id = DataManager.Companion.getInstance(this).getNative();
        nativeAd = new NativeAd(this, id);
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                loadMRec();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                if (nativeAd == null || nativeAd != ad) {
                    loadMRec();
                    return;
                }
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);
        // Add the AdOptionsView
        View choiceView = findViewById(R.id.ad_choices_container);
        if (choiceView instanceof RelativeLayout) {
            RelativeLayout adChoicesContainer = (RelativeLayout) choiceView;
            AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);
        } else {
            LinearLayout adChoicesContainer = (LinearLayout) choiceView;
            AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);
        }
        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    private void loadMRec() {
        binding.layoutAdBottom.removeAllViews();
        AdView adViewTop = new AdView(mActivity, DataManager.Companion.getInstance(mActivity).getRectangle(), AdSize.RECTANGLE_HEIGHT_250);
        binding.layoutAdBottom.addView(adViewTop);
        adViewTop.loadAd(adViewTop.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        }).build());
    }

    @Nullable
    @Override
    protected View getRootView() {
        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Toolbar getAppBarLayout() {
        return binding.toolBars.toolBar;
    }
}
