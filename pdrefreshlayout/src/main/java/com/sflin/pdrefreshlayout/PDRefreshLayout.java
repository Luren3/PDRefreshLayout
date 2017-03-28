package com.sflin.pdrefreshlayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.sflin.pdrefreshlayout.Footer.DefaultLoadMoreView;
import com.sflin.pdrefreshlayout.Header.RubberView;
import com.sflin.pdrefreshlayout.util.DensityUtil;

/**
 * Created by sflin on 2016/12/1.
 */

public class PDRefreshLayout extends FrameLayout {

    //手指按下的坐标
    private float lastY;
    private float firstY;

    //刷新状态 1刷新状态 2加载状态 3没有添加子View状态
    private static final int REFRESH = 1;
    private static final int LOADING = 2;
    private static final int NoVIEW = 3;
    private int state = REFRESH;

    // 阻尼系数
    private static final float SCROLL_RATIO = 0.3f;

    private boolean isRefreshing = false;

    //下拉状态
    private boolean refreshState = true;
    //加载更多状态
    private boolean loadMoreState = true;

    //多点触控pointId
    private int mActivePointerId = -1;

    private IHeaderView mHeadView;
    private IFooterView mFooterView;

    //头部layout
    private FrameLayout mHeadLayout;

    //头部的高度
    protected float mHeadHeight;

    //底部layout
    private FrameLayout mFootLayout;

    //底部高度
    private float mFootHeight;

    private View mChildView;

    private PDRefreshListener mPDRefreshListener;


    public PDRefreshLayout(Context context) {
        this(context,null);
    }

