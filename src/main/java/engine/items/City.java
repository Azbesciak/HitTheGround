package engine.items;

/**
 * Created by Wojciech Jaronski on 29.06.2017.
 */
public class City extends Terrain {
    /**
     * A Terrain is composed by blocks, each block is a GameItem constructed
     * from a HeightMap.
     *
     * @param terrainSize   The number of blocks will be terrainSize * terrainSize
     * @param scale         The scale to be applied to each terrain block
     * @param minY          The minimum y value, before scaling, of each terrain block
     * @param maxY          The maximum y value, before scaling, of each terrain block
     * @param heightMapFile
     * @param textureFile
     * @param textInc
     * @throws Exception
     */
    public City(int terrainSize, float scale, float minY, float maxY, String heightMapFile, String textureFile, int textInc) throws Exception {
        super(terrainSize, scale, minY, maxY, heightMapFile, textureFile, textInc);
    }
}
