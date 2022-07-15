package de.hs_kl.gatav.Asteroids;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;

import de.hs_kl.gatav.flyingsaucerfull.R;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    private SpaceGLSurfaceView spaceGLSurfaceView;
    private WindowManager mWindowManager;
    private Display mDisplay;
    ConstraintLayout gamePlayOverlay;
    ConstraintLayout gameOverOverlay;
    TextView gameOverText;
    private boolean menu = true;
    private boolean lastShot = false;

    MediaPlayer mpg = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeScreen() throws IOException {
        Log.d("Menu: ", menu + "");

        if (!menu) {

            setContentView(R.layout.menu_start);
            mpg = new MediaPlayer();
            mpg.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );


            Button play = (Button) findViewById(R.id.play);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playSound("menuclick.wav", false);
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

            mpg = new MediaPlayer();
            mpg.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View ll = inflater.inflate(R.layout.surface_view_overlay, null);
            addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView highscore = (TextView) findViewById(R.id.highscore);
            ImageView[] lifeIcons = new ImageView[3];
            lifeIcons[2] = (ImageView) findViewById(R.id.health1);
            lifeIcons[1] = (ImageView) findViewById(R.id.health2);
            lifeIcons[0] = (ImageView) findViewById(R.id.health3);
            gamePlayOverlay = (ConstraintLayout) findViewById(R.id.gamePlayOverlay);
            gameOverOverlay = (ConstraintLayout) findViewById(R.id.gameOverBox);
            gameOverText = (TextView) findViewById(R.id.gameOverText);
            spaceGLSurfaceView.setLifeIcons(lifeIcons);
            spaceGLSurfaceView.setGamePlayOverlay(gamePlayOverlay);
            spaceGLSurfaceView.setGameOverOverlay(gameOverOverlay);
            spaceGLSurfaceView.setGameOverText(gameOverText);

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

            Button buttonControlShot = (Button) findViewById(R.id.controlShot);
            buttonControlShot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastShot) {
                        playSound("Pew2.wav", false);
                        lastShot = !lastShot;
                    } else {
                        playSound("Pew1.wav", false);
                        lastShot = !lastShot;
                    }

                    spaceGLSurfaceView.spawnNewShot();
                    spaceGLSurfaceView.updateScore(highscore);
                }

            });

            Button buttonControlMenu = (Button) findViewById(R.id.controlMenu);
            buttonControlMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playSound("menuclick.wav", false);
                        changeScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            Button buttonGameOverPlay = (Button) findViewById(R.id.gameOverPlayButton);
            buttonGameOverPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playSound("menuclick.wav", false);
                    restartGame();
                }

            });

            Button buttonGameOverMenu = (Button) findViewById(R.id.gameOverMenuButton);
            buttonGameOverMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playSound("menuclick.wav", false);
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
        getSupportActionBar().hide();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mDisplay = mWindowManager.getDefaultDisplay();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.menu_start);
        mpg = new MediaPlayer();

        mpg.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    playSound("menuclick.wav", false);
                    changeScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public MediaPlayer playSound(String sound, boolean loop) {

        MediaPlayer mpg = new MediaPlayer();

        if (mpg.isPlaying()) mpg.stop();
        try {
            try {
                AssetFileDescriptor afd = getApplicationContext().getAssets().openFd("sfx/" + sound);
                mpg.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mpg.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } catch (NullPointerException e) {
                Log.d("Nullpointer", "true");
            }

            mpg.prepare();
            mpg.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mp.reset();
                    mp.release();
                    mp = null;
                }
            });
            mpg.start();
            mpg.setLooping(loop);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return mpg;
    }

    public void stopSound(MediaPlayer mpg) {
        if (mpg.isPlaying()) mpg.stop();
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accelerateTouch() {
        //thrustSound = playSound("fly1.wav", false);

        float vecDir[] = spaceGLSurfaceView.getShipViewDirection();

        for (float i = 1; i < 5; i += 0.5f) {
            spaceGLSurfaceView.setShipVelocity(vecDir[0] * 10f, 0, vecDir[2] * 10f);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accelerateStop() {
        //stopSound(thrustSound);
        spaceGLSurfaceView.setShipVelocity(0, 0, 0);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void restartGame() {
        spaceGLSurfaceView.restart();
        spaceGLSurfaceView.death();
    }


}