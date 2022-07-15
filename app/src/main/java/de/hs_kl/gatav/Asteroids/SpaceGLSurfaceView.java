package de.hs_kl.gatav.Asteroids;


import static de.hs_kl.gatav.Asteroids.util.Utilities.normalize;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.hs_kl.gatav.Asteroids.objects.Asteroid;
import de.hs_kl.gatav.Asteroids.objects.BorgCube;
import de.hs_kl.gatav.Asteroids.objects.Obstacle;
import de.hs_kl.gatav.Asteroids.objects.Shot;
import de.hs_kl.gatav.Asteroids.objects.SpaceObject;
import de.hs_kl.gatav.Asteroids.objects.SpaceShip;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SpaceGLSurfaceView extends GLSurfaceView {
    private static float health = 3f;
    private static int highscoreEnd = 0;
    private SpaceRenderer renderer;
    public Context context;

    public float boundaryTop, boundaryBottom, boundaryLeft, boundaryRight;

    private static final int obstacleCount = 8;
    private static final float minSpawnDistanceToPlayer = 1.5f;
    private static final float minSpawnDistanceBetweenObstacles = 1.5f;
    private static final float asteroidMinScale = 0.5f;
    private static final float asteroidMaxScale = 1.0f;
    public int highscore = 0;
    public TextView highscoreText;
    private GL10 glm;
    private ImageView[] lifeIcons;
    private ConstraintLayout gamePlayOverlay;
    private ConstraintLayout gameOverOverlay;
    private TextView gameOverText;
    public MediaPlayer mpg = null;



    private float[] dirVec = {0f, 0f, 1f};

    private long lastShot = 0;


    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private SpaceShip ship = new SpaceShip(getResourceShipModel());
    private ArrayList<Shot> shotArray = new ArrayList<Shot>();
    private ArrayList<Obstacle> obstaclesToBeRemoved = new ArrayList<Obstacle>();

    public SpaceGLSurfaceView(Context context) throws IOException {
        super(context);
        renderer = new SpaceRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mpg = new MediaPlayer();
        mpg.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }


    @UiThread
    public void death() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (health == 0f) {
                    Log.d("UI thread", "I am the UI thread");
                    setGameOverlay();
                }
                if (health == 3f && gameOverOverlay.getVisibility() == View.VISIBLE) {
                    gamePlayOverlay.setVisibility(View.VISIBLE);
                    gameOverOverlay.setVisibility(View.INVISIBLE);
                    lifeIcons[0].setVisibility(View.VISIBLE);
                    lifeIcons[1].setVisibility(View.VISIBLE);
                    lifeIcons[2].setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void playSound(String sound) {

        MediaPlayer mpg = new MediaPlayer();

        if (mpg.isPlaying()) mpg.stop();
        try {
            try {
                AssetFileDescriptor afd = context.getApplicationContext().getAssets().openFd("sfx/" + sound);
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


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setGameOverlay() {
        if (gamePlayOverlay.getVisibility() == View.VISIBLE) {
            gamePlayOverlay.setVisibility(View.INVISIBLE);
            gameOverOverlay.setVisibility(View.VISIBLE);
            gameOverText.setText("Game Over! Dein Highscore beträgt: " + getHighscoreEnd());
        }
    }

    public static int getHighscoreEnd() {
        return highscoreEnd;
    }


    private InputStream getResourceShipModel() {
        AssetManager am = getContext().getAssets();
        try {
            return am.open("rd1.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getResourceShotModel() {
        AssetManager am = getContext().getAssets();
        try {
            return am.open("shot.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getResourceAsteroidModel() {
        AssetManager am = getContext().getAssets();
        try {
            return am.open("asteroid.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateScore(TextView highscoreText) {
        this.highscoreText = highscoreText;
    }

    public void setShipVelocity(float vx, float vy, float vz) {
        ship.setVelocity(vx, vy, vz);
    }

    public void restart() {
        ship.resetHealth();
        ship.setPosition(0, 0, 0);
        health = 3f;
        highscore = 0;
        highscoreEnd = 0;
    }

    public void spawnObstacles() {
        if (obstacleCount / 2 > obstacles.size()) {
            for (int i = 0; i < obstacleCount / 2 - obstacles.size(); ++i) {

                float scale = 1.0f;
                scale = (float) Math.random() * (asteroidMaxScale - asteroidMinScale) + asteroidMinScale;

                float spawnX = 0.0f;
                float spawnZ = 0.0f;
                float spawnOffset = scale * 0.5f;
                float velocity[] = new float[3];

                // determine source and destination quadrant
                int sourceCode = ((Math.random() < 0.5 ? 0 : 1) << 1) | (Math.random() < 0.5 ? 0 : 1);  // source quadrant
                int destCode = sourceCode ^ 3;    // destination quadrant is opposite of source
                //Log.d("Code", sourceCode+" "+destCode);

                /* sourceCode, destCode
                 * +----+----+
                 * | 00 | 01 |
                 * +----+----+
                 * | 10 | 11 |
                 * +----+----+
                 */

                // calculate source vertex position, <0.5 horizontal, else vertical
                if (Math.random() < 0.5) {  // horizontal placing, top or bottom
                    spawnZ = (sourceCode & 2) > 0 ? boundaryBottom - spawnOffset : boundaryTop + spawnOffset;
                    spawnX = (sourceCode & 1) > 0 ? boundaryRight * (float) Math.random() : boundaryLeft * (float) Math.random();
                } else {  // vertical placing, left or right
                    spawnZ = (sourceCode & 2) > 0 ? boundaryBottom * (float) Math.random() : boundaryTop * (float) Math.random();
                    spawnX = (sourceCode & 1) > 0 ? boundaryRight + spawnOffset : boundaryLeft - spawnOffset;
                }

                // calculate destination vertex position, <0.5 horizontal, else vertical
                if (Math.random() < 0.5) {  // horizontal placing, top or bottom
                    velocity[2] = (destCode & 2) > 0 ? boundaryBottom - spawnOffset : boundaryTop + spawnOffset;
                    velocity[0] = (destCode & 1) > 0 ? boundaryRight * (float) Math.random() : boundaryLeft * (float) Math.random();
                } else {  // vertical placing, left or right
                    velocity[2] = (destCode & 2) > 0 ? boundaryBottom * (float) Math.random() : boundaryTop * (float) Math.random();
                    velocity[0] = (destCode & 1) > 0 ? boundaryRight + spawnOffset : boundaryLeft - spawnOffset;
                }

                // calculate velocity
                velocity[0] -= spawnX;
                velocity[2] -= spawnZ;
                normalize(velocity);


                boolean positionOk = true;

                // check distance to other obstacles
                for (Obstacle obstacle : obstacles) {
                    float minDistance = 0.5f * scale + 0.5f * obstacle.scale + minSpawnDistanceBetweenObstacles;
                    if (Math.abs(spawnX - obstacle.getX()) < minDistance
                            && Math.abs(spawnZ - obstacle.getZ()) < minDistance)
                        positionOk = false;    // Distance too small -> invalid position
                }

                // check distance to player
                float minPlayerDistance = 0.5f * scale + 0.5f * ship.scale + minSpawnDistanceToPlayer;
                if (Math.abs(spawnX - ship.getX()) < minPlayerDistance &&
                        Math.abs(spawnZ - ship.getZ()) < minPlayerDistance)
                    positionOk = false;    // Distance to player too small -> invalid position

                if (!positionOk)
                    continue; // Invalid spawn position -> try again next time

                Asteroid newAsteroid;
                if(Asteroid.modelLoaded) {
                    newAsteroid = new Asteroid();
                } else {
                    newAsteroid = new Asteroid(getResourceAsteroidModel());
                }

                newAsteroid.scale = scale;
                newAsteroid.randomizeRotationAxis();
                newAsteroid.angularVelocity = 50;
                newAsteroid.setPosition(spawnX, 0, spawnZ);
                newAsteroid.velocity = velocity;
                obstacles.add(newAsteroid);

            }
        }
    }

    public void spawnObstacles(Obstacle obstacleDestroyed) {
        playSound("crack1.wav");
        if (obstacleCount > obstacles.size() && obstacleDestroyed.scale > 0.25f) {
            for (int i = -1; i < 2; i += 2) {


                float scale = obstacleDestroyed.scale / 2;
                float spawnOffset = scale * 0.5f * i;
                float spawnX = obstacleDestroyed.getX() + spawnOffset;
                float spawnZ = obstacleDestroyed.getZ() + spawnOffset;

                float velocity[] = new float[3];


                // calculate velocity
                velocity[0] -= spawnX * i;
                velocity[2] -= spawnZ * i;
                normalize(velocity);


                Asteroid newAsteroid;
                if(Asteroid.modelLoaded) {
                    newAsteroid = new Asteroid();
                } else {
                    newAsteroid = new Asteroid(getResourceAsteroidModel());
                }
                newAsteroid.scale = scale;
                newAsteroid.randomizeRotationAxis();
                newAsteroid.angularVelocity = 50 * i;
                newAsteroid.setPosition(spawnX, 0, spawnZ);
                newAsteroid.velocity = velocity;
                obstaclesToBeRemoved.add(obstacleDestroyed);
                obstacles.add(newAsteroid);
            }
        } else {
            obstaclesToBeRemoved.add(obstacleDestroyed);
        }
    }

    public void spawnNewShot() {
        if (System.currentTimeMillis() - lastShot > 150) {
            float[] shotDir = getShipViewDirection();
            float scale = 0.2f;

            float spawnX = ship.getX();
            float spawnZ = ship.getZ();
            float spawnOffset = scale * 0.5f;
            float velocity[] = new float[3];

            velocity[0] = spawnX + spawnOffset;
            velocity[2] = spawnZ + spawnOffset;
            normalize(velocity);

            Shot newShot;
            if(!Shot.modelLoaded) {
               newShot = new Shot(getResourceShotModel());
            } else {
                newShot = new Shot();
            }

            newShot.scale = scale;
            newShot.velocity = velocity;
            newShot.setPosition(spawnX, 0, spawnZ);
            shotArray.add(newShot);

            newShot.setVelocity(shotDir[0] * 20f, shotDir[1], shotDir[2] * 20f);

            Log.d("Shot array: ", "" + shotArray.isEmpty());
            lastShot = System.currentTimeMillis();
        }

    }

    public void setLifeIcons(ImageView[] lifeIcons) {
        this.lifeIcons = lifeIcons;
    }

    public void setGamePlayOverlay(ConstraintLayout gamePlayOverlay) {
        this.gamePlayOverlay = gamePlayOverlay;
    }

    public void setGameOverOverlay(ConstraintLayout gameOverOverlay) {
        this.gameOverOverlay = gameOverOverlay;
    }

    public void setGameOverText(TextView gameOverText) {
        this.gameOverText = gameOverText;
    }


    private class SpaceRenderer implements Renderer {
        private float[] modelViewScene = new float[16];


        long lastFrameTime;

        public SpaceRenderer() {
            lastFrameTime = System.currentTimeMillis();
        }


        @Override
        public void onDrawFrame(GL10 gl) {

            // update time calculation
            long delta = System.currentTimeMillis() - lastFrameTime;
            float fracSec = (float) delta / 1000;
            lastFrameTime = System.currentTimeMillis();


            // scene updates
            updateShip(fracSec);
            updateObstacles(fracSec);
            updateShot(fracSec);
            updateHighscore();
            updateLife();


            // clear screen and depth buffer
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            GL11 gl11 = (GL11) gl;
            glm = gl;

            // load local system to draw scene items
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl11.glLoadMatrixf(modelViewScene, 0);

            ship.draw(gl);
            ArrayList<Obstacle> obstacleCopies = new ArrayList<>();
            for (Obstacle obstacle : obstacles) {
                obstacleCopies.add(obstacle);
            }
            for (Obstacle obstacle : obstacleCopies) {
                obstacle.draw(gl);
            }
            obstacleCopies.clear();

            ArrayList<Shot> shotCopies = new ArrayList<>();
            for (Shot shot : shotArray) {
                shotCopies.add(shot);
            }
            for (Shot shot : shotCopies) {
                shot.draw(gl);
            }
            shotCopies.clear();

        }

        private void updateLife() {
            if (ship.getHealth() == 2f) {
                lifeIcons[0].setVisibility(View.INVISIBLE);
                health = 2f;
            } else if (ship.getHealth() == 1f) {
                lifeIcons[1].setVisibility(View.INVISIBLE);
                health = 1f;
            } else if (ship.getHealth() == 0f) {
                lifeIcons[2].setVisibility(View.INVISIBLE);
                health = 0f;
                death();
            }
        }

        private void updateHighscore() {
            if (highscoreText != null) highscoreText.setText("Highscore: " + highscore);
        }

        private void updateShot(float fracSec) {
            ArrayList<Shot> shotsToBeRemoved = new ArrayList<Shot>();


            // position update on all shots
            for (Shot shot : shotArray) {
                shot.update(fracSec);
            }

            // check for shots that flew out of the viewing area and remove
            // or deactivate them
            for (Shot shot : shotArray) {
                // offset makes sure that the obstacles don't get deleted or set
                // inactive while visible to the player.
                float offset = shot.scale;

                if ((shot.getX() > boundaryRight + offset)
                        || (shot.getX() < boundaryLeft - offset)
                        || (shot.getZ() > boundaryTop + offset)
                        || (shot.getZ() < boundaryBottom - offset)) {

                    shotsToBeRemoved.add(shot);
                }
            }
            // remove obsolete obstacles
            for (Shot shot : shotsToBeRemoved) {
                shotArray.remove(shot);
            }
            shotsToBeRemoved.clear();


            // shot collision with asteroid
            ArrayList<Obstacle> obstaclesCopy = new ArrayList<>();
            for (Obstacle obstacle : obstacles) {
                obstaclesCopy.add(obstacle);
            }
            ArrayList<Shot> shotArrayCopy = new ArrayList<>();
            for (Shot shot : shotArray) {
                shotArrayCopy.add(shot);
            }
            for (Shot shot : shotArrayCopy) {
                for (Obstacle obstacle : obstaclesCopy) {
                    if (areColliding(shot, obstacle)) {

                        Log.d("Attach", "Hit");
                        if (obstacle instanceof Asteroid) {
                            highscore += 10;
                            highscoreEnd += 10;
                        }
                        shotsToBeRemoved.add(shot);
                        spawnObstacles(obstacle);
                        //ODOT
                    }
                }

            }
            obstaclesCopy.clear();
            for (Shot shot : shotsToBeRemoved) {
                shotArray.remove(shot);
            }
            shotsToBeRemoved.clear();
        }


        private void updateShip(float fracSec) {
            ship.update(fracSec);
            // keep ship within window boundaries
            if (ship.getX() < boundaryLeft + ship.scale / 2)
                ship.setX(boundaryRight - ship.scale / 2);
            if (ship.getX() > boundaryRight - ship.scale / 2)
                ship.setX(boundaryLeft + ship.scale / 2);
            if (ship.getZ() < boundaryBottom + ship.scale / 2)
                ship.setZ(boundaryTop - ship.scale / 2);
            if (ship.getZ() > boundaryTop - ship.scale / 2)
                ship.setZ(boundaryBottom + ship.scale / 2);
        }


        private boolean areColliding(SpaceObject obj1, SpaceObject obj2) {
            float obj1X = obj1.getX();
            float obj1Z = obj1.getZ();
            float obj2X = obj2.getX();
            float obj2Z = obj2.getZ();
            float squaredHitDistance = ((obj1.scale + obj2.scale) / 2) * ((obj1.scale + obj2.scale) / 2);
            float squaredDistance = (obj1X - obj2X) * (obj1X - obj2X) + (obj1Z - obj2Z) * (obj1Z - obj2Z);


            if (squaredDistance < squaredHitDistance) {
                Log.d("Hit", "hit");
                return true;
            }
            return false;
        }


        private void updateObstacles(float fracSec) {


            // position update on all obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.update(fracSec);
            }


            // check for obstacles that flew out of the viewing area and remove
            // or deactivate them
            for (Obstacle obstacle : obstacles) {
                // offset makes sure that the obstacles don't get deleted or set
                // inactive while visible to the player.
                float offset = obstacle.scale;

                if ((obstacle.getX() > boundaryRight + offset)
                        || (obstacle.getX() < boundaryLeft - offset)
                        || (obstacle.getZ() > boundaryTop + offset)
                        || (obstacle.getZ() < boundaryBottom - offset)) {
                    obstaclesToBeRemoved.add(obstacle);
                }
            }
            // remove obsolete obstacles
            for (Obstacle obstacle : obstaclesToBeRemoved) {
                obstacles.remove(obstacle);
            }
            obstaclesToBeRemoved.clear();


            // obstacle collision with space ship
            for (Obstacle obstacle : obstacles) {
                if (areColliding(ship, obstacle)) {
                    ship.damage(1f);
                    playSound("hit1.wav");// add some damage to the ship
                    obstaclesToBeRemoved.add(obstacle);
                    //ODOT
                }
            }
            // remove obsolete obstacles
            for (Obstacle obstacle : obstaclesToBeRemoved) {
                obstacles.remove(obstacle);
            }
            obstaclesToBeRemoved.clear();


            // obstacle collision handling with each other
            for (int i = 0; i <= obstacles.size() - 2; i++) {
                Obstacle obstacle = obstacles.get(i);
                float ax = obstacle.getX();
                float az = obstacle.getZ();

                // check for collision with other obstacle
                for (int j = i + 1; j <= obstacles.size() - 1; j++) {
                    Obstacle otherObstacle = obstacles.get(j);
                    float oax = otherObstacle.getX();
                    float oaz = otherObstacle.getZ();

                    if (areColliding(obstacle, otherObstacle)) {
                        // collisions: let them bounce off each other
                        // http://de.wikipedia.org/wiki/ -> ElastischerStoß

                        float cv1[] = obstacle.velocity;
                        float cv2[] = otherObstacle.velocity;
                        float csv1 = cv1[2] / cv1[0]; // slope of velocity 1
                        float csv2 = cv2[2] / cv2[0]; // slope of velocity 2

                        float csz = (oaz - az) / (oax - ax); // central slope between centers
                        float cst = -1.0f / csz; // tangent slope perpendicular to central line

                        // calculate vt for both velocities
                        float cvt1[] = new float[3];
                        float cvt2[] = new float[3];
                        cvt1[0] = cv1[0] * (csz - csv1) / (csz - cst);
                        cvt1[2] = cvt1[0] * cst;

                        cvt2[0] = cv2[0] * (csz - csv2) / (csz - cst);
                        cvt2[2] = cvt2[0] * cst;

                        // calculate vz for both velocities
                        float cvz1[] = new float[3];
                        float cvz2[] = new float[3];
                        cvz1[0] = cv1[0] * (cst - csv1) / (cst - csz);
                        cvz1[2] = cvz1[0] * csz;

                        cvz2[0] = cv2[0] * (cst - csv2) / (cst - csz);
                        cvz2[2] = cvz2[0] * csz;

                        // asteroid-asteroid or borg-borg
                        // => inclined central elastic collision with identical masses
                        if (obstacle.getClass() == otherObstacle.getClass()) {
                            cv1[0] = cvt1[0] + cvz2[0];
                            cv1[2] = cvt1[2] + cvz2[2];

                            cv2[0] = cvt2[0] + cvz1[0];
                            cv2[2] = cvt2[2] + cvz1[2];
                        } else {  // different scenarios

                            // one obstacle is borg cube, one is asteroid
                            // let the borg cube push the little asteroid out of the way - borg is much stronger than asteroid
                            // => inclined central elastic collision with one superior mass
                            if (otherObstacle instanceof BorgCube && obstacle instanceof Asteroid) {
                                cv1[0] = cvt1[0] - cvz1[0] + cvz2[0] * 2.0f;
                                cv1[2] = cvt1[2] - cvz1[2] + cvz2[2] * 2.0f;
                            } else if (obstacle instanceof BorgCube && otherObstacle instanceof Asteroid) {
                                cv2[0] = cvt2[0] - cvz2[0] + cvz1[0] * 2.0f;
                                cv2[2] = cvt2[2] - cvz2[2] + cvz1[2] * 2.0f;
                            }

                            //ODOT
                        }

                        if (obstacle instanceof Asteroid)
                            ((Asteroid) obstacle).angularVelocity *= -1.0f;
                        if (otherObstacle instanceof Asteroid)
                            ((Asteroid) otherObstacle).angularVelocity *= -1.0f;
                    }
                }
            }
            // remove obsolete obstacles
            for (Obstacle obstacle : obstaclesToBeRemoved) {
                obstacles.remove(obstacle);
            }
            obstaclesToBeRemoved.clear();


            // Spawn new borg or asteroid obstacles to match the target obstacle count
            spawnObstacles();

        }


        @Override
        // Called when surface is created or the viewport gets resized
        // set projection matrix
        // precalculate modelview matrix
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GL11 gl11 = (GL11) gl;
            gl.glViewport(0, 0, width, height);

            float aspectRatio = (float) width / height;
            float fovy = 45.0f;

            // set up projection matrix for scene
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, fovy, aspectRatio, 0.001f, 100.0f);

            // set up modelview matrix for scene
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            float desired_height = 10.0f;
            // We want to be able to see the range of 5 to -5 units at the y
            // axis (height=10).
            // To achieve this we have to pull the camera towards the positive z axis
            // based on the following formula:
            // z = (desired_height / 2) / tan(fovy/2)
            float z = (float) (desired_height / 2 / Math.tan(fovy / 2 * (Math.PI / 180.0f)));
            // forward for the camera is backward for the scene
            gl.glTranslatef(0.0f, 0.0f, -z);
            // rotate local to achive top down view from negative y down to xz-plane
            // z range is the desired height
            gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            // save local system as a basis to draw scene items
            gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewScene, 0);

            // window boundaries
            // z range is the desired height
            boundaryTop = desired_height / 1.5f;
            boundaryBottom = -desired_height / 1.5f;
            // x range is the desired width
            boundaryLeft = -(desired_height / 1.7f * aspectRatio);
            boundaryRight = (desired_height / 1.7f * aspectRatio);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);


            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_FLAT);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

    }

    public void setRotationShip(boolean dir) {
        ship.setRotation(dir);
        getShipViewDirection();

    }

    public void stopRotationShip() {
        ship.stopRotation();
        getShipViewDirection();
    }

    public float[] getShipViewDirection() {
        float shipRotation = ship.getRotationShip();
        if (shipRotation < 0f) shipRotation = 360f + shipRotation;
        if (shipRotation > 359f) {
            shipRotation = 1;
        }

        if (shipRotation >= 0f && shipRotation <= 90f) {
            dirVec[2] = (float) (shipRotation / 90);
            dirVec[1] = 0f;
            dirVec[0] = (float) ((90 - shipRotation) / 90) * -1;
        } else if (shipRotation > 90f && shipRotation <= 180f) {

            dirVec[0] = (float) ((shipRotation - 90) / 90);
            dirVec[1] = 0f;
            dirVec[2] = (float) ((90 - (shipRotation - 90)) / 90);
        } else if (shipRotation > 180f && shipRotation <= 270f) {
            dirVec[2] = (float) ((shipRotation - 180) / 90) * -1;
            dirVec[1] = 0f;
            dirVec[0] = (float) ((90 - (shipRotation - 180)) / 90);
        } else if (shipRotation > 270f && shipRotation <= 359.9f) {
            dirVec[0] = (float) ((shipRotation - 270) / 90) * -1;
            dirVec[1] = 0f;
            dirVec[2] = (float) ((90 - (shipRotation - 270)) / 90) * -1;
        }

        normalize(dirVec);
        return dirVec;
    }


}

