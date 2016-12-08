package com.sflin.pdrefreshlayout;

import android.view.View;

/**
 * Created by sflin on 2016/12/1.
 */

public interface IHeaderView {

    View getView();

    int getHeadHeight();

    /**
     * 下拉准备刷新动作
     * @param moveY 移动的距离
     * @param headHeight 底部高度
     */
    void onRefreshingDown(float moveY, float headHeight);

    /**
     * 下拉释放过程
     * @param moveY
     * @param headHeight
     * @param state 状态值 0表示手指离开屏幕 1表示刷新回弹
     */
    void onRefreshReleasing(float moveY, float headHeight,int state);

    /**
     * 完成刷新
     * @param moveY
     * @param headHeight
     * @param isRefreshing 是否在刷新
     */
    void onFinish(float moveY, float headHeight,boolean isRefreshing);
}
