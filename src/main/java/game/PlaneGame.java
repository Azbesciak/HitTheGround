package game;

import engine.*;
import engine.graph.Camera;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.graph.weather.Fog;
import engine.items.Building;
import engine.items.City;
import engine.items.CityBuildings;
import engine.items.SkyBox;
import engine.items.Terrain;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaneGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.10f;
    private static final int STRAIGHT_ANGLE = 180;
    private static final int BUILDING_SPACES = 60;
    public static final int NUMBER_OF_AVAILABLE_BUILDINGS = 4;
    public static final int NUMBER_OF_EXISTING_BUILDINGS = 5;

    private final Renderer renderer;
    private final Camera camera;
    private Scene scene;
    private float angleInc;
    private float lightAngle;
    private boolean firstTime;
    private static final float skyBoxScale = 500.0f;
    private boolean sceneChanged;

    PlaneGame() {
        renderer = new Renderer();
        camera = new Camera();
        angleInc = 0;
        lightAngle = 90;
        firstTime = true;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        scene = new Scene();

        final Terrain terrain = prepareTerrain();
        scene.setTerrain(terrain);
//        scene.addGameItems(prepareCityBuildings(terrain).getGameItems());
        for (Building building : prepareBuildings(terrain, NUMBER_OF_EXISTING_BUILDINGS)) {
            scene.addGameItems(building.getGameItems());
        }

        scene.setPlane(createPlane());
        scene.setRenderShadows(true);
        scene.setFog(createFog());
        scene.setSkyBox(createSkyBox(skyBoxScale));
        setupLights();
        setupCamera();
    }

    private Plane createPlane() throws Exception {
        return new Plane("models/plane/FA-22_Raptor.obj", "/models/plane");
    }

    private List<Building> prepareBuildings(Terrain terrain, int numberOfBuildings) throws Exception {
        final Random random = new Random();
        List<Building> buildings = new ArrayList<>(numberOfBuildings);
        final Building[] availableBuildings = getAvailableBuildings();
        float axis = numberOfBuildings * BUILDING_SPACES;
        for (float x = -axis / 2; x <= axis / 2; x += BUILDING_SPACES) {
            for (float z = -axis / 2; z  <= axis / 2; z += BUILDING_SPACES) {
                final int buildingIndex = random.nextInt(NUMBER_OF_AVAILABLE_BUILDINGS);
                final Building building = Utils.deepCopy(availableBuildings[buildingIndex]);
                building.setPosition(x, z, terrain);
                buildings.add(building);
                building.setDisableFrustumCulling(false);
            }
        }
        return buildings;
    }

    private Building[] getAvailableBuildings() throws Exception {
         return new Building[]{
                new Building("models/ob/building/building002.obj", null,
                             4f, new Quaternionf(0.707f, 0, 0)),
                new Building("models/ob/skycraper/skycraper001.obj",null,
                             0.004f, new Quaternionf(0, 0, 0)),
                new Building("models/ob/block/block001.obj", null,
                             18f, new Quaternionf(0, 0,0)),
                 new Building("models/buildings/obj/ResidentialBuildings001.obj",
                              "/models/buildings/textures",
                              1, new Quaternionf(0, 0,0))
        };
    }


    private Terrain prepareTerrain() throws Exception {
        float terrainScale = skyBoxScale;
        int terrainSize = 5;
        float minY = 0f;
        float maxY = 0.25f;
        int textInc = 40;
        return new Terrain(terrainSize, terrainScale, minY, maxY,
                "/textures/heightmap.png",
                "/textures/terrain.png", textInc,
                "/textures/heightmap_city.png");
    }

    private CityBuildings prepareCityBuildings(Terrain terrain) throws Exception {
       return new CityBuildings("models/city2/The_city.obj", "/models/city2", terrain);
    }

    private City prepareCity() throws Exception {
        float terrainScale = 0.00008f;
        int terrainSize = 1;
        float minY = 0f;
        float maxY = 0.25f;
        int textInc = 40;
        return new City(terrainSize, terrainScale, minY, maxY,
                "/textures/heightmap_city.png",
                "/textures/terrain_city.png", textInc);
    }

    private Fog createFog() {
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        return new Fog(true, fogColour, 0.0075f);
    }

    private SkyBox createSkyBox(float skyBoxScale) throws Exception {
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        return skyBox;
    }

    private void setupCamera() {
        camera.setPosition(0, 0, 0);
        camera.setRotation(0, 0, (float) Math.toRadians(20));
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        if (scene.getPlane().onInput(window)) {
            sceneChanged = true;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        rotateCamera(mouseInput);
        // Update camera position
        scene.getPlane().update(scene.getTerrain(), CAMERA_POS_STEP);
        moveCamera(scene.getPlane().getCameraDistance());
        updateLight();

        // Update view matrix
        camera.updateViewMatrix();
    }

    private void rotateCamera(MouseInput mouseInput) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY,
                    rotVec.y * MOUSE_SENSITIVITY,
                    0);
            sceneChanged = true;
        }
    }

    private void moveCamera(float distance) {
        camera.followPlane(scene.getPlane(), distance);
    }

    private void updateLight() {
        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else {

            if (lightAngle > STRAIGHT_ANGLE) {
                lightAngle = STRAIGHT_ANGLE;
            }
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        scene.getSceneLight().getDirectionalLight()
                .setDirection(new Vector3f(0, yValue, zValue).normalize());
    }

    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
    }
}
