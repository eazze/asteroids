package de.hs_kl.gatav.flyingsaucerfull;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import de.hs_kl.gatav.flyingsaucerfull.objects.SpaceShip;

public class MainActivity extends Activity {

    private SpaceGLSurfaceView spaceGLSurfaceView;
    private WindowManager mWindowManager;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Display mDisplay;
    private boolean menu = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeScreen() throws IOException {
        Log.d("Menu: ", menu + "");

        if(!menu) {

            setContentView(R.layout.menu_start);

            Button play = (Button)findViewById(R.id.play);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        changeScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

        } else {

        spaceGLSurfaceView = new SpaceGLSurfaceView(this);
        spaceGLSurfaceView.context = this;
        setContentView(spaceGLSurfaceView);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View ll = inflater.inflate(R.layout.surface_view_overlay, null);
        addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Button buttonControlThrust = (Button) findViewById(R.id.controlThrust);
        // Register the onClick listener with the implementation above
        buttonControlThrust.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                accelerateTouch();


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    accelerateStop();
                    buttonControlThrust.setPressed(!buttonControlThrust.isPressed());

                    // Do what you want
                    return true;
                }
                return false;
            }
        });


        Button buttonControlLeft = (Button) findViewById(R.id.controlLeft);
        buttonControlLeft.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spaceGLSurfaceView.setRotationShip(false);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    spaceGLSurfaceView.stopRotationShip();
                    buttonControlLeft.setPressed(!buttonControlLeft.isPressed());
                    // Do what you want
                    return true;
                }
                return false;
            }
        });

        Button buttonControlRight = (Button) findViewById(R.id.controlRight);
        buttonControlRight.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spaceGLSurfaceView.setRotationShip(true);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    spaceGLSurfaceView.stopRotationShip();
                    buttonControlRight.setPressed(!buttonControlRight.isPressed());
                    // Do what you want
                    return true;
                }
                return false;
            }
        });

        Button buttonControlShot = (Button)findViewById(R.id.controlShot);
        buttonControlShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spaceGLSurfaceView.spawnNewShot();
            }

        });

        Button buttonControlMenu = (Button) findViewById(R.id.controlMenu);
        buttonControlMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }
        menu = !menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mDisplay = mWindowManager.getDefaultDisplay();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.menu_start);

        Button play = (Button)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    changeScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        //addContentView(gameWidgets, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }


    private void accelerateTouch() {
        float vecDir[] = spaceGLSurfaceView.getShipViewDirection();

        for (float i = 1; i < 5; i += 0.5f) {
            spaceGLSurfaceView.setShipVelocity(vecDir[0] * 10f, 0, vecDir[2] * 10f);
        }
    }

    private void accelerateStop() {
        float vecDir[] = spaceGLSurfaceView.getShipViewDirection();
        long startZeit = System.currentTimeMillis();
        /*for(float i = 5f; i > 0; i -= 0.5f) {
            spaceGLSurfaceView.setShipVelocity(vecDir[0]*i, vecDir[1], vecDir[2]*i);
            SystemClock.sleep(30);
        }*/
        float i = 10f;
        while ((System.currentTimeMillis() - startZeit) < 500) {
            //Log.d("Time: ", "" + (System.currentTimeMillis() - startZeit));
            spaceGLSurfaceView.setShipVelocity(vecDir[0] * i, vecDir[1], vecDir[2] * i);
            if (i > 1) i -= 1f;

        }
        spaceGLSurfaceView.setShipVelocity(0, 0, 0);

    }

}