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

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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



        /*Button buttonControlLeft = (Button)findViewById(R.id.controlLeft);
        buttonControlLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spaceGLSurfaceView.setShipVelocity(1, 0, 1);
                if(event.getAction() == MotionEvent.ACTION_UP){
                    spaceGLSurfaceView.setShipVelocity(0, 0, 0);
                    // Do what you want
                    return true;
                }
                return false;
            }
        });

        Button buttonControlRight = (Button)findViewById(R.id.controlRight);
        buttonControlRight.setOnTouchListener(controlRight);

        Button buttonControlShot = (Button)findViewById(R.id.controlShot);
        buttonControlShot.setOnTouchListener(controlShot);

        Button buttonControlMenu = (Button)findViewById(R.id.controlMenu);
        buttonControlMenu.setOnTouchListener(controlMenu);*/


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

   /* private View.OnTouchListener controlMenu = new View.OnTouchListener() {
        public void onClick(View v) {
            Log.d("Thrust", "1");
        }
    };

    private View.OnTouchListener controlThrust = new View.OnTouchListener() {
        public void onClick(View v) {
            spaceGLSurfaceView.setShipVelocity(1, 0, 1);
        }
    };

    private View.OnTouchListener controlShot = new View.OnTouchListener() {
        public void onClick(View v) {
            Log.d("Thrust", "1");
        }
    };

    private View.OnTouchListener controlLeft = new View.OnTouchListener() {
        public void onClick(View v) {
            Log.d("Left", "1");
        }
    };

    private View.OnTouchListener controlRight = new View.OnTouchListener() {
        public void onClick(View v) {
            Log.d("Thrust", "1");
        }
    };*/


/*
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        switch(mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                //Log.d("rot", ""+0);
                spaceGLSurfaceView.setShipVelocity(-event.values[0], 0, -event.values[1]);
                break;
            case Surface.ROTATION_90:
                //Log.d("rot", ""+90);
                spaceGLSurfaceView.setShipVelocity(event.values[1], 0, -event.values[0]);
                break;
            case Surface.ROTATION_180:
                //Log.d("rot", ""+180);
                spaceGLSurfaceView.setShipVelocity(event.values[0], 0, event.values[1]);
                break;
            case Surface.ROTATION_270:
                //Log.d("rot", ""+270);
                spaceGLSurfaceView.setShipVelocity(-event.values[1], 0, event.values[0]);
                break;
        }
    }*/

    private void accelerateTouch() {
        for(float i = 0; i < 2; i += 0.2f) {
            spaceGLSurfaceView.setShipVelocity(i, 0, i);
        }

    }

    private void accelerateStop() {
        for(float i = 2; i > 0; i -= 0.5f) {
            spaceGLSurfaceView.setShipVelocity(i, 0, i);
            SystemClock.sleep(120);
        }
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