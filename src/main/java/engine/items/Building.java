package engine.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.loaders.obj.MaterialLoader;
import engine.loaders.obj.OBJLoader;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wojciech Jaronski on 29.06.2017.
 */
public class Building extends GameItem {

    public GameItem[] getGameItems() {

        return new GameItem[]{this};
    }

    public Building(String objFile, String textureDir, float scaling, Quaternionf rotation)
            throws Exception {
        super(objFile, textureDir, true);
        for (Mesh mesh : getMeshes()) {
            mesh.setBoundingRadius(20);
        }
        scale = scaling;
        setRotation(rotation);

    }

    public void setPosition(float posX, float posZ, Terrain terrain) {
        final float posY = terrain.getHeight(new Vector3f(posX, 0, posZ));
        setPosition(posX, posY, posZ);
    }

}
