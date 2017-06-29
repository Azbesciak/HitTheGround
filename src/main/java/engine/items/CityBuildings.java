package engine.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.loaders.obj.OBJLoader;
import engine.loaders.obj_advanced.ModelData;

/**
 * Created by Wojciech Jaronski on 29.06.2017.
 */
public class CityBuildings extends GameItem {

    private ModelData data;

    public ModelData getData() {
        return data;
    }

//    public Mesh getMesh() {
//        return data.getMesh();
//    }

    public GameItem[] getGameItems() {
        return new GameItem[]{new GameItem(getMeshes())};
    }

    public CityBuildings(String objFile) throws Exception {
        super();
        Mesh cityBuildingsMesh = OBJLoader.loadMesh(objFile);
        Material material = new Material();
        cityBuildingsMesh.setMaterial(new Material());
        setMesh(cityBuildingsMesh);
        setPosition(0, 0, 0);
//        data = OBJFileLoader.loadOBJ(objFile);
    }

}
