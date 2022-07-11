package de.hs_kl.gatav.flyingsaucerfull.objects;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Space;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.microedition.khronos.opengles.GL10;

import de.hs_kl.gatav.flyingsaucerfull.MainActivity;
import de.hs_kl.gatav.flyingsaucerfull.R;
import de.hs_kl.gatav.flyingsaucerfull.SpaceGLSurfaceView;
import de.hs_kl.gatav.flyingsaucerfull.util.MeshObjectLoader;

public class SpaceShip extends SpaceObject {

    //@formatter:off


    private static float FULL_HEALTH=3.0f;
    private float health = FULL_HEALTH;
    public MeshObjectLoader.MeshArrays modelShip;
    //@formatter:on
    private float rotation = 90f;
    private boolean dir;
    private float rotateSpeed = 10f;
    private boolean rotateShip = false;
    private float angularVelocity = 10.0f;
    private float deathRotationVelocity = 1.0f;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SpaceShip(InputStream resourceShipModel) {
        scale = 0.05f;

        model(resourceShipModel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MeshObjectLoader.MeshArrays model(InputStream is) {
        if (is != null) {
            InputStream targetStream = is;
            modelShip = MeshObjectLoader.loadModelMeshFromStream(targetStream);
            return modelShip;
        } else {
            Log.d("E: ", "File not Found");
            return null;
        }

    }

    public void damage(float dmg) {
        if(health > 0f) {
            health -= dmg;
        }
    }

    public void resetHealth() {
        health = 3f;
    }

    @Override
    public void draw(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glPushMatrix();
        {
            gl.glMultMatrixf(transformationMatrix, 0);

            gl.glScalef(scale, scale, scale);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glLineWidth(2.0f);
            gl.glRotatef(-90, 0, 1, 0);
            gl.glColor4f(1,1,1,1);

            gl.glPushMatrix();
            {
                gl.glRotatef(rotation, 0, 1, 0);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, modelShip.getVertices());
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, modelShip.getNumVertices() / 3);

            gl.glPopMatrix();

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        gl.glPopMatrix();
    } }

    @Override
    public void update(float fracSec) {
        if(rotateShip) {
            if(dir) {
                if(rotation == 360f || rotation > 360f) rotation = 0;
                rotation += fracSec * angularVelocity * deathRotationVelocity * rotateSpeed;
            } else {
                if(rotation == -360f || rotation < -360f) rotation = 0;
                rotation -= fracSec * angularVelocity * deathRotationVelocity * rotateSpeed; }}

        //NICHT KLAMMERN!!!
        updatePosition(fracSec);

    }

    public float getRotationShip() {
        return rotation;
    }

    public void setRotation(boolean dir) {
        rotateShip = true;
        this.dir = dir;

    }

    public void stopRotation() {
        rotateShip = false;
    }

    public int getHealth() {
        return (int) health;
    }
}

