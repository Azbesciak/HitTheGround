package engine.items;

public class CityBuildings extends GameItem {


    public GameItem[] getGameItems() {
        return new GameItem[]{this};
    }

    public CityBuildings(String objFile, String textureDir) throws Exception {
        super(objFile, textureDir, true);
        setPosition(0, -100, 0);
        scale = 0.1f;
        getMesh().setBoundingRadius(1000);
        setDisableFrustumCulling(true);
    }

}
