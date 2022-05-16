package de.hs_kl.gatav.flyingsaucerfull.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Shot extends SpaceObject {

    private static float[] colorShot= { 0.8f, 0.8f, 0.8f };
    private float[] shotArray = createShot();
    boolean buffersInitialized = false;

    private static FloatBuffer shotVerticesBuffer;


    public Shot() {
        scale = 0.5f;


        if (!buffersInitialized) {
            // Initialize buffers


            ByteBuffer shotCircleBB = ByteBuffer.allocateDirect(shotArray.length * 2);
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

        gl.glPushMatrix();
        {
            gl.glMultMatrixf(transformationMatrix, 0);

            gl.glScalef(scale, scale, scale);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glLineWidth(2.0f);

           // draw ring
            gl.glPushMatrix();
            {

                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, shotVerticesBuffer);
            }
            gl.glPopMatrix();
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        gl.glPopMatrix();
    }

    @Override
    public void update(float fracSec) {
        updatePosition(fracSec);
    }

    private float[] createShot() {
        float[] vertices = new float[3];
        int i = 0;
        for(float t=0;t<2*Math.PI;i+=0.1)
        {
            vertices[i]=(float) (scale*Math.cos(t)+1);
            vertices[i+1]= (float) (scale*Math.cos(t)+1);
            vertices[i+2]=0;
            i++;
        }
        return vertices;
    }
}
