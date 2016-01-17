package com.carpediem.randy.lib;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * Created by randy on 16-1-17.
 */
public class FoldableTextView extends TextView {
    private final static int DEFAULT_MAX_LINE = 3;
    private final static int DEFAULT_ANIM_TIME = 500;
    private int mActualLineCount ;
    private int mMaxLineCount = DEFAULT_MAX_LINE;
    private boolean mIsFold = true;
    private int mAnimTime = DEFAULT_ANIM_TIME;
    public FoldableTextView(Context context) {
        super(context);
        init(context);
    }

    public FoldableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FoldableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        setBackgroundColor(Color.BLUE);
    }

//   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public FoldableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mActualLineCount = getLineCount();
        if (getLineCount() > mMaxLineCount && mIsFold) {
            setMaxLines(mMaxLineCount);
            mIsFold = false;
        }
    }


    public void fold() {
        float currentHeight = getHeight();
        float endHeight = getLayout().getLineTop(mMaxLineCount);
        Animation animation = new FoldAnimation(this,currentHeight,endHeight,mAnimTime);
        animation.setFillAfter(true);
        startAnimation(animation);
    }

    public  void unFold() {

        log(getLayout().getLineTop(getLineCount()) + " " + getLineHeight());
        float currentHeight = getHeight();
        float endHeight = getActualLineHeight();
        log(" unFold" + currentHeight +" "+endHeight);
        Animation animation = new FoldAnimation(this,currentHeight,endHeight,mAnimTime);
        animation.setFillAfter(true);
        startAnimation(animation);
    }

    private int getActualLineHeight() {
        return getLayout().getLineTop(mActualLineCount);
    }

    private void log(String msg) {
        Log.d("TEST",msg);
    }

    private void applyAlphaTransformation() {

    }

    class FoldAnimation extends Animation {
        private  View mTarget;
        private float mStartHeight;
        private float mEndHeight;
        private int mAnimationDuration;
        public FoldAnimation(View target,float startHeight,float endHeight,int duration) {
            super();
            mTarget = target;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            mAnimationDuration = duration;
            setDuration(mAnimationDuration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            log(interpolatedTime+" ddd");
            int currentHeight =(int)((mEndHeight - mStartHeight) * interpolatedTime  + mStartHeight);
            mTarget.getLayoutParams().height = currentHeight;
            mTarget.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
