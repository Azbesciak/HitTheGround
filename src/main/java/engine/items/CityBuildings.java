package engine.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.loaders.assimp.StaticMeshesLoader;
import engine.loaders.obj.OBJLoader;

public class CityBuildings extends GameItem {


    public GameItem[] getGameItems() {
        return new GameItem[]{this};
    }

    public CityBuildings(String objFile) throws Exception {
        super();
        Mesh cityBuildingsMesh = OBJLoader.loadMesh(objFile);
        Material material = new Material();
        cityBuildingsMesh.setMaterial(material);
        setMesh(cityBuildingsMesh);
        setPosition(0, -100, 0);
        scale = 0.1f;
        getMesh().setBoundingRadius(1000);
    }

}
