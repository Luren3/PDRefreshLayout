# PDRefreshLayout

### PDRefreshLayout是一个支持上拉刷新，下拉加载的控件。主要特性有：

1. 支持任何布局的下拉刷新
2. 支持加载更多
3. 可以自定义Header的刷新效果和Footer的加载更多效果
4. 支持刷新和加载时的监听事件
5. 支持自动弹出下拉刷新方法

## 效果图
![3](http://o9o9d242i.bkt.clouddn.com/3.gif?imageView2/3/w/400/h/400)
![2](http://o9o9d242i.bkt.clouddn.com/2.gif?imageView2/3/w/400/h/400)
![1](http://o9o9d242i.bkt.clouddn.com/1.gif?imageView2/3/w/400/h/400)

## 使用方法
1. **添加gradle依赖**
	
		compile 'com.sflin:pdrefreshlayout:1.0.5'
		
2. **在xml中添加PDRefreshLayout**

		<?xml version="1.0" encoding="utf-8"?>
		<com.sflin.pdrefreshlayout.PDRefreshLayout
		    xmlns:android="http://schemas.android.com/apk/res/android"
		    android:id="@+id/refresh"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent">
		
		
		    <android.support.v7.widget.RecyclerView
                android:id="@+id/client_list"
                android:overScrollMode="never"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>
		
		</com.sflin.pdrefreshlayout.PDRefreshLayout>

3. **在Activity中添加监听事件**

		mRefresh.setRefreshListener(new PDRefreshListener() {
            @Override
            public void onRefresh() {//刷新中时调用
            	super. onRefresh();
            	new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	//这个方法设置false来完成刷新
                        mRefresh.setRefreshing(false);
                    }
                },1000);
            }

            @Override
            public void onLoadMore() {//加载中时调用
            	super. onLoadMore();
            	new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	//这个方法设置false来完成加载
                        mRefresh.setRefreshing(false);
                    }
                },1000);
            }

            @Override
            public void onFinishRefresh() {//刷新完成调用
                super.onFinishRefresh();
                
            }

            @Override
            public void onFinishLoadMore() {//加载完成调用
                super.onFinishLoadMore();
                
            }
        });
        
  以上3步就可以完成刷新或者加载
  
#### 除了以上几个方法，控件还提供其他几个方法
1. setRefreshState(boolean state)
	* true表示开启下拉刷新(默认开启)
	* false表示禁止下拉刷新
	
2. setLoadMoreState(boolean state)
	* true表示开启上拉加载(默认开启)
	* false表示禁止上拉加载

3. setHeadHeight(float mHeadHeight) 设置头部下拉高度（就是表示头部下拉到此高度，可进入刷新状态）

4. setFootHeight(float mFootHeight) 设置底部上拉高度（就是表示底部上拉到此高度，可进入加载状态）

5. setHeaderView(IHeaderView headerView) 设置头部下拉刷新效果

6. setBottomView(IFooterView bottomView) 设置底部上拉加载效果

7. public void setIsAutoPull(boolean isAutoPull) --true 设置自动弹出下拉头刷新

#### 自定义Header和Footer

1. Header

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
		
	具体实现代码可以参考[DefaultRefreshView](https://github.com/HellForGate/PDRefreshLayout/blob/master/pdrefreshlayout/src/main/java/com/sflin/pdrefreshlayout/Header/DefaultRefreshView.java)和[RubberView](https://github.com/HellForGate/PDRefreshLayout/blob/master/pdrefreshlayout/src/main/java/com/sflin/pdrefreshlayout/Header/RubberView.java)的实现代码
	
2. Footer

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
		
	具体实现代码可以参考[DefaultLoadMoreView](https://github.com/HellForGate/PDRefreshLayout/blob/master/pdrefreshlayout/src/main/java/com/sflin/pdrefreshlayout/Footer/DefaultLoadMoreView.java)的实现代码
	
#### 其他说明
* 控件只能有一个子View


如果你觉得还不错，欢迎Star. 欢迎加入交流群: 114925972