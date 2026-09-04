package com.javacity.world;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.MatParam;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

public class CityLoader {
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;

    public CityLoader(AssetManager assetManager, PhysicsSpace physicsSpace) {
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
    }

    public Node loadCity() {
        Node cityNode = new Node("City");
        
        Spatial cityModel = null;
        try {
            cityModel = assetManager.loadModel("Scenes/City/city_district.glb");
        } catch (AssetNotFoundException e) {
            System.err.println("WARNING: city_district.glb not found. Creating temporary ground plane.");
            cityModel = createTempGround();
        }
        
        // Sanitize any negative scale transforms from imported 3D nodes before Bullet physics generation
        sanitizeNegativeScales(cityModel);

        // Gently ensure materials render naturally without over-reflection
        adjustMaterialsForNonIBL(cityModel);
        
        cityModel.setLocalScale(com.javacity.core.GameConfig.WORLD_SCALE);
        // Lower city model slightly so tree trunks and solar panel bases rest flat on the ground plane
        cityModel.setLocalTranslation(0, -0.2f, 0);
        
        CollisionShape sceneShape = null;
        try {
            sceneShape = CollisionShapeFactory.createMeshShape(cityModel);
        } catch (Exception e) {
            System.err.println("Warning creating MeshShape for scene: " + e.getMessage() + ". Utilizing BoxShape fallback.");
            sceneShape = CollisionShapeFactory.createBoxShape(cityModel);
        }
        
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        cityModel.addControl(landscape);
        physicsSpace.add(landscape);

        // Unbreakable 10-meter thick solid ground floor under the entire city at y=0.0f
        com.jme3.bullet.collision.shapes.BoxCollisionShape groundShape = new com.jme3.bullet.collision.shapes.BoxCollisionShape(new com.jme3.math.Vector3f(2500f, 5.0f, 2500f));
        RigidBodyControl groundBody = new RigidBodyControl(groundShape, 0);
        groundBody.setPhysicsLocation(new com.jme3.math.Vector3f(0, -5.0f, 0));
        physicsSpace.add(groundBody);

        // Invisible World Perimeter Barriers matching exact map bounds (X: -280 to +280, Z: -280 to +770)
        float wallHeight = 500f;

        // North Wall (Z = +770f)
        com.jme3.bullet.collision.shapes.BoxCollisionShape northWall = new com.jme3.bullet.collision.shapes.BoxCollisionShape(new Vector3f(300f, wallHeight, 20f));
        RigidBodyControl northBody = new RigidBodyControl(northWall, 0);
        northBody.setPhysicsLocation(new Vector3f(0, wallHeight, 770f));
        physicsSpace.add(northBody);

        // South Wall (Z = -280f)
        com.jme3.bullet.collision.shapes.BoxCollisionShape southWall = new com.jme3.bullet.collision.shapes.BoxCollisionShape(new Vector3f(300f, wallHeight, 20f));
        RigidBodyControl southBody = new RigidBodyControl(southWall, 0);
        southBody.setPhysicsLocation(new Vector3f(0, wallHeight, -280f));
        physicsSpace.add(southBody);

        // East Wall (X = +290f)
        com.jme3.bullet.collision.shapes.BoxCollisionShape eastWall = new com.jme3.bullet.collision.shapes.BoxCollisionShape(new Vector3f(20f, wallHeight, 535f));
        RigidBodyControl eastBody = new RigidBodyControl(eastWall, 0);
        eastBody.setPhysicsLocation(new Vector3f(290f, wallHeight, 245f));
        physicsSpace.add(eastBody);

        // West Wall (X = -290f)
        com.jme3.bullet.collision.shapes.BoxCollisionShape westWall = new com.jme3.bullet.collision.shapes.BoxCollisionShape(new Vector3f(20f, wallHeight, 535f));
        RigidBodyControl westBody = new RigidBodyControl(westWall, 0);
        westBody.setPhysicsLocation(new Vector3f(-290f, wallHeight, 245f));
        physicsSpace.add(westBody);
        
        cityNode.attachChild(cityModel);
        
        return cityNode;
    }

    private void sanitizeNegativeScales(Spatial spatial) {
        if (spatial == null) return;
        com.jme3.math.Vector3f scale = spatial.getLocalScale();
        if (scale.x < 0 || scale.y < 0 || scale.z < 0) {
            spatial.setLocalScale(Math.abs(scale.x), Math.abs(scale.y), Math.abs(scale.z));
        }
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                sanitizeNegativeScales(child);
            }
        }
    }
    
    private void adjustMaterialsForNonIBL(Spatial spatial) {
        if (spatial instanceof Geometry) {
            Geometry geo = (Geometry) spatial;
            Material mat = geo.getMaterial();
            if (mat != null) {
                if (mat.getMaterialDef().getName().contains("PBR")) {
                    // Safe non-IBL PBR values that preserve ALL original textures and vibrant base colors
                    try {
                        if (mat.getParam("Metallic") != null) mat.setFloat("Metallic", 0.05f);
                        if (mat.getParam("Roughness") != null) mat.setFloat("Roughness", 0.45f);
                    } catch (Exception e) {
                        // Ignore parameter mismatch
                    }
                }
            }
        }
        
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                adjustMaterialsForNonIBL(child);
            }
        }
    }
    
    private Spatial createTempGround() {
        Quad quad = new Quad(100f, 100f);
        Geometry geo = new Geometry("TempGround", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", ColorRGBA.Gray);
        mat.setFloat("Roughness", 0.8f);
        mat.setFloat("Metallic", 0.1f);
        geo.setMaterial(mat);
        geo.setLocalTranslation(-50f, 0, 50f);
        geo.rotate(-1.5708f, 0, 0);
        return geo;
    }
}
