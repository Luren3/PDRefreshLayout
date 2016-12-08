package com.sflin.pdrefreshlayout;

/**
 * Created by sflin on 2016/12/2.
 */

public interface IPDRefreshListener {

    //上拉刷新中
    void onRefresh();

    //下拉加载中
    void onLoadMore();

    //上拉刷新完成
    void onFinishRefresh();

    //下拉加载完成
    void onFinishLoadMore();
}
