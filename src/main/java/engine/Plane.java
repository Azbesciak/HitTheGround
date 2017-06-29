package engine;

import engine.items.GameItem;
import engine.items.Terrain;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class Plane extends GameItem {

	private static final double MAX_SPEED = 15;
	private static final double ACCELERATION = 0.01;
	private static final double RESISTANCE = 0.0001;
	private static final double GRAVITY = 0.01;
	private static final int NEGATIVE_RIGHT_ANGLE = -90;
	public static final int CRASH_LIMIT_ANGLE = -30;
	public static final float TILT_ANGLE_DIFFERENCE = 0.05f;

	private double speedDirectionAngle = NEGATIVE_RIGHT_ANGLE;
	private Vector3f positionOffset;
	private Vector3f rotationOffset;

	private boolean isInAir = false;

	private long recentTime;
	private long difference;

	public Plane(String objModel, String textureFile, String normalFile) throws Exception {
		super(objModel, textureFile, normalFile);
		positionOffset = new Vector3f();
		rotationOffset = new Vector3f();
		scale = 0.1f;
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
		rotationOffset.mul((float)Math.cos(Math.toRadians(speedDirectionAngle)));
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
		updatePosition(terrain, sensitivity);
		updateRotation(terrain, sensitivity);
	}

	private void updatePosition(Terrain terrain, float sensitivity) {
		positionOffset.mul(sensitivity);
		position.add(positionOffset.rotate(Utils.deepCopy(rotation).rotateX(90)));
		final float height = terrain.getHeight(position) + 2;
		position.y += GRAVITY * difference * Math.sin(Math.toRadians(speedDirectionAngle));
		if (position.y <= height) {
			isInAir = false;
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

}
