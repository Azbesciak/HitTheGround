package engine;

import engine.items.GameItem;
import engine.items.Terrain;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class Plane extends GameItem {

	private float speed;
	private final float MAX_SPEED = 1;
	private Vector3f positionOffset;
	private Vector3f rotationOffset;
	public Vector3f positionDegrees;

	public Vector3f rotationCorrection;

	public int angle = 10;
	public Plane(String objModel, String textureFile, String normalFile) throws Exception {
		super(objModel, textureFile, normalFile);
		positionOffset = new Vector3f();
		rotationOffset = new Vector3f();
		positionDegrees = new Vector3f();
		rotationCorrection = new Vector3f((float) Math.toRadians(0), 0 ,0);
		scale = 0.1f;
		getRotation().rotateX((float)Math.toRadians(-90));
	}

	public boolean onInput(Window window) {
		boolean positionChanged = increaseOffset(window);
		boolean rotationChanged = rotate(window);
		return positionChanged || rotationChanged;
	}

	private boolean rotate(Window window) {
		boolean sceneChanged = false;
		if (window.isKeyPressed(GLFW_KEY_LEFT)) {
			sceneChanged = true;
			rotationOffset.y -= 0.1f;
//			rotationCorrection.x -= 0.05f;
//			rotationCorrection.y += 0.05f;
//			rotationCorrection.z += 0.05f;
		} else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
			sceneChanged = true;
			rotationOffset.y += 0.1f;
//			rotationCorrection.x += 0.05f;
//			rotationCorrection.y -= 0.05f;
//			rotationCorrection.z -= 0.05f;
		}
		if (window.isKeyPressed(GLFW_KEY_UP)) {
			sceneChanged = true;
			rotationOffset.x += 0.05f;
//			rotationCorrection.y += 0.05f;
//			rotationCorrection.z += 0.05f;
		} else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
			sceneChanged = true;
			rotationOffset.x -= 0.1f;
//			rotationCorrection.y -= 0.05f;
//			rotationCorrection.z -= 0.05f;
		}
		rotationCorrection.x = keepInBorder(rotationCorrection.x);
		rotationCorrection.y = keepInBorder(rotationCorrection.y);
		rotationCorrection.z = keepInBorder(rotationCorrection.z);

		if (window.isKeyPressed(GLFW_KEY_1)) {
			sceneChanged = true;
			rotationCorrection.x += 0.01f;
		} else if (window.isKeyPressed(GLFW_KEY_2)) {
			sceneChanged = true;
			rotationCorrection.x -= 0.01f;
		}
		if (window.isKeyPressed(GLFW_KEY_3)) {
			sceneChanged = true;
			rotationCorrection.y += 0.01f;
		} else if (window.isKeyPressed(GLFW_KEY_4)) {
			sceneChanged = true;
			rotationCorrection.y -= 0.01f;
		}
		if (window.isKeyPressed(GLFW_KEY_5)) {
			sceneChanged = true;
			rotationCorrection.z += 0.01f;
		} else if (window.isKeyPressed(GLFW_KEY_6)) {
			sceneChanged = true;
			rotationCorrection.z -= 0.01f;
		}

		return sceneChanged;
	}

	private float keepInBorder(float value) {
		if (value > 1) {
			return 1;
		} else if (value < -1) {
			return -1;
		} else {
			return value;
		}
	}

	private boolean increaseOffset(Window window) {
		boolean sceneChanged = false;
		if (window.isKeyPressed(GLFW_KEY_W)) {
			sceneChanged = true;
			positionOffset.z -= 1;
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			sceneChanged = true;
			positionOffset.z += 1;
		}
		if (window.isKeyPressed(GLFW_KEY_A)) {
			sceneChanged = true;
			positionOffset.x -= 1;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			sceneChanged = true;
			positionOffset.x += 1;
		}
		if (window.isKeyPressed(GLFW_KEY_Z)) {
			sceneChanged = true;
			positionOffset.y -= 1;
		} else if (window.isKeyPressed(GLFW_KEY_X)) {
			sceneChanged = true;
			positionOffset.y += 1;
		}

		if (window.isKeyPressed(GLFW_KEY_Q)) {
			sceneChanged = true;
			angle -= 1;
		} else if (window.isKeyPressed(GLFW_KEY_E)) {
			sceneChanged = true;
			angle += 1;
		}
		return sceneChanged;
	}

	public void update(Terrain terrain, float sensitivity) {
		updatePosition(terrain, sensitivity);
		updateRotation(terrain, sensitivity);
	}

	private void updatePosition(Terrain terrain, float sensitivity) {
		positionOffset.mul(sensitivity);
		position.add(positionOffset.rotate(Utils.deepCopy(rotation).rotateX(90)));
		final float height = terrain.getHeight(position) + 2;
		if (position.y < height) {
			position.y = height;
		}
		positionOffset.zero();
	}

	private void updateRotation(Terrain terrain, float sensitivity) {
		rotation.rotate(
				rotationOffset.x,
				rotationOffset.y,
				rotationOffset.z
		);
		rotationOffset.zero();
	}

}
