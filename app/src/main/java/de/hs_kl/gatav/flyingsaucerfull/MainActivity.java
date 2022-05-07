package de.hs_kl.gatav.flyingsaucerfull;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import de.hs_kl.gatav.flyingsaucerfull.objects.SpaceShip;

public class MainActivity extends Activity  {

    private SpaceGLSurfaceView spaceGLSurfaceView;
    private WindowManager mWindowManager;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

        mDisplay = mWindowManager.getDefaultDisplay();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        spaceGLSurfaceView = new SpaceGLSurfaceView(this);
        spaceGLSurfaceView.context=this;


        setContentView(spaceGLSurfaceView);



        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View ll = inflater.inflate(R.layout.surface_view_overlay, null);
        addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Button buttonControlThrust = (Button)findViewById(R.id.controlThrust);
        // Register the onClick listener with the implementation above
        buttonControlThrust.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                accelerateTouch();



                if(event.getAction() == MotionEvent.ACTION_UP){
                   accelerateStop();
                    buttonControlThrust.setPressed(!buttonControlThrust.isPressed());

                    // Do what you want
                    return true;
                }
                return false;
            }
        });



        Button buttonControlLeft = (Button)findViewById(R.id.controlLeft);
        buttonControlLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spaceGLSurfaceView.setRotationShip(false);
                if(event.getAction() == MotionEvent.ACTION_UP){
                    spaceGLSurfaceView.stopRotationShip();
                    buttonControlLeft.setPressed(!buttonControlLeft.isPressed());
                    // Do what you want
                    return true;
                }
                return false;
            }
        });

        Button buttonControlRight = (Button)findViewById(R.id.controlRight);
        buttonControlRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spaceGLSurfaceView.setRotationShip(true);
                if(event.getAction() == MotionEvent.ACTION_UP){
                    spaceGLSurfaceView.stopRotationShip();
                    buttonControlRight.setPressed(!buttonControlRight.isPressed());
                    // Do what you want
                    return true;
                }
                return false;
            }
        });/*

        Button buttonControlShot = (Button)findViewById(R.id.controlShot);
        buttonControlShot.setOnTouchListener(controlShot);*/

        Button buttonControlMenu = (Button)findViewById(R.id.controlMenu);
        buttonControlMenu.setOnClickListener(controlMenu);


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

    private View.OnClickListener controlMenu = new View.OnClickListener() {
        public void onClick(View v) {
            //Log.d("Thrust", "1");
        }
    };
    /*
    private View.OnClickListener controlThrust = new View.OnClickListener() {
        public void onClick(View v) {
            //Log.d("Thrust", "1");
        }
    };*/


    private void accelerateTouch() {
        float vecDir[] = spaceGLSurfaceView.getShipViewDirection();
        spaceGLSurfaceView.setShipVelocity(vecDir[0]*10f, 0, vecDir[2]*10f);
        /*for(float i = 1; i < 5; i += 1) {
            spaceGLSurfaceView.setShipVelocity(vecDir[0]*i, 0, vecDir[2]*i);
        }*/
    }

    private void accelerateStop() {
        //float vecDir[] = spaceGLSurfaceView.getShipViewDirection();
        //spaceGLSurfaceView.setShipVelocity(vecDir[0], 0, vecDir[2]);
        /*for(float i = 2f; i > 0; i -= 0.5f) {
            spaceGLSurfaceView.setShipVelocity(vecDir[0]*i, vecDir[1], vecDir[2]*i);
            SystemClock.sleep(120);
        }*/
        spaceGLSurfaceView.setShipVelocity(0, 0, 0);

    }

    private int getScreenHeight(Context context) {
        int height;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            height = display.getHeight();  // deprecated
        }

        return height;
    }
    private int getScreenWidth(Context context) {
        int width;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            width = display.getWidth();  // deprecated
        }

        return width;
    }

}