package com.sflin.pdrefreshlayout;

import android.view.View;

/**
 * Created by sflin on 2016/12/1.
 */

public interface IFooterView {

    View getView();

    int getFootHeight();

    /**
     * 上拉准备加载更多的动作
     * @param moveY 移动的距离
     * @param bottomHeight 底部高度
     */
    void onLoadingUp(float moveY, float bottomHeight);

    /**
     * 上拉释放过程
     * @param moveY
     * @param bottomHeight
     * @param state 状态值 0表示手指离开屏幕 1表示加载回弹
     */
    void onLoadReleasing(float moveY, float bottomHeight,int state);

    /**
     * 完成加载
     * @param moveY
     * @param bottomHeight
     * @param isRefreshing 是否在刷新
     */
    void onFinish(float moveY, float bottomHeight,boolean isRefreshing);
}
