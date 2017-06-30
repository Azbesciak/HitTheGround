package engine.items;

import org.joml.Vector3f;

public class CityBuildings extends GameItem {


    public GameItem[] getGameItems() {
        return new GameItem[]{this};
    }

    public CityBuildings(String objFile, String textureDir, Terrain terrain) throws Exception {
        super(objFile, textureDir, true);
        final float height = terrain.getHeight(new Vector3f(0, 0, 0));
        setPosition(0, height, 0);
        scale = 0.1f;
        getMesh().setBoundingRadius(1000);
        setDisableFrustumCulling(true);
    }

}
