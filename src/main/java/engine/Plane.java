package engine;

import engine.items.GameItem;
import engine.items.Terrain;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class Plane extends GameItem {

	private float speed;
	private final float MAX_SPEED = 1;
	private Vector3f positionOffset;
	private Vector3f rotationOffset;
	private Vector3f positionDegrees;

	public Plane(String objModel, String textureFile, String normalFile) throws Exception {
		super(objModel, textureFile, normalFile);
		positionOffset = new Vector3f();
		rotationOffset = new Vector3f();
		positionDegrees = new Vector3f();
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
		} else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
			sceneChanged = true;
			rotationOffset.y += 0.1f;
		}
		if (window.isKeyPressed(GLFW_KEY_UP)) {
			sceneChanged = true;
			rotationOffset.x += 0.1f;
		} else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
			sceneChanged = true;
			rotationOffset.x -= 0.1f;
		}
		return sceneChanged;
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
		return sceneChanged;
	}

	public void update(Terrain terrain, float sensitivity) {
		updatePosition(terrain, sensitivity);
		updateRotation(terrain, sensitivity);
	}

	private void updatePosition(Terrain terrain, float sensitivity) {
		positionOffset.mul(sensitivity);
		position.add(positionOffset.rotate(Utils.deepCopy(rotation).rotateX(90)));
//		position.set(Utils.updatePosition(rotation, position, positionOffset));
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
