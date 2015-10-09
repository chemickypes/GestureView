package com.baraccasoftware.gestureview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by angelomoroni on 09/10/15.
 */
public class VerticalScrollingLinearLayout extends LinearLayout {

    private static final int ZERO_TARGET_HEIGHT = 0;
    private static final int MIN_DURATION = 200;
    private  ResizeAnimation resizeAnimation;
    private int targetHeight = ZERO_TARGET_HEIGHT;
    private int initialHeight;
    private int duration;

    private boolean open = true;
    private boolean setFlag = false;
    private boolean animating = false;

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
