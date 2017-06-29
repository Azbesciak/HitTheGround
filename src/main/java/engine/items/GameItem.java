package engine.items;

import engine.graph.Material;
import engine.graph.Texture;
import engine.loaders.obj.OBJLoader;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import engine.graph.Mesh;

public class GameItem {

    private boolean selected;

    private Mesh[] meshes;

    protected final Vector3f position;

    protected float scale;

    protected final Quaternionf rotation;

    private int textPos;
    
    private boolean disableFrustumCulling;

    private boolean insideFrustum;

    public GameItem(String objModel, String textureFile) throws Exception {
       this(objModel, textureFile, null);
    }

    public GameItem(String objModel, String textureFile, String normalFile) throws Exception {
        this();
        Mesh mesh = OBJLoader.loadMesh(objModel);
        Texture texture = new Texture(textureFile);
        final Material material = new Material(texture, 0.0f);
        if (normalFile != null) {
            final Texture normal = new Texture(normalFile);
            material.setNormalMap(normal);
        }
        mesh.setMaterial(material);
        setMesh(mesh);
        setPosition(0, 0, 0);
    }

    public GameItem() {
        selected = false;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextPos() {
        return textPos;
    }

    public boolean isSelected() {
        return selected;
    }

    public final void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public boolean isInsideFrustum() {
        return insideFrustum;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        this.insideFrustum = insideFrustum;
    }
    
    public boolean isDisableFrustumCulling() {
        return disableFrustumCulling;
    }

    public void setDisableFrustumCulling(boolean disableFrustumCulling) {
        this.disableFrustumCulling = disableFrustumCulling;
    }    
}
