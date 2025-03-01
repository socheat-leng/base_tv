package com.khmerpress.core.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import today.khmerpress.core.R;


/**
 * Created by Socheat on 9/29/2017.
 */

public class InfoView extends RelativeLayout {

    //Views
    private LinearLayout mLayout;
    private ProgressBar mProgressBar;
    private TextView txtInfo;

    //Objects
    private Context mContext;

    public InfoView(Context context) {
        super(context);
        init();
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_info_view, this);
        mLayout = findViewById(R.id.layoutInfo);
        mProgressBar = findViewById(R.id.progress_bar);
        txtInfo = findViewById(R.id.txt_info);
    }

    public void showLoading() {
        mLayout.setVisibility(VISIBLE);
        mLayout.setClickable(true);
        mLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.loadingTransparent));
        mProgressBar.setVisibility(VISIBLE);
        txtInfo.setVisibility(GONE);
    }

    public void showLoading(boolean show) {
        if (show) {
            mLayout.setVisibility(VISIBLE);
            mLayout.setClickable(true);
            mLayout.setBackgroundColor(Color.TRANSPARENT);
            mProgressBar.setVisibility(VISIBLE);
            txtInfo.setVisibility(GONE);
        } else {
            hide();
        }
    }

    public void showWhiteLoading() {
        mLayout.setVisibility(VISIBLE);
        mLayout.setClickable(true);
        mProgressBar.setVisibility(GONE);
        txtInfo.setVisibility(GONE);
    }

    public void showInfo(String message) {
        mLayout.setVisibility(VISIBLE);
        mLayout.setClickable(false);
        mProgressBar.setVisibility(GONE);
        txtInfo.setVisibility(VISIBLE);
        txtInfo.setText(message);
    }

    public void showInfoWhite(String message) {
        mLayout.setVisibility(VISIBLE);
        mLayout.setClickable(false);
        mLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
        mProgressBar.setVisibility(GONE);
        txtInfo.setVisibility(VISIBLE);
        txtInfo.setText(message);
    }

    public void showInfo(String message, boolean clickable) {
        mLayout.setVisibility(VISIBLE);
        mLayout.setClickable(clickable);
        mProgressBar.setVisibility(GONE);
        txtInfo.setVisibility(VISIBLE);
        txtInfo.setText(message);
    }

    public void hide() {
        mLayout.setVisibility(GONE);
    }

    public TextView getTxtInfo() {
        return txtInfo;
    }
}