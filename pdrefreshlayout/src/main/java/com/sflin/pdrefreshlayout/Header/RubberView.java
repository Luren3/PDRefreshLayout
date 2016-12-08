package com.sflin.pdrefreshlayout.Header;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sflin.pdrefreshlayout.IHeaderView;
import com.sflin.pdrefreshlayout.R;
import com.sflin.pdrefreshlayout.util.DensityUtil;


/**
 * Created by sflin on 2016/12/1.
 */

public class RubberView extends View implements IHeaderView {

    private Paint mPaint;
    private Paint mLoadPaint;
    private Paint mTextPaint;

    private float size;

    //大圆半径
    private float GreatCircleR;

    //小圆半径
    private float SmallCircleR;

    //大圆和小圆分别Y轴的坐标
    private float GreatCircleY, SmallCircleY;

    private float index;

    private int width,height;

    private Path mPath;

    private Bitmap mBitmap;

    private RectF mLoadingRectF;

    //顶部和底部开始角度
    private int mTopStartAngle = 10;
    private int mBottomStartAngle = 190;
    //扫过角度
    private int mSweepAngel = 10;

    private boolean flag = true;

    private ValueAnimator mAnimator;

    private int state = 0;

    public RubberView(Context context) {
        this(context,null);
    }

    public RubberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

//        setBackgroundColor(0xffe6e6e6);
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffbfbfbf);
        mPaint.setStyle(Paint.Style.FILL);

        mLoadPaint = new Paint();
        mLoadPaint.setColor(Color.WHITE);
        mLoadPaint.setAntiAlias(true);
        mLoadPaint.setStyle(Paint.Style.STROKE);
        mLoadPaint.setStrokeWidth(DensityUtil.dp2px(getContext(),3));
        mLoadPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(DensityUtil.dp2px(getContext(),15));

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_refresh);

        size = mBitmap.getWidth()/2+10;
        GreatCircleR = size;
        SmallCircleR = size;
        GreatCircleY = size+10;
        SmallCircleY = size+10;

        index = SmallCircleR+SmallCircleY;

//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        if (height==0){
            state = 0;
        }

        mLoadingRectF = new RectF(width/2-DensityUtil.dp2px(getContext(),10),DensityUtil.dp2px(getContext(),25),
                width/2+DensityUtil.dp2px(getContext(),10),DensityUtil.dp2px(getContext(),45));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //路径重置
        mPath.reset();

        switch (state){
            case 0:
                RectF rectF = new RectF(width / 2 - GreatCircleR, GreatCircleY - GreatCircleR,
                        width / 2 + GreatCircleR, GreatCircleY + GreatCircleR);
                //绘制大圆圆弧
                mPath.arcTo(rectF, 0, -180);
                //绘制左边的二次曲线
                mPath.quadTo(width / 2 - SmallCircleR, (GreatCircleY + SmallCircleY) / 2, width / 2 - SmallCircleR, SmallCircleY);
                //把点移动到大半圆的右边
                mPath.moveTo(width / 2 + GreatCircleR, GreatCircleY);
                //绘制右边的二次曲线
                mPath.quadTo(width / 2 + SmallCircleR, (GreatCircleY + SmallCircleY) / 2, width / 2 + SmallCircleR, SmallCircleY);
                //绘制小圆圆弧
                mPath.arcTo(new RectF(width / 2 - SmallCircleR, SmallCircleY - SmallCircleR,
                        width / 2 + SmallCircleR, SmallCircleY + SmallCircleR), 0, 180);

                canvas.drawPath(mPath, mPaint);
                Bitmap bitmap = resizeImage(mBitmap,GreatCircleR/size);
                canvas.drawBitmap(bitmap,rectF.centerX()-bitmap.getWidth()/2,rectF.centerY()-bitmap.getHeight()/2,null);
                break;
            case 1:
                canvas.drawArc(mLoadingRectF, mTopStartAngle,mSweepAngel, false, mLoadPaint);
                canvas.drawArc(mLoadingRectF, mBottomStartAngle, mSweepAngel, false, mLoadPaint);

                mTopStartAngle += 10;
                mBottomStartAngle += 10;
                if (mTopStartAngle > 360) {
                    mTopStartAngle = mTopStartAngle - 360;
                }
                if (mBottomStartAngle > 360) {
                    mBottomStartAngle = mBottomStartAngle - 360;
                }

                if (flag) {
                    if (mSweepAngel < 160) {
                        mSweepAngel += 2;
                        invalidate();
                    }
                } else {
                    if (mSweepAngel > 10) {
                        mSweepAngel -= 2 * 2;
                        invalidate();
                    }
                }
                if (mSweepAngel >= 160 || mSweepAngel <= 10) {
                    flag = !flag;
                    invalidate();
                }
                break;
            case 2:
                canvas.drawText("刷新完成",width / 2, DensityUtil.dp2px(getContext(),35),mTextPaint);
                break;
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getHeadHeight() {
        return DensityUtil.dp2px(getContext(),70);
    }

    @Override
    public void onRefreshingDown(float moveY, float headHeight) {
        if (moveY<headHeight){
            if (moveY>index){
                float dy = moveY - (SmallCircleY + SmallCircleR);
                if (Math.abs(dy) > 0) {
                    SmallCircleY = SmallCircleY + dy;
                    calculateR();
                }
                invalidate();
            }
        }else {
            state = 1;
        }
    }

    @Override
    public void onRefreshReleasing(float moveY, float headHeight,int state) {
        if (moveY<headHeight){
            if (moveY>index){
                float dy = moveY - (SmallCircleY + SmallCircleR);
                if (Math.abs(dy) > 0) {
                    SmallCircleY = SmallCircleY + dy;
                    calculateR();
                }
                invalidate();
            }
        }
    }

    @Override
    public void onFinish(float moveY, float headHeight,boolean isRefreshing) {
        state = 2;
    }

    private void calculateR(){
        float dy=SmallCircleY-GreatCircleY;

        float progress=dy/(height);
        SmallCircleR=(float)(size*(1-0.9*progress));
        GreatCircleR= (float) (size*(1-0.5*progress));
    }

    private Bitmap resizeImage(Bitmap bitmap, float scale) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    //添加动画
    private void addAnimator(){
        mAnimator = ValueAnimator.ofFloat(0,360);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(-1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

}
