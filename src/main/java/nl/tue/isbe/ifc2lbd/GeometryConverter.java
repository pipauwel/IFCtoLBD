/*******************************************************************************
 * Copyright (C) 2017 Chi Zhang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package nl.tue.isbe.ifc2lbd;

import nl.tue.isbe.IFC.IfcVersion;
import nl.tue.isbe.bimsparql.geometry.*;
import nl.tue.isbe.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.isbe.bimsparql.geometry.ewkt.WktWriteException;
import org.bimserver.geometry.Matrix;
import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineGeometry;
import org.bimserver.plugins.renderengine.RenderEngineModel;
import org.ifcopenshell.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.*;

public class GeometryConverter {

    int i;

    private Map<Long, IfcOpenShellEntityInstance> instancesById;
    InstanceGeometry instanceGeometry;

    public GeometryConverter() {
        instanceGeometry = new InstanceGeometry();
    }

    public void parseModel2GeometryStream(InputStream in, OutputStream out, IfcVersion ifcVersion)
            throws IOException, WktWriteException {
        i = 0;
        generateGeometry(in);
        System.out.println("Model parsed!");
        System.out.println("Finished!");
    }

    public void generateGeometry(InputStream in) throws WktWriteException {
        IfcOpenShellEngine ifcOpenShellEngine;
        try {
            //Path path = Paths.get(GeometryConverter.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            //String enginePath=path.toString() + getEngineForOS();
            //Path enginePath = Paths.get(GeometryGenerator.class.getClassLoader().getResource("exe/64/win/IfcGeomServer.exe").getPath());
            IfcGeomServerClient client = new IfcGeomServerClient(IfcGeomServerClient.ExecutableSource.S3, IfcOpenShellEnginePlugin.DEFAULT_COMMIT_SHA);
            ifcOpenShellEngine = new IfcOpenShellEngine(client.getExecutableFilename(), false, false);//enginePath
            ifcOpenShellEngine.init();

            RenderEngineModel renderEngineModel = ifcOpenShellEngine.openModel(in);
            renderEngineModel.generateGeneralGeometry();
            instancesById = ((IfcOpenShellModel) renderEngineModel).getInstancesById();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (RenderEngineException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } /*catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    public InstanceGeometry getGeometry(String lineNum, IfcOpenShellEntityInstance shellInstance){
        InstanceGeometry ig = new InstanceGeometry();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor
                .submit(new GeometryConverter.GeometryTask(lineNum, shellInstance));

        executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //System.out.println(i);
        ig = instanceGeometry;
        instanceGeometry = null;
        return ig;

        /*if (ig != null) {
            addGeometryTriples(ig);
            Geometry g = toGeometry(ig);

            String s = toWKT(g);
            String igType = ig.getType();
            long igId = ig.getId();
        }*/

        /*Iterator<Map.Entry<Long, IfcOpenShellEntityInstance>> it = GeometryConverter.instancesById.entrySet().iterator();
        while (it.hasNext()) {
            i++;
            Map.Entry<Long, IfcOpenShellEntityInstance> pair = it.next();

            InstanceGeometry ig = new InstanceGeometry();
            IfcOpenShellEntityInstance renderEngineInstance = pair.getValue();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor
                    .submit(new GeometryConverter.GeometryTask(pair.getKey().toString(), renderEngineInstance));

                *//*try {
                    //System.out.println(future.get(1, TimeUnit.SECONDS));
                } catch (TimeoutException e) {
                    future.cancel(true);
                    //System.out.println(pair.getKey() + " " + renderEngineInstance.getEntityType() + " Terminated!");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*//*
            executor.shutdownNow();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //System.out.println(i);
            ig = instanceGeometry;
            instanceGeometry = null;
            if (ig != null) {
                addGeometryTriples(ig);
            }
        }*/
    }

    private static int[] bufferToIntArray(ByteBuffer buffer){
        int[] array = new int[buffer.capacity()];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.get(i);
        }
        return array;
    }

    private static float[] bufferToFloatArray(ByteBuffer buffer){
        float[] array = new float[buffer.capacity()];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.get(i);
        }
        return array;
    }

    private void transformGeometry(String id, IfcOpenShellEntityInstance renderEngineInstance)
            throws WktWriteException {
        RenderEngineGeometry geometry = renderEngineInstance.generateGeometry();
        if (geometry != null && geometry.getNrIndices() > 0) {
            instanceGeometry = new InstanceGeometry();
            instanceGeometry.setId(renderEngineInstance.getEntityId());
            instanceGeometry.setPointers(bufferToIntArray(geometry.getIndices()));
            instanceGeometry.setType(renderEngineInstance.getEntityType());
            instanceGeometry.setColors(bufferToFloatArray(geometry.getMaterials()));
            instanceGeometry.setMaterialIndices(bufferToIntArray(geometry.getMaterialIndices()));

            double[] tranformationMatrix = new double[16];
            Matrix.setIdentityM(tranformationMatrix, 0);
            if (renderEngineInstance.getTransformationMatrix() != null) {
                tranformationMatrix = renderEngineInstance.getTransformationMatrix();
            }
            double[] points = new double[geometry.getNrVertices()];//getVertices().length
            for (int i = 0; i < instanceGeometry.getPointers().length; i++) {//
                processExtends(tranformationMatrix, bufferToFloatArray(geometry.getVertices()), instanceGeometry.getPointers()[i] * 3,
                        points);
            }
            instanceGeometry.setPoints(points);
        }
    }

    private String getEngineForOS() throws RenderEngineException {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch");
        String result = "/exe";
        if (arch.contains("64")) {
            result = result + "/64";
            if (os.indexOf("win") >= 0) {
                return result + "/win/IfcGeomServer.exe";
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
                return result + "/linux/IfcGeomServer";
            } else if (os.indexOf("mac") >= 0) {
                return result + "/osx/IfcGeomServer";
            }
        } else if (arch.contains("32")) {
            result = result + "/32";
            if (os.indexOf("win") >= 0) {
                return result + "/win/IfcGeomServer.exe";
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
                return result + "/linux/IfcGeomServer";
            }
        }
        throw new RenderEngineException("not supported operation system : " + os + " " + arch);

    }

    /*public void processBoundingBoxes() {

    }*/

    public Geometry toGeometry(InstanceGeometry ig) {
        TriangulatedSurface geometry = new TriangulatedSurface();
        double[] points = ig.getPoints();
        int[] indices = ig.getPointers();
        if (points != null && indices != null) {
            for (int i = 0; i < indices.length; i = i + 3) {
                double d1 = points[indices[i] * 3];
                double d2 = points[indices[i] * 3 + 1];
                double d3 = points[indices[i] * 3 + 2];
                double d4 = points[indices[i + 1] * 3];
                double d5 = points[indices[i + 1] * 3 + 1];
                double d6 = points[indices[i + 1] * 3 + 2];
                double d7 = points[indices[i + 2] * 3];
                double d8 = points[indices[i + 2] * 3 + 1];
                double d9 = points[indices[i + 2] * 3 + 2];
                Triangle t = new Triangle(new Point3d(d1, d2, d3), new Point3d(d4, d5, d6), new Point3d(d7, d8, d9));
                geometry.addTriangle(t);
            }
        }
        return geometry;
    }

    public String toWKT(Geometry g) throws WktWriteException {
        EwktWriter ew = new EwktWriter("");
        ew.writeRec(g);
        return ew.getString();
    }


    private void processExtends(double[] transformationMatrix, float[] ds, int index, double[] output) {
        double x = ds[index];
        double y = ds[index + 1];
        double z = ds[index + 2];

        double[] result = new double[4];
        Matrix.multiplyMV(result, 0, transformationMatrix, 0, new double[] { x, y, z, 1 }, 0);
        output[index] = result[0];
        output[index + 1] = result[1];
        output[index + 2] = result[2];
    }

    class GeometryTask implements Callable<String> {
        String id;
        IfcOpenShellEntityInstance r;

        public GeometryTask(String id, IfcOpenShellEntityInstance r) {
            this.id = id;
            this.r = r;
        }

        @Override
        public String call() throws Exception {
            transformGeometry(id, r);
            return "Ready!";
        }
    }

    public Map<Long, IfcOpenShellEntityInstance> getAllInstancesById(){
        return instancesById;
    }
}
