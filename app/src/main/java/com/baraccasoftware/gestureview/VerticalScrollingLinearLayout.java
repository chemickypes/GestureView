package com.baraccasoftware.gestureview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by angelomoroni on 09/10/15.
 */
public class VerticalScrollingLinearLayout extends LinearLayout {

    private static final int ZERO_TARGET_HEIGHT = 0;
    private static final int MIN_DURATION = 200;
    private static final String TAG = VerticalScrollingLinearLayout.class.getName();
    private  ResizeAnimation resizeAnimation;
    private int targetHeight = ZERO_TARGET_HEIGHT;
    private int initialHeight;
    private int duration;

    private boolean open = true;
    private boolean setFlag = false;
    private boolean animating = false;

    private float mLastX,mStartY,mStartX,mLastY;
    private boolean mIsAnimating = false;
    private int mTouchSlop;


    private float iY,iX;

    private int action;
    private int windowwidth;
   private int  x_cord,y_cord,x,y;
    private int screenCenter;
    private boolean isScollViewScrollable;

    public VerticalScrollingLinearLayout(Context context) {
        super(context);

        init();
    }

    public VerticalScrollingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalScrollingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        resizeAnimation = new ResizeAnimation(this);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        windowwidth = size.x;

        screenCenter = windowwidth / 2;
    }


    private void startAnimation() {

        startAnimation(resizeAnimation);
    }

    public void close(){
        if(open && !animating){
            //initialHeight = getHeight();
            if(!setFlag) set(0,null);
            startAnimation(initialHeight,targetHeight);
            open = false;
        }
    }

    public void open(){
        if(!open && !animating){
            if(!setFlag) set(0,null);
            startAnimation(targetHeight,initialHeight);
            open = true;
        }
    }

    public void set(int duration,@Nullable Interpolator interpolator){
       set(ZERO_TARGET_HEIGHT,duration,interpolator);

    }

    public void set(int duration,@Nullable Interpolator interpolator,
                    @Nullable VerticalScrollingLinearLayoutListener listener){
        set(ZERO_TARGET_HEIGHT,duration,interpolator, listener);
    }

    public void set(int targetHeight,int duration,@Nullable Interpolator interpolator){
        set(targetHeight, duration, interpolator, null);
    }

    public void set(int targetHeight,int duration,
                    @Nullable Interpolator interpolator,
                    @Nullable VerticalScrollingLinearLayoutListener listener ){
        if(!setFlag) {
            initialHeight = getHeight();
            this.targetHeight = targetHeight;
            setDuration(duration > MIN_DURATION ? duration : MIN_DURATION);
            if (interpolator != null) setInterpolator(interpolator);
            setAnimationListener(listener != null? listener : buildVerticalScollingLinearLayoutListener());

            setFlag = true;
        }
    }

    private void startAnimation(int initialHeight, int targetHeight){
        //this.targetHeight = targetHeight;

        resizeAnimation.setMisures(initialHeight,targetHeight);
        startAnimation();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                mStartX = mLastX;
                mStartY = mLastY;

                //coordinate iniziali
                iY = getY();
                iX = getX();

                setIfScrollViewCanScroll();
                Log.d(TAG,"INT MotionEvent.ACTION_DOWN");
                Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsAnimating = false;
                Log.d(TAG,"INT MotionEvent.ACTION_CANCEL or MotionEvent.ACTION_UP");
                Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG,"INT MotionEvent.ACTION_MOVE");
                Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);
                float x = ev.getRawX();
                float y = ev.getRawY();
                float xDelta = Math.abs(x - mLastX);
                float yDelta = Math.abs(y - mLastY);

                float yDeltaTotal = y - mStartY;
                float xDeltaTotal = x - mStartX;
                if(mIsAnimating || !isScollViewScrollable){


                    Log.d(TAG,"INT MotionEvent.ACTION_MOVE ANIMATING");
                    Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);

                    return true;

                }else {
                    if (Math.abs(xDelta)>Math.abs(yDelta) && Math.abs(xDelta) > mTouchSlop) {
                        mIsAnimating = true;
                        mStartX = x;
                        Log.d(TAG,"INT MotionEvent.ACTION_MOVE start ANIMATING");
                        Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);
                        return true;
                    }else  {
                        mIsAnimating = false;
                        Log.d(TAG,"INT MotionEvent.ACTION_MOVE stop ANIMATING");
                        Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);

                        //mStartY = y;
                        //return false;
                    }
                }

                break;
        }

        return false;
    }

    private void setIfScrollViewCanScroll() {
        ScrollView scrollView = (ScrollView) getChildAt(0);
        View child = scrollView.getChildAt(0);
        isScollViewScrollable = scrollView.getHeight() < child.getHeight() + scrollView.getPaddingTop() + scrollView.getPaddingBottom();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsAnimating = false;
                Log.d(TAG,"INT MotionEvent.ACTION_CANCEL or MotionEvent.ACTION_UP");
                Log.d(TAG,"INT mIsAnimating: "+mIsAnimating);

                if (action == 0) {
                    // Log.e("Event Status", "Nothing");
                    animate().y(iY).x(iX).rotation(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animating = false;
                        }
                    });

                } else {


                    animate().y(iY).x(iX).rotation(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animating = false;
                        }
                    });
                }

                break;
            case MotionEvent.ACTION_MOVE:
                x_cord = (int) event.getRawX();
                y_cord = (int) event.getRawY();

                setX(iX + (x_cord - mLastX));
                setY(iY + (y_cord - mLastY));
                if (x_cord >= screenCenter) {
                    //v.setRotation((float) ((x_cord - x) * (Math.PI / 42)));
                    if (x_cord > (screenCenter + (screenCenter / 2))) {

                        if (x_cord > (windowwidth - (screenCenter / 4))) {
                            action = 2;
                        } else {
                            action = 0;
                        }
                    } else {
                        action = 0;

                    }

                } else {
                    // rotate
                   // v.setRotation((float) ((x_cord - x) * (Math.PI / 42)));
                    if (x_cord < (screenCenter / 2)) {

                        if (x_cord < screenCenter / 4) {
                            action = 1;
                        } else {
                            action = 0;
                        }
                    } else {
                        action = 0;

                    }

                }
                break;
            default: return super.onTouchEvent(event);
        }

        return true;

    }

    public VerticalScrollingLinearLayoutListener buildVerticalScollingLinearLayoutListener(){
        return new VerticalScrollingLinearLayoutListener(this) {
            @Override
            protected void onAnimationScrollEnd(Animation animation) {
                //nothing
            }

            @Override
            protected void onAnimationScrollStart(Animation animation) {
                //nothing
            }
        };

    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
        resizeAnimation.setTargetHeight(this.targetHeight);
    }

    public void setAnimationListener(VerticalScrollingLinearLayoutListener listener){
        resizeAnimation.setAnimationListener(listener);
    }

    public void setInterpolator(Interpolator interpolator){
        resizeAnimation.setInterpolator(interpolator);
    }

    public void setDuration(int duration) {
        this.duration = duration;
        resizeAnimation.setDuration(this.duration);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    private void showOrHideViews(boolean toShow) {
        for(int i = 0; i<getChildCount();i++){
            getChildAt(i).animate().alpha(toShow?1:0).setDuration(toShow?duration+50:duration-50);
        }
    }

    public static abstract class VerticalScrollingLinearLayoutListener implements Animation.AnimationListener {

        VerticalScrollingLinearLayout linearLayout;

        public VerticalScrollingLinearLayoutListener(VerticalScrollingLinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            linearLayout.setAnimating(false) ;
            onAnimationScrollEnd(animation);
        }

        protected abstract void onAnimationScrollEnd(Animation animation);

        @Override
        public void onAnimationStart(Animation animation) {
            linearLayout.setAnimating(true) ;
            linearLayout.showOrHideViews(linearLayout.isOpen());
            onAnimationScrollStart(animation);
        }

        protected abstract void onAnimationScrollStart(Animation animation);

        @Override
        public void onAnimationRepeat(Animation animation) {
            onAnimationScrollRepeat(animation);
        }

        private void onAnimationScrollRepeat(Animation animation) {
            //nothing
        }


    }

    public static class ResizeAnimation extends Animation {

        int startHeight;
        int targetHeight;
        View view;

        public ResizeAnimation(View view, int targetHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
            startHeight = view.getHeight();
        }

        public ResizeAnimation(View view) {
            this.view = view;
        }

        public void setMisures(int startHeight, int targetHeight){
            this.targetHeight = targetHeight;
            this.startHeight = startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int Height, int height, int parentWidth, int parentHeight) {
            super.initialize(Height, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }

        public void setTargetHeight(int targetHeight) {
            this.targetHeight = targetHeight;
        }
    }


}
