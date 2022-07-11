package de.hs_kl.gatav.flyingsaucerfull.objects;


import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static de.hs_kl.gatav.flyingsaucerfull.util.Utilities.*;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.flyingsaucerfull.util.MeshObjectLoader;

public class Asteroid extends Obstacle {
    // current rotation
    public float rotation = 0.0f;
    // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float rotationAxis[] = {0.0f, 1.0f, 0.0f};

    private static float colorA[] = {0.46f, 0.22f, 0.0f};
    private static float colorB[] = {0.36f, 0.25f, 0.14f};

    private float currentColor[] = new float[3];
    MeshObjectLoader.MeshArrays modelAsteroid;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Asteroid(InputStream resourceShipModel) {
        randomizeRotationAxis();
        randomizeColor();

        scale = 0.05f;

        model(resourceShipModel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MeshObjectLoader.MeshArrays model(InputStream is) {
        if (is != null) {
            InputStream targetStream = is;
            modelAsteroid = MeshObjectLoader.loadModelMeshFromStream(targetStream);
            return modelAsteroid;
        } else {
            Log.d("E: ", "File not Found");
            return null;
        }

    }

    @Override
    public void draw(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        {
            gl.glMultMatrixf(transformationMatrix, 0);
            gl.glScalef(scale, scale, scale);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glLineWidth(1.0f);

            gl.glRotatef(rotation, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, modelAsteroid.getVertices());
            gl.glColor4f(currentColor[0], currentColor[1], currentColor[2], 0);
            gl.glDrawArrays(GL10.GL_LINES, 0, modelAsteroid.getNumVertices() / 2);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        gl.glPopMatrix();
    }

    @Override
    public void update(float fracSec) {
        updatePosition(fracSec);
        rotation += fracSec * angularVelocity;
    }

    public void randomizeRotationAxis() {
        rotationAxis[0] = (float) Math.random();
        rotationAxis[1] = (float) Math.random();
        rotationAxis[2] = (float) Math.random();
        normalize(rotationAxis);
    }

    public void randomizeColor() {
        // shades of brown
        float factor = (float) Math.random();
        currentColor[0] = factor * colorA[0] + (1.0f - factor) * colorB[0];
        currentColor[1] = factor * colorA[1] + (1.0f - factor) * colorB[1];
        currentColor[2] = factor * colorA[2] + (1.0f - factor) * colorB[2];
    }

}

