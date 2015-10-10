package com.baraccasoftware.gestureview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * Created by angelomoroni on 10/10/15.
 */
public  abstract class PanelTouchListener implements View.OnTouchListener {

    private final Context context;
    private final int mTouchSlop;
    private int windowwidth;
    private int screenCenter;
    private int x_cord, y_cord, x, y;
    private float alphaValue = 0;
    private float iX,iY;
    private int action;
    private boolean animating = false;

    public PanelTouchListener(Context context) {

        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        windowwidth = size.x;

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();


        Log.d("TOUCHEPANEL", "x e y "+ iX +" "+iY);


        screenCenter = windowwidth / 2;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x_cord = (int) event.getRawX();
        y_cord = (int) event.getRawY();

        //v.setX(x_cord - screenCenter );
        //v.setY(y_cord );
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                if (!animating) {
                    iX = v.getX();
                    iY = v.getY();
                    animating = true;
                }

                Log.d("TOUCHEPANEL DOWN", "x e y "+ iX +" "+iY);
                Log.v("On touch", x + " " + y);
                break;
            case MotionEvent.ACTION_MOVE:
                x_cord = (int) event.getRawX(); // Updated for more
                // smoother animation.
                y_cord = (int) event.getRawY();
//                if(deltaX() >25 || deltaY()>25 ){
//
//                }
                v.setX(iX + (x_cord - x));
                v.setY(iY + (y_cord - y));
                // v.setY(y_cord-y);﻿
                // y_cord = (int) event.getRawY();
                // v.setX(x_cord - screenCenter + 40);
                // v.setY(y_cord - 150);
                if (x_cord >= screenCenter) {
                    v.setRotation((float) ((x_cord - x) * (Math.PI / 42)));
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
                    v.setRotation((float) ((x_cord - x) * (Math.PI / 42)));
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

            case MotionEvent.ACTION_UP:
                x_cord = (int) event.getRawX();
                y_cord = (int) event.getRawY();

                Log.e("X Point", "" + x_cord + " , Y " + y_cord);



                if (action == 0) {
                    // Log.e("Event Status", "Nothing");
                    v.animate().y(iY).x(iX).rotation(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animating = false;
                        }
                    });
//                    v.setX(iX);
//                    v.setY(iY);
//                    v.setRotation(0);
                } else {
                    //ok dobbiamo toglierlo
                    remove();
                    animating = false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private int deltaY() {
        return Math.abs(y_cord - y);
    }

    private int deltaX() {
        return Math.abs(x_cord-x);
    }


    public abstract void remove() ;
}