package game;

import engine.*;
import engine.graph.*;
import engine.graph.lights.DirectionalLight;
import engine.graph.weather.Fog;
import engine.items.SkyBox;
import engine.items.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class PlaneGame implements IGameLogic {

	private static final float MOUSE_SENSITIVITY = 0.2f;
	private static final float CAMERA_POS_STEP = 0.10f;
	private static final int STRAIGHT_ANGLE = 180;

	private final Vector3f cameraInc;
	private final Renderer renderer;
	private final Camera camera;
	private Scene scene;
	private float angleInc;
	private float lightAngle;
	private boolean firstTime;

	private boolean sceneChanged;

	PlaneGame() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		angleInc = 0;
		lightAngle = 90;
		firstTime = true;
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
		scene = new Scene();
		float skyBoxScale = 75.0f;

		scene.setTerrain(prepareTerrain());
		scene.setPlane(createPlane());
		scene.setRenderShadows(true);
		scene.setFog(createFog());
		scene.setSkyBox(createSkyBox(skyBoxScale));
		setupLights();
		setupCamera();
	}

	private Plane createPlane() throws Exception {
		return new Plane("/models/FA-22_Raptor.obj",
						 "/textures/FA-22_Raptor_P01.png",
						 "/textures/FA-22_Raptor_N.png");
	}

	private Terrain prepareTerrain() throws Exception {
		float terrainScale = 75;
		int terrainSize = 5;
		float minY = 0f;
		float maxY = 0.25f;
		int textInc = 40;
		return new Terrain(terrainSize, terrainScale, minY, maxY,
						   "/textures/heightmap.png",
						   "/textures/terrain.png", textInc);
	}

	private Fog createFog() {
		Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
		return new Fog(true, fogColour, 0.02f);
	}

	private SkyBox createSkyBox(float skyBoxScale) throws Exception {
		SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
		skyBox.setScale(skyBoxScale);
		return skyBox;
	}

	private void setupCamera() {
		camera.setPosition(0,0,0);
		camera.setRotation(0,0,0);
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
		sceneChanged = false;
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW_KEY_W)) {
			sceneChanged = true;
			cameraInc.z = -1;
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			sceneChanged = true;
			cameraInc.z = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_A)) {
			sceneChanged = true;
			cameraInc.x = -1;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			sceneChanged = true;
			cameraInc.x = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_Z)) {
			sceneChanged = true;
			cameraInc.y = -1;
		} else if (window.isKeyPressed(GLFW_KEY_X)) {
			sceneChanged = true;
			cameraInc.y = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_LEFT)) {
			sceneChanged = true;
			angleInc -= 0.05f;
		} else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
			sceneChanged = true;
			angleInc += 0.05f;
		} else {
			angleInc = 0;
		}
	}

	@Override
	public void update(float interval, MouseInput mouseInput, Window window) {
		rotateCamera(mouseInput);
		// Update camera position
		moveCamera();
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

	private void moveCamera() {
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
							cameraInc.y * CAMERA_POS_STEP,
							cameraInc.z * CAMERA_POS_STEP,
							scene.getTerrain());
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
