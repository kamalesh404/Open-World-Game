package com.javacity.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.util.Random;

public class RealisticSky {
    private Node skyNode;
    private Node cloudNode;
    private Geometry sunDisc;
    private AssetManager assetManager;

    private CityBackdrop cityBackdrop;

    public RealisticSky(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.skyNode = new Node("RealisticSkyNode");
        this.cloudNode = new Node("CloudLayerNode");
        
        buildSkyDome();
        
        this.cityBackdrop = new CityBackdrop(assetManager);
        skyNode.attachChild(cityBackdrop.getBackdropNode());

        skyNode.attachChild(cloudNode);

        buildSunDisc();
        buildCloudLayer();
    }

    private void buildSkyDome() {
        // High-poly sky dome surrounding the world behind the backdrop cylinder (radius 2200m)
        Sphere skySphere = new Sphere(32, 32, 2200f);
        skySphere.setTextureMode(Sphere.TextureMode.Polar);
        Geometry skyGeo = new Geometry("SkyDomeGeo", skySphere);

        Material skyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        skyMat.setColor("Color", new ColorRGBA(0.92f, 0.48f, 0.22f, 1.0f)); // Golden crimson sunset sky
        skyMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        skyMat.getAdditionalRenderState().setDepthWrite(false);

        skyGeo.setMaterial(skyMat);
        skyGeo.setQueueBucket(RenderQueue.Bucket.Sky);
        skyNode.attachChild(skyGeo);
    }

    private void buildSunDisc() {
        // Bright glowing sun disc on distant horizon
        Sphere sunSphere = new Sphere(24, 24, 55f);
        sunDisc = new Geometry("SunDisc", sunSphere);
        Material sunMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMat.setColor("Color", new ColorRGBA(1.0f, 0.95f, 0.70f, 1.0f));
        sunDisc.setMaterial(sunMat);
        sunDisc.setQueueBucket(RenderQueue.Bucket.Sky);

        Vector3f sunDir = new Vector3f(0.85f, 0.18f, 0.35f).normalizeLocal();
        sunDisc.setLocalTranslation(sunDir.mult(2000f));
        skyNode.attachChild(sunDisc);
    }

    private void buildCloudLayer() {
        Texture2D cloudTex = createProceduralCloudTexture();
        Random rand = new Random(42);
        int cloudCount = 25;

        Material cloudMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cloudMat.setTexture("ColorMap", cloudTex);
        cloudMat.setColor("Color", new ColorRGBA(1.0f, 0.88f, 0.75f, 0.50f));
        cloudMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cloudMat.getAdditionalRenderState().setDepthWrite(false);
        cloudMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        for (int i = 0; i < cloudCount; i++) {
            float width = 250f + rand.nextFloat() * 300f;
            float height = 250f + rand.nextFloat() * 300f;
            Quad cloudQuad = new Quad(width, height);
            Geometry cloudGeo = new Geometry("CloudBillboard_" + i, cloudQuad);
            cloudGeo.setMaterial(cloudMat);
            cloudGeo.setQueueBucket(RenderQueue.Bucket.Sky);

            float angle = rand.nextFloat() * FastMath.TWO_PI;
            float dist = 300f + rand.nextFloat() * 1200f;
            float altitude = 650f + rand.nextFloat() * 300f;

            float cx = FastMath.cos(angle) * dist - width / 2f;
            float cz = FastMath.sin(angle) * dist - height / 2f;

            cloudGeo.setLocalTranslation(cx, altitude, cz);
            Quaternion rot = new Quaternion();
            rot.fromAngles(FastMath.HALF_PI + (rand.nextFloat() - 0.5f) * 0.3f, rand.nextFloat() * FastMath.TWO_PI, 0);
            cloudGeo.setLocalRotation(rot);

            cloudNode.attachChild(cloudGeo);
        }
    }

    private Texture2D createProceduralCloudTexture() {
        int size = 128;
        ByteBuffer buf = BufferUtils.createByteBuffer(size * size * 4);
        for (int y = 0; y < size; y++) {
            float ny = (y / (float) size) * 2.0f - 1.0f;
            for (int x = 0; x < size; x++) {
                float nx = (x / (float) size) * 2.0f - 1.0f;
                float distSq = nx * nx + ny * ny;
                float alpha = FastMath.clamp(1.0f - FastMath.sqrt(distSq), 0.0f, 1.0f);
                alpha = FastMath.pow(alpha, 2.0f);

                buf.put((byte) 255);
                buf.put((byte) 230);
                buf.put((byte) 200);
                buf.put((byte) (int)(alpha * 180));
            }
        }
        buf.rewind();
        Image img = new Image(Image.Format.RGBA8, size, size, buf);
        Texture2D tex = new Texture2D(img);
        tex.setMinFilter(Texture.MinFilter.Trilinear);
        tex.setMagFilter(Texture.MagFilter.Bilinear);
        return tex;
    }

    public void update(float tpf, Vector3f playerPos) {
        if (playerPos != null) {
            skyNode.setLocalTranslation(playerPos.x, 0, playerPos.z);
        }
        cloudNode.rotate(0, tpf * 0.0012f, 0);
        if (cityBackdrop != null) {
            cityBackdrop.update(playerPos);
        }
    }

    public Node getSkyNode() {
        return skyNode;
    }
}
