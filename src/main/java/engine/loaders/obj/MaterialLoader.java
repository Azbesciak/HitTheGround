package engine.loaders.obj;

import engine.Utils;
import engine.graph.Material;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Wojciech Jaronski on 30.06.2017.
 */
public class MaterialLoader {

    /**
     * Ns 96.078431
     Ka 1.000000 1.000000 1.000000
     Kd 0.640000 0.640000 0.640000
     Ks 0.500000 0.500000 0.500000
     Ke 0.000000 0.000000 0.000000
     Ni 1.000000
     d 1.000000
     illum 2

     * @param tokens
     * @return
     * @throws Exception
     */

    private static Vector4f floatToVec(String[] tokens){
//        new Vector3f(0,0,0).
        return new Vector4f(
                Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3]),
            1f
        );
    }

    public static HashMap<String, Material> loadMaterials(String materialFile) throws Exception {
        HashMap<String, Material> materials = new HashMap<>(11);
        List<String> lines = Utils.readAllLines(materialFile);

        String matName="";

        for (int i = 0; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split("\\s+");

            switch (tokens[0]) {
                case "newmtl":
                    matName = tokens[1];
                    Material material = new Material();
                    materials.put(matName,material);
                    break;
                case "Ns":
                    break;
                case "Ka": materials.get(matName).setAmbientColour(floatToVec(tokens));
                    break;
                case "Kd": materials.get(matName).setDiffuseColour(floatToVec(tokens));
                    break;
                case "Ks": materials.get(matName).setSpecularColour(floatToVec(tokens));
                    break;
                case "Ke": //nie wykorzystujemy
                    break;
                case "Ni": //nie wykorzystujemy
                    break;
                case "d": //nie wykorzystujemy
                    break;
                case "illum": //nie wykorzystujemy
                    break;
                default:
                    break;
            }
        }


        return materials;
    }

}
