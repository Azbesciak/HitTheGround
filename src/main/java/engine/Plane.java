package engine;

import engine.items.GameItem;
import engine.items.Terrain;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class Plane extends GameItem {

	private static final float WORLD_SCALE = 0.001f;
	private static final double RESISTANCE = 0.0001;
	private static final double ACCELERATION = 0.01;
	private static final double MAX_SPEED = 10000 * WORLD_SCALE;
	private static final double GRAVITY = 10 * WORLD_SCALE;
	private static final float ADDITIONAL_DISTANCE = 4f * WORLD_SCALE;
	private static final float BASIC_DISTANCE = 20 * WORLD_SCALE;

	private static final int NEGATIVE_RIGHT_ANGLE = -90;
	private static final int CRASH_LIMIT_ANGLE = -30;
	private static final float TILT_ANGLE_DIFFERENCE = 0.05f;

	private double speedDirectionAngle = NEGATIVE_RIGHT_ANGLE;
	private Vector3f positionOffset;
	private Vector3f rotationOffset;

	private boolean isInAir = true;
	private boolean wasCrash = false;

	private long recentTime;
	private long difference;

	public Plane(String objModel, String textureFile, String normalFile) throws Exception {
		super(objModel, textureFile, normalFile);
		positionOffset = new Vector3f();
		rotationOffset = new Vector3f();
		scale = WORLD_SCALE;
		recentTime = System.currentTimeMillis();
		stabilizePlaneModel();
	}

	public boolean onInput(Window window) {
		increaseOffset(window);
		rotate(window);
		return true;
	}

	private void rotate(Window window) {
		if (isInAir) {
			if (window.isKeyPressed(GLFW_KEY_LEFT)) {
				rotationOffset.y -= TILT_ANGLE_DIFFERENCE;
			} else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
				rotationOffset.y += TILT_ANGLE_DIFFERENCE;
			}
		}
		if (window.isKeyPressed(GLFW_KEY_UP)) {
			rotationOffset.x += TILT_ANGLE_DIFFERENCE;
		} else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
			rotationOffset.x -= TILT_ANGLE_DIFFERENCE;
		}

		if (window.isKeyPressed(GLFW_KEY_Q)) {
			rotationOffset.z += TILT_ANGLE_DIFFERENCE;
		} else if (window.isKeyPressed(GLFW_KEY_E)) {
			rotationOffset.z -= TILT_ANGLE_DIFFERENCE;
		}

//		rotationOffset.mul((float) Math.cos(Math.toRadians(speedDirectionAngle)));
	}

	private boolean increaseOffset(Window window) {
		difference = System.currentTimeMillis() - recentTime;
		if (window.isKeyPressed(GLFW_KEY_W)) {
			speedDirectionAngle = Math.min(speedDirectionAngle + difference * ACCELERATION, 0);
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			speedDirectionAngle = Math.max(speedDirectionAngle - difference * ACCELERATION, NEGATIVE_RIGHT_ANGLE);
		}
		speedDirectionAngle = Math.max(speedDirectionAngle - difference * RESISTANCE, NEGATIVE_RIGHT_ANGLE);
		positionOffset.z -= Math.cos(Math.toRadians(speedDirectionAngle)) * MAX_SPEED;
		if (window.isKeyPressed(GLFW_KEY_A)) {
			positionOffset.x -= 1;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			positionOffset.x += 1;
		}
		if (window.isKeyPressed(GLFW_KEY_Z)) {
			positionOffset.y -= 1;
		} else if (window.isKeyPressed(GLFW_KEY_X)) {
			positionOffset.y += 1;
		}
		recentTime = System.currentTimeMillis();
		return true;
	}

	public void update(Terrain terrain, float sensitivity) {
		updateRotation(terrain, sensitivity);
		updatePosition(terrain, sensitivity);
	}

	private void updatePosition(Terrain terrain, float sensitivity) {
		positionOffset.mul(sensitivity);
		final Quaternionf planeRealRotation = Utils.deepCopy(rotation).rotateX(90);
		position.add(positionOffset.rotate(planeRealRotation));
		final float height = terrain.getHeight(position) + 2;
		position.y += GRAVITY * difference * Math.sin(Math.toRadians(speedDirectionAngle));
		if (position.y <= height) {
//			isInAir = false;
			if (speedDirectionAngle > CRASH_LIMIT_ANGLE) {
				speedDirectionAngle = NEGATIVE_RIGHT_ANGLE;
				rotation.set(new Quaternionf());
				stabilizePlaneModel();
			}
			position.y = height;
		} else {
			isInAir = true;
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

	private void stabilizePlaneModel() {
		rotation.rotateX((float) Math.toRadians(-90));
	}

	public float getCameraDistance() {
		return BASIC_DISTANCE + ADDITIONAL_DISTANCE *
				(float)Math.cos(Math.toRadians(speedDirectionAngle));
	}
}
