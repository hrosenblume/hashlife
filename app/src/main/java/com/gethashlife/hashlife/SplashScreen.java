package com.gethashlife.hashlife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;

public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 999999999;
    Animation zoomin, zoomout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView i = (ImageView) findViewById(R.id.sqr);
        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoomout = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        i.setAnimation(zoomin);
        i.setAnimation(zoomout);

        zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                ImageView i = (ImageView) findViewById(R.id.sqr);
                //zoomout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                i.startAnimation(zoomout);

            }
        });

        zoomout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                ImageView i = (ImageView) findViewById(R.id.sqr);
                i.startAnimation(zoomin);
            }
        });

        Encryption.generateKeys();
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg");
        try {
            registerDevice();
        } catch (Exception e) {
            System.out.println(e);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MyActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void registerDevice() throws JSONException {
        Toast.makeText(getApplicationContext(), (String) "Currently Registering Device",
                Toast.LENGTH_LONG).show();
        SMS.sendMessage("347-269-2418", "This message will register my device.");
    }

    public void kill_activity()
    {
        finish();
    }
}
