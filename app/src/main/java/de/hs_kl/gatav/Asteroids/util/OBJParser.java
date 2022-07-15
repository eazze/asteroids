package de.hs_kl.gatav.Asteroids.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class OBJParser {

    public static VertArray loadModelVertices(InputStream is) {
        BufferedReader bufferedReader = null;

        try {
            ArrayList<Float> vList = new ArrayList<Float>();
            ArrayList<Werte> wList = new ArrayList<Werte>();

            FloatBuffer mVertexBuffer;

            int werteCounter = 0;

            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String str;
            String[] tmp;

            while ((str = bufferedReader.readLine()) != null) {

                str = str.replace("  ", " ");
                tmp = str.split(" ");

                if (tmp[0].equalsIgnoreCase("v")) {
                    for (int i = 1; i < 4; i++) {
                        vList.add(Float.parseFloat(tmp[i]));
                    }
                }


                if (tmp[0].equalsIgnoreCase("f")) {

                    String[] ftmp;
                    int x;
                    int y;
                    int z;

                    for (int i = 1; i < 4; i++) {
                        ftmp = tmp[i].split("/");

                        x = (!(ftmp[0].equals(""))) ? Integer.parseInt(ftmp[0]) - 1 : -1;
                        y = (!(ftmp[1].equals(""))) ? Integer.parseInt(ftmp[1]) - 1 : -1;
                        z = (!(ftmp[1].equals(""))) ? Integer.parseInt(ftmp[2]) - 1 : -1;

                        wList.add(new Werte(x, y, z));
                    }

                    werteCounter++;

                    if (tmp.length > 4 && !tmp[4].equals("")) {

                        for (int i = 1; i < 5; i++) {
                            ftmp = tmp[i].split("/");

                            if (i == 1 || i == 3) {
                                x = (!(ftmp[0].equals(""))) ? Integer.parseInt(ftmp[0]) - 1 : -1;
                                y = (!(ftmp[1].equals(""))) ? Integer.parseInt(ftmp[1]) - 1 : -1;
                                z = (!(ftmp[2].equals(""))) ? Integer.parseInt(ftmp[2]) - 1 : -1;
                                wList.add(new Werte(x, y, z));
                            } else if (i == 2) {
                                String[] gtmp = tmp[4].split("/");
                                x = (!(gtmp[0].equals(""))) ? Integer.parseInt(gtmp[0]) - 1 : -1;
                                y = (!(gtmp[1].equals(""))) ? Integer.parseInt(gtmp[1]) - 1 : -1;
                                z = (!(gtmp[2].equals(""))) ? Integer.parseInt(gtmp[2]) - 1 : -1;
                                wList.add(new Werte(x, y, z));
                            }
                        }

                        werteCounter++;
                    }
                }
            }

            int wListSize = wList.size();
            ByteBuffer vbb = ByteBuffer.allocateDirect(wListSize * 4 * 3);
            vbb.order(ByteOrder.LITTLE_ENDIAN);
            mVertexBuffer = vbb.asFloatBuffer();

            Werte w;

            for (int j = 0; j < wListSize; j++) {

                w = wList.get(j);

                mVertexBuffer.put(vList.get((int) w.x * 3));
                mVertexBuffer.put(vList.get((int) (w.x * 3 + 1)));
                mVertexBuffer.put(vList.get((int) (w.x * 3 + 2)));


            }
            mVertexBuffer.rewind();
            return new VertArray(mVertexBuffer, werteCounter * 3);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static class Werte {
        public long x;
        public int y;
        public int z;

        public Werte(long x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class VertArray {

        private Buffer vertices;

        private int vertCounter;

        public VertArray(Buffer vertices, int vertCounter) {
            this.vertices = vertices;
            this.vertCounter = vertCounter;
        }

        public Buffer getVertices() {
            return vertices;
        }

        public int getNumVertices() {
            return vertCounter;
        }
    }
}