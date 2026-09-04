package com.javacity.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

public class CityBackdrop {
    private Node backdropNode;
    private AssetManager assetManager;

    public CityBackdrop(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.backdropNode = new Node("CityBackdropNode");
        buildSkylineRing();
    }

    private void buildSkylineRing() {
        // Distant 360-degree cylindrical skyline wall (Radius 1800m, Height -120m to 850m)
        // Placed far on distant horizon so horizontal curvature is completely flat and natural
        float radius = 1800f;
        float bottomY = -120f;
        float topY = 850f;
        int segments = 64;
        float tileCount = 7.0f; // 7x horizontal tiling around 1800m circumference for crisp building detail

        Mesh mesh = new Mesh();

        int vertexCount = (segments + 1) * 2;
        Vector3f[] positions = new Vector3f[vertexCount];
        Vector3f[] normals = new Vector3f[vertexCount];
        Vector2f[] texCoords = new Vector2f[vertexCount];
        int[] indices = new int[segments * 6];

        for (int i = 0; i <= segments; i++) {
            float angle = ((float) i / segments) * FastMath.TWO_PI;
            float cos = FastMath.cos(angle);
            float sin = FastMath.sin(angle);

            float x = radius * cos;
            float z = radius * sin;

            float u = ((float) i / segments) * tileCount;

            Vector3f norm = new Vector3f(-cos, 0, -sin);

            int idxBottom = i * 2;
            int idxTop = i * 2 + 1;

            positions[idxBottom] = new Vector3f(x, bottomY, z);
            normals[idxBottom] = norm;
            texCoords[idxBottom] = new Vector2f(u, 0.0f);

            positions[idxTop] = new Vector3f(x, topY, z);
            normals[idxTop] = norm;
            texCoords[idxTop] = new Vector2f(u, 1.0f);
        }

        int indexPtr = 0;
        for (int i = 0; i < segments; i++) {
            int b0 = i * 2;
            int t0 = i * 2 + 1;
            int b1 = (i + 1) * 2;
            int t1 = (i + 1) * 2 + 1;

            indices[indexPtr++] = b0;
            indices[indexPtr++] = t0;
            indices[indexPtr++] = b1;

            indices[indexPtr++] = t0;
            indices[indexPtr++] = t1;
            indices[indexPtr++] = b1;
        }

        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();

        Geometry ringGeo = new Geometry("SkylineRingGeo", mesh);

        Texture tex = null;
        try {
            tex = assetManager.loadTexture("Textures/Sky/skyline_backdrop.jpg");
            tex.setWrap(Texture.WrapMode.Repeat);
        } catch (Exception e) {
            System.err.println("Backdrop texture load error: " + e.getMessage());
        }

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if (tex != null) {
            mat.setTexture("ColorMap", tex);
        }
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setDepthWrite(false);

        ringGeo.setMaterial(mat);
        ringGeo.setQueueBucket(RenderQueue.Bucket.Sky);
        backdropNode.attachChild(ringGeo);
    }

    public void update(Vector3f playerPos) {
        if (playerPos != null) {
            backdropNode.setLocalTranslation(playerPos.x, 0, playerPos.z);
        }
    }

    public Node getBackdropNode() {
        return backdropNode;
    }
}
