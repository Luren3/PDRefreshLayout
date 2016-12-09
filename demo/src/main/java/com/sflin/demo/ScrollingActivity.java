package com.sflin.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sflin.pdrefreshlayout.PDRefreshLayout;
import com.sflin.pdrefreshlayout.PDRefreshListener;

public class ScrollingActivity extends AppCompatActivity {

    private PDRefreshLayout mPDRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        mPDRefreshLayout = (PDRefreshLayout) findViewById(R.id.refresh);
        mPDRefreshLayout.setRefreshListener(new PDRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPDRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPDRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }

            @Override
            public void onFinishRefresh() {
                super.onFinishRefresh();
            }

            @Override
            public void onFinishLoadMore() {
                super.onFinishLoadMore();
            }
        });
    }
}
