package de.hs_kl.gatav.flyingsaucerfull.objects;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Shot extends SpaceObject {

    private static float[] colorShot = {0.8f, 0.8f, 0.8f};
    private float[] shotArray = {
            -0.095671f, 0.182819f, 0.000000f,
            -0.176777f, 0.139924f, 0.000000f,
            -0.230970f, 0.065390f, 0.000000f,
            -0.230970f, -0.065390f, 0.000000f,
            -0.176777f, -0.139924f, 0.000000f,
            -0.095671f, -0.182819f, 0.000000f,
            -0.088388f, 0.182819f, -0.036612f,
            -0.163320f, 0.139924f, -0.067649f,
            -0.213388f, 0.065390f, -0.088388f,
            -0.213388f, -0.065390f, -0.088388f,
            -0.163320f, -0.139924f, -0.067649f,
            -0.088388f, -0.182819f, -0.036612f,
            -0.067650f, 0.182819f, -0.067649f,
            -0.125000f, 0.139924f, -0.125000f,
            -0.163320f, 0.065390f, -0.163320f,
            -0.163320f, -0.065390f, -0.163320f,
            -0.125000f, -0.139924f, -0.125000f,
            -0.067650f, -0.182819f, -0.067649f,
            -0.036612f, 0.182819f, -0.088388f,
            -0.067650f, 0.139924f, -0.163320f,
            -0.088388f, 0.065390f, -0.213388f,
            -0.088388f, -0.065390f, -0.213388f,
            -0.067650f, -0.139924f, -0.163320f,
            -0.036612f, -0.182819f, -0.088388f,
            -0.000000f, 0.182819f, -0.095671f,
            -0.000000f, 0.139924f, -0.176777f,
            -0.000000f, 0.065390f, -0.230970f,
            -0.000000f, -0.065390f, -0.230970f,
            -0.000000f, -0.139924f, -0.176777f,
            -0.000000f, -0.182819f, -0.095671f,
            0.036612f, 0.182819f, -0.088388f,
            0.067649f, 0.139924f, -0.163320f,
            0.088388f, 0.065390f, -0.213388f,
            0.088388f, -0.065390f, -0.213388f,
            0.067649f, -0.139924f, -0.163320f,
            0.036612f, -0.182819f, -0.088388f,
            -0.000000f, 0.197882f, 0.000000f,
            0.067649f, 0.182819f, -0.067649f,
            0.125000f, 0.139924f, -0.125000f,
            0.163320f, 0.065390f, -0.163320f,
            0.163320f, -0.065390f, -0.163320f,
            0.125000f, -0.139924f, -0.125000f,
            0.067649f, -0.182819f, -0.067649f,
            0.088388f, 0.182819f, -0.036612f,
            0.163320f, 0.139924f, -0.067649f,
            0.213388f, 0.065390f, -0.088388f,
            0.213388f, -0.065390f, -0.088388f,
            0.163320f, -0.139924f, -0.067649f,
            0.088388f, -0.182819f, -0.036612f,
            -0.000000f, -0.197882f, 0.000000f,
            0.095671f, 0.182819f, 0.000000f,
            0.176777f, 0.139924f, 0.000000f,
            0.230970f, 0.065390f, 0.000000f,
            0.230970f, -0.065390f, 0.000000f,
            0.176777f, -0.139924f, 0.000000f,
            0.095671f, -0.182819f, 0.000000f,
            0.088388f, 0.182819f, 0.036612f,
            0.163320f, 0.139924f, 0.067649f,
            0.213388f, 0.065390f, 0.088388f,
            0.213388f, -0.065390f, 0.088388f,
            0.163320f, -0.139924f, 0.067649f,
            0.088388f, -0.182819f, 0.036612f,
            0.067649f, 0.182819f, 0.067649f,
            0.125000f, 0.139924f, 0.125000f,
            0.163320f, 0.065390f, 0.163320f,
            0.163320f, -0.065390f, 0.163320f,
            0.125000f, -0.139924f, 0.125000f,
            0.067649f, -0.182819f, 0.067649f,
            0.036612f, 0.182819f, 0.088388f,
            0.067649f, 0.139924f, 0.163320f,
            0.088388f, 0.065390f, 0.213388f,
            0.088388f, -0.065390f, 0.213388f,
            0.067649f, -0.139924f, 0.163320f,
            0.036612f, -0.182819f, 0.088388f,
            -0.000000f, 0.182819f, 0.095671f,
            -0.000000f, 0.139924f, 0.176777f,
            -0.000000f, 0.065390f, 0.230970f,
            -0.000000f, -0.065390f, 0.230970f,
            -0.000000f, -0.139924f, 0.176777f,
            -0.000000f, -0.182819f, 0.095671f,
            -0.036612f, 0.182819f, 0.088388f,
            -0.067650f, 0.139924f, 0.163320f,
            -0.088388f, 0.065390f, 0.213388f,
            -0.088388f, -0.065390f, 0.213388f,
            -0.067650f, -0.139924f, 0.163320f,
            -0.036612f, -0.182819f, 0.088388f,
            -0.067650f, 0.182819f, 0.067649f,
            -0.125000f, 0.139924f, 0.125000f,
            -0.163320f, 0.065390f, 0.163320f,
            -0.163320f, -0.065390f, 0.163320f,
            -0.125000f, -0.139924f, 0.125000f,
            -0.067650f, -0.182819f, 0.067649f,
            -0.088388f, 0.182819f, 0.036612f,
            -0.163320f, 0.139924f, 0.067649f,
            -0.213388f, 0.065390f, 0.088388f,
            -0.213388f, -0.065390f, 0.088388f,
            -0.163320f, -0.139924f, 0.067649f,
            -0.088388f, -0.182819f, 0.036612f
    };
    boolean buffersInitialized = false;

    private static FloatBuffer shotVerticesBuffer;


    public Shot() {
        scale = 0.5f;


        if (!buffersInitialized) {
            // Initialize buffers


            ByteBuffer shotCircleBB = ByteBuffer.allocateDirect(shotArray.length * 4);
            shotCircleBB.order(ByteOrder.nativeOrder());
            shotVerticesBuffer = shotCircleBB.asFloatBuffer();
            shotVerticesBuffer.put(shotArray);
            shotVerticesBuffer.position(0);

            buffersInitialized = true;
        }
    }

    @Override
    public void draw(GL10 gl) {

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        Log.d("spawnNewShot", "1");

        gl.glPushMatrix();
        Log.d("spawnNewShot", "2");
        {
            gl.glMultMatrixf(transformationMatrix, 0);
            Log.d("spawnNewShot", "3");

            gl.glScalef(scale, scale, scale);
            Log.d("spawnNewShot", "4");

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            Log.d("spawnNewShot", "5");

            gl.glLineWidth(2.0f);
            Log.d("spawnNewShot", "6");

            // draw ring
            gl.glPushMatrix();
            Log.d("spawnNewShot", "7");
            {

                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, shotVerticesBuffer);
                Log.d("spawnNewShot", "8");
            }
            gl.glPopMatrix();
            Log.d("spawnNewShot", "9");
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            Log.d("spawnNewShot", "10");
        }
        gl.glPopMatrix();
        Log.d("spawnNewShot", "11");
    }

    @Override
    public void update(float fracSec) {
        updatePosition(fracSec);
    }

    private float[] createShot() {
        float[] vertices =
        {
            -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
                    0.0f,  0.5f, 0.0f
        };
        return vertices;
    }
}