    public PDRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mHeadHeight = DensityUtil.dp2px(context,50);
        mFootHeight = DensityUtil.dp2px(context,50);
    }

    //设置下拉状态
    public void setRefreshState(boolean state){
        refreshState = state;
    }

    //设置加载更多状态
    public void setLoadMoreState(boolean state){
        loadMoreState = state;
    }

    //设置头部高度
    public void setHeadHeight(float mHeadHeight){
        this.mHeadHeight = mHeadHeight;
    }

    //设置底部高度
    public void setFootHeight(float mFootHeight){
        this.mFootHeight = mFootHeight;
    }

    private void init(){
        //使用isInEditMode解决可视化编辑器无法识别自定义控件的问题
        if (isInEditMode()) return;
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("PDRefreshLayout can host only one direct child");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("PDRefreshLayout can host only one direct child");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("PDRefreshLayout can host only one direct child");
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("PDRefreshLayout can host only one direct child");
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getChildCount() == 0){
            state = NoVIEW;
            return;
        }

        state = REFRESH;

        //添加头部
        if (mHeadLayout == null) {
            mHeadLayout = new FrameLayout(getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            layoutParams.gravity = Gravity.TOP;
            mHeadLayout.setLayoutParams(layoutParams);
            this.addView(mHeadLayout);

            if (mHeadView == null) {
//                setHeaderView(new DefaultRefreshView(getContext()));
                setHeaderView(new RubberView(getContext()));
                if (mHeadView.getHeadHeight()>0){
                    setHeadHeight(mHeadView.getHeadHeight());
                }
            }
        }

        //添加底部
        if (mFootLayout == null) {
            mFootLayout = new FrameLayout(getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            layoutParams.gravity = Gravity.BOTTOM;
            mFootLayout.setLayoutParams(layoutParams);
            this.addView(mFootLayout);

            if (mFooterView == null) {
                setBottomView(new DefaultLoadMoreView(getContext()));
                if (mFooterView.getFootHeight()>0){
                    setFootHeight(mFooterView.getFootHeight());
                }
            }
        }

        //获得子控件
        mChildView = getChildAt(0);
        if (mChildView == null) return;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                lastY = event.getY();
                firstY = lastY;
                if (isRefreshing){//刷新加载时，拦截点击事件
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY() - lastY;
                if (moveY>0 && !canChildScrollUp() && refreshState && !isRefreshing){
                    state = REFRESH;
                    return true;
                }
                if (moveY<0 && !canChildScrollDown() && loadMoreState && !isRefreshing){
                    state = LOADING;
                    return true;
                }
            case MotionEvent.ACTION_UP:
                break;        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshing) return super.onTouchEvent(event);
        int pointerIndex = -1;
        switch (event.getAction()&MotionEvent.ACTION_MASK){//MotionEvent.ACTION_MASK 配合多点触控
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                lastY = event.getY(pointerIndex);
                if (state == REFRESH) {
                    float moveY = Math.max(0, event.getY(pointerIndex) - firstY);
                    moveY = moveY * SCROLL_RATIO;
                    mHeadLayout.getLayoutParams().height = (int) moveY;
                    mHeadLayout.requestLayout();
                    mChildView.setTranslationY(moveY);
                    mHeadView.onRefreshingDown(mChildView.getTranslationY(),mHeadHeight);
                }else if (state == LOADING){
                    float moveY = Math.min(0, event.getY(pointerIndex) - firstY);
                    moveY = moveY * SCROLL_RATIO;
                    mFootLayout.getLayoutParams().height = (int) -moveY;
                    mFootLayout.requestLayout();
                    mChildView.setTranslationY(moveY);
                    mFooterView.onLoadingUp(mChildView.getTranslationY(),mFootHeight);
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:// 多点触摸按下action
                pointerIndex = MotionEventCompat.getActionIndex(event);
                if (pointerIndex < 0) {
                    return false;
                }
                firstY = event.getY(pointerIndex)-(lastY-firstY);
                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:// 多点触摸抬起action
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (state == REFRESH) {
                    if (mChildView.getTranslationY()<mHeadHeight){
                        animChildView(0f);
                    }else {
                        mHeadView.onRefreshReleasing(mChildView.getTranslationY(),mHeadHeight,0);
                        isRefreshing = true;
                        animChildView(mHeadHeight);
                        if (mPDRefreshListener!=null){
                            mPDRefreshListener.onRefresh();
                        }
                    }
                }else if (state == LOADING){
                    if (mChildView.getTranslationY()>-mFootHeight){
                        animChildView(0f);
                    }else {
                        mFooterView.onLoadReleasing(mChildView.getTranslationY(),mFootHeight,0);
                        isRefreshing = true;
                        animChildView(-mFootHeight);
                        if (mPDRefreshListener!=null){
                            mPDRefreshListener.onLoadMore();
                        }
                    }
                }
                break;
        }
        return true;
    }

    //检测子View是否能下拉
    private boolean canChildScrollUp(){
        if (canScrollUp(mChildView)){
            return true;
        }else {
            ViewGroup viewGroup = (ViewGroup) mChildView;
            for (int i = 0; i <viewGroup.getChildCount() ; i++) {
                View view = viewGroup.getChildAt(i);
                if (canScrollUp(view)){
                    return true;
                }
            }

        }
        return false;
    }

    //检测子View是否能上拉
    private boolean canChildScrollDown(){
        if (canScrollDown(mChildView)){
            return true;
        }else {
            ViewGroup viewGroup = (ViewGroup) mChildView;
            for (int i = 0; i <viewGroup.getChildCount() ; i++) {
                View view = viewGroup.getChildAt(i);
                if (canScrollDown(view)){
                    return true;
                }
            }
        }
        return false;
    }


    //能否下拉
    private boolean canScrollUp(View view) {
        if (view == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(view, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }

    //能否上拉
    private boolean canScrollDown(View view) {
        if (view == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(view, 1) || view.getScrollY() < 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, 1);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    public void setRefreshing(boolean refreshing){
        if (isRefreshing == refreshing){
            return;
        }
        if (!refreshing){
            if (state == REFRESH){
                if (mPDRefreshListener!=null){
                    mPDRefreshListener.onFinishRefresh();
                }
                mHeadView.onFinish(mChildView.getTranslationY(),mHeadHeight,isRefreshing);
            }else if (state == LOADING){
                if (mPDRefreshListener!=null){
                    mPDRefreshListener.onFinishLoadMore();
                }
                mFooterView.onFinish(mChildView.getTranslationY(),mHeadHeight,isRefreshing);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animChildView(0f);
                }
            },1000);
        }
    }

    public void setIsAutoPull(boolean isAutoPull){
        if (isAutoPull){
            state = REFRESH;
            isRefreshing = true;
            mHeadLayout.getLayoutParams().height = (int) mHeadHeight;
            mHeadLayout.requestLayout();
            mChildView.setTranslationY(mHeadHeight);
            mHeadView.onRefreshingDown(mChildView.getTranslationY(),mHeadHeight);
        }
    }

    //刷新监听
    public void setRefreshListener(PDRefreshListener refreshListener){
        if (mPDRefreshListener == null){
            mPDRefreshListener = refreshListener;
        }
    }

    //设置头部View
    public void setHeaderView(final IHeaderView headerView) {
        if (headerView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHeadLayout.removeAllViewsInLayout();
                    mHeadLayout.addView(headerView.getView());
                }
            });
            mHeadView = headerView;
        }
    }

    //设置底部View
    public void setBottomView(final IFooterView bottomView) {
        if (bottomView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mFootLayout.removeAllViewsInLayout();
                    mFootLayout.addView(bottomView.getView());
                }
            });
            mFooterView = bottomView;
        }
    }

    private void animChildView(float endValue) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mChildView, "translationY", mChildView.getTranslationY(), endValue);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) mChildView.getTranslationY();
                height = Math.abs(height);

                if (state == REFRESH) {
                    mHeadView.onRefreshReleasing(mChildView.getTranslationY(),mHeadHeight,1);
                    mHeadLayout.getLayoutParams().height = height;
                    mHeadLayout.requestLayout();
                    if (height == 0){
                        isRefreshing = false;
                    }
                } else if (state == LOADING) {
                    mFooterView.onLoadReleasing(mChildView.getTranslationY(),mFootHeight,1);
                    mFootLayout.getLayoutParams().height = height;
                    mFootLayout.requestLayout();
                    if (height == 0){
                        isRefreshing = false;
                    }
                }
            }
        });
        animator.start();
    }
}
