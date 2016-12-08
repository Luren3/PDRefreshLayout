package com.sflin.pdrefreshlayout.Footer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.sflin.pdrefreshlayout.IFooterView;
import com.sflin.pdrefreshlayout.R;


/**
 * Created by sflin on 2016/12/1.
 */

public class DefaultLoadMoreView extends FrameLayout implements IFooterView {

    private ImageView mFootDown;
    private ImageView mFootLoading;
    private ImageView mFootFinish;
    private TextView  mFootContent;

    private String pullDownStr = "上拉加载";
    private String releaseRefreshStr = "释放加载";
    private String refreshingStr = "正在加载";
    private String refreshFinishStr = "加载完成";

    public DefaultLoadMoreView(Context context) {
        this(context, null);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init(){
        View rootView = View.inflate(getContext(), R.layout.layout_default_footer, null);
        mFootDown = (ImageView) rootView.findViewById(R.id.default_footer_down);
        mFootContent = (TextView) rootView.findViewById(R.id.default_footer_content);
        mFootLoading = (ImageView) rootView.findViewById(R.id.default_footer_loading);
        mFootFinish = (ImageView) rootView.findViewById(R.id.default_footer_finish);
        addView(rootView);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getFootHeight() {
        return 0;
    }

    @Override
    public void onLoadingUp(float moveY, float headHeight) {
        mFootLoading.setVisibility(GONE);
        mFootFinish.setVisibility(GONE);
        mFootDown.setVisibility(VISIBLE);
        if (-moveY<headHeight){
            mFootContent.setText(pullDownStr);
            mFootDown.setRotation(360f);
        }else {
            mFootDown.setRotation(180f);
            mFootContent.setText(releaseRefreshStr);
        }

    }

    @Override
    public void onLoadReleasing(float moveY, float headHeight,int state) {
        if (state == 0){
            mFootDown.setVisibility(GONE);
            mFootFinish.setVisibility(GONE);
            mFootContent.setText(refreshingStr);
            mFootLoading.setVisibility(VISIBLE);
            ((AnimationDrawable)mFootLoading.getDrawable()).start();
        }
    }

    @Override
    public void onFinish(float moveY, float headHeight,boolean isRefreshing) {
        mFootDown.setVisibility(GONE);
        mFootFinish.setVisibility(VISIBLE);
        mFootContent.setText(refreshFinishStr);
        mFootLoading.setVisibility(GONE);
    }

}
