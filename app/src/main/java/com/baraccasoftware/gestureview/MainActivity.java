package com.baraccasoftware.gestureview;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final VerticalScrollingLinearLayout view = (VerticalScrollingLinearLayout) findViewById(R.id.tt);

        final VerticalScrollingLinearLayout.VerticalScrollingLinearLayoutListener listenerV
                = new VerticalScrollingLinearLayout.VerticalScrollingLinearLayoutListener(view){

            @Override
            protected void onAnimationScrollEnd(Animation animation) {
                Log.d(MainActivity.class.getName(),"animation END");
            }

            @Override
            protected void onAnimationScrollStart(Animation animation) {
                Log.d(MainActivity.class.getName(),"animation START");
            }
        };

//        view.setOnTouchListener(new PanelTouchListener(MainActivity.this) {
//            @Override
//            public void remove() {
//
//                Log.d(MainActivity.class.getName(),"remove");
//            }
//        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view.setInterpolator(new AccelerateDecelerateInterpolator());
                //view.setDuration(600);
                view.set(700, new AccelerateDecelerateInterpolator(),listenerV);
                if(view.isOpen()){
                    view.close();
                }else {
                    view.open();
                }
            }
        });

//        FrameLayout frame = (FrameLayout) findViewById(R.id.graphics_holder);
//        PlayAreaView image = new PlayAreaView(this);
//        frame.addView(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
