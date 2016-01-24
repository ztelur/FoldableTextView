package com.carpediem.randy.lib;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * Created by randy on 16-1-17.
 */
public class FoldableTextView extends TextView {
    private final static int DEFAULT_MAX_LINE = 3;
    private final static int DEFAULT_ANIM_TIME = 500;
    private final static float DEFAULT_ALPHA_START = 0.8f;
    private int mActualLineCount ;
    private int mMaxLineCount = DEFAULT_MAX_LINE;
    private boolean mIsFold = true;
    private int mAnimTime = DEFAULT_ANIM_TIME;
    private float mAnimationAlphaStart = DEFAULT_ALPHA_START;

    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            applyAlphaTransformation(mAnimationAlphaStart);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
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
    private boolean mRelayout = false;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMaxLines(Integer.MAX_VALUE); //TODO:很重要的

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mActualLineCount = getLineCount();

        if (getLineCount() > mMaxLineCount && mIsFold) {
            log("set max line");
            setMaxLines(mMaxLineCount);
        } else {
            log("not set max line");
        }
    }


    public void fold() {
        mIsFold = true;
        float currentHeight = getHeight();
        float endHeight = getLayout().getLineTop(mMaxLineCount);
        Animation animation = new FoldAnimation(this,currentHeight,endHeight,mAnimTime);
        animation.setFillAfter(true);
        clearAnimation();
        startAnimation(animation);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public  void unFold() {
        mIsFold=false;
        log(getLayout().getLineTop(getLineCount()) + " " + getLineHeight());
        float currentHeight = getHeight();
        float endHeight = getActualLineHeight();
        log(" unFold" + currentHeight +" "+endHeight);
        Animation animation = new FoldAnimation(this,currentHeight,endHeight,mAnimTime);
        animation.setAnimationListener(mAnimationListener);
        clearAnimation();
        animation.setFillAfter(true);
        startAnimation(animation);
    }

    private int getActualLineHeight() {
        return getLayout().getLineTop(mActualLineCount);
    }

    private void log(String msg) {
        Log.d("TEST",msg);
    }


    private static boolean isPostHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private static boolean isPostLolipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private void applyAlphaTransformation(float alpha) {
        if (isPostHoneycomb()) {
            setAlpha(alpha);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            this.startAnimation(alphaAnimation);
        }
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
            int currentHeight =(int)((mEndHeight - mStartHeight) * interpolatedTime  + mStartHeight);
            if (Float.compare(mAnimationAlphaStart,1.0f) !=0) {
                applyAlphaTransformation(mAnimationAlphaStart + interpolatedTime * (1.0f - mAnimationAlphaStart));
            }
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
