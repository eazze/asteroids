package de.hs_kl.gatav.Asteroids.objects;

import android.util.Log;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.Asteroids.util.OBJParser;

public class Shot extends SpaceObject {

    private static final float[] colorShot = {0.8f, 0.8f, 0.8f};

    boolean buffersInitialized = false;

    private static float[] colorBody = {0.8f, 0.8f, 0.8f};
    private float rotation = 90f;
    private float ringRotation = 0.0f;
    static OBJParser.VertArray modelShot;
    public static boolean modelLoaded = false;

    public Shot() {

        scale = 0.03f;
        modelLoaded = true;

    }

    public Shot(InputStream resourceShotModel) {

        scale = 0.03f;
        modelShot = model(resourceShotModel);
        modelLoaded = true;

    }

    public OBJParser.VertArray model(InputStream is) {
        if (is != null && !modelLoaded) {
            InputStream targetStream = is;
            return OBJParser.loadModelVertices(targetStream);
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

            gl.glLineWidth(4.0f);

            // draw model
            gl.glPushMatrix();
            {
                gl.glRotatef(rotation, 0, 1, 0);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, modelShot.getVertices());

                gl.glColor4f(colorBody[0], colorBody[1], colorBody[2], 0);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, modelShot.getNumVertices() / 3);

                gl.glPopMatrix();


                gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            }
            gl.glPopMatrix();
        }
    }

    @Override
    public void update(float fracSec) {
        updatePosition(fracSec);
    }

}
