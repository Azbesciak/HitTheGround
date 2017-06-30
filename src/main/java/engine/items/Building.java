package engine.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.loaders.obj.MaterialLoader;
import engine.loaders.obj.OBJLoader;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wojciech Jaronski on 29.06.2017.
 */
public class Building extends GameItem {

    public GameItem[] getGameItems() {

        return new GameItem[]{this};
    }

    public Building(String objFile, int posX, int posY, int posZ, float scaling, Quaternionf rotation) throws Exception {
        super(objFile, null, true);
        for (Mesh mesh : getMeshes()) {
            mesh.setBoundingRadius(1000);
        }

        setPosition(posX, posY, posZ);
        scale = scaling;
        setRotation(rotation);
//        getMesh().setBoundingRadius(1000);
    }

}
