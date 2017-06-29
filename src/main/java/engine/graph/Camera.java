package engine.graph;

import engine.Plane;
import engine.Utils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {

    public static final int TILT_OF_CAMERA = 15;
    private final Vector3f position;

    private final Quaternionf rotation;
    
    private Matrix4f viewMatrix;
    
    public Camera() {
        position = new Vector3f();
        rotation = new Quaternionf();
        viewMatrix = new Matrix4f();
    }
    
    public Camera(Vector3f position, Quaternionf rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    
    public Matrix4f updateViewMatrix() {
        return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
    }

    public void followPlane(Plane plane, float distance) {
        final Vector3f planePosition = Utils.deepCopy(plane.getPosition());
		final Quaternionf planeRotation = Utils.deepCopy(plane.getRotation()).rotateX((float)Math.toRadians(90));


		rotation.set(planeRotation.conjugate());
        final Vector3f cameraUponPlane = new Vector3f(
                0,
                (float) Math.sin(Math.toRadians(TILT_OF_CAMERA)) * distance,
                (float) Math.cos(Math.toRadians(TILT_OF_CAMERA)) * distance);

        position.set(cameraUponPlane
				.rotate(planeRotation.conjugate()).add(planePosition));
	}

    public Quaternionf getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}