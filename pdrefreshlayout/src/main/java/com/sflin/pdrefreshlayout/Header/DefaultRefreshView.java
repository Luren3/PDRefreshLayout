package com.sflin.pdrefreshlayout.Header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.sflin.pdrefreshlayout.IHeaderView;
import com.sflin.pdrefreshlayout.R;


/**
 * Created by sflin on 2016/12/1.
 */

public class DefaultRefreshView extends FrameLayout implements IHeaderView {

    private ImageView mHeadDown;
    private ImageView mHeadLoading;
    private ImageView mHeadFinish;
    private TextView  mHeadContent;

    private String pullDownStr = "下拉刷新";
    private String releaseRefreshStr = "释放刷新";
    private String refreshingStr = "正在刷新";
    private String refreshFinishStr = "刷新完成";

    public DefaultRefreshView(Context context) {
        this(context, null);
    }

    public DefaultRefreshView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(){
        View rootView = View.inflate(getContext(), R.layout.layout_default_header, null);
        mHeadDown = (ImageView) rootView.findViewById(R.id.default_header_down);
        mHeadContent = (TextView) rootView.findViewById(R.id.default_header_content);
        mHeadLoading = (ImageView) rootView.findViewById(R.id.default_header_loading);
        mHeadFinish = (ImageView) rootView.findViewById(R.id.default_header_finish);
        addView(rootView);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getHeadHeight() {
        return 0;
    }

    @Override
    public void onRefreshingDown(float moveY, float headHeight) {
        mHeadLoading.setVisibility(GONE);
        mHeadFinish.setVisibility(GONE);
        mHeadDown.setVisibility(VISIBLE);
        if (moveY < headHeight){
            mHeadContent.setText(pullDownStr);
            mHeadDown.setRotation(360f);
        }else if (moveY > headHeight){
            mHeadContent.setText(releaseRefreshStr);
            mHeadDown.setRotation(180f);
        }
    }

    @Override
    public void onRefreshReleasing(float moveY, float headHeight,int state) {
        if (state == 0){
            mHeadDown.setVisibility(GONE);
            mHeadFinish.setVisibility(GONE);
            mHeadContent.setText(refreshingStr);
            mHeadLoading.setVisibility(VISIBLE);
            ((AnimationDrawable)mHeadLoading.getDrawable()).start();
        }
    }

    @Override
    public void onFinish(float moveY, float headHeight,boolean isRefreshing) {
        mHeadDown.setVisibility(GONE);
        mHeadContent.setText(refreshFinishStr);
        mHeadLoading.setVisibility(GONE);
        mHeadFinish.setVisibility(VISIBLE);
        Log.e("dsd",mHeadContent.getText()+"");
    }
}
