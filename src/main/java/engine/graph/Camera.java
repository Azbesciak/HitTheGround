package engine.graph;

import engine.Plane;
import engine.Utils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    
    private final Vector3f rotation;
    
    private Matrix4f viewMatrix;
    
    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        viewMatrix = new Matrix4f();
    }
    
    public Camera(Vector3f position, Vector3f rotation) {
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
        final Vector3f planePosition = plane.getPosition();

        final Vector3f planeRotation =
                Utils.deepCopy(plane.getRotation())
                        .rotateX(90)
                        .getEulerAnglesXYZ(new Vector3f());
        rotation.x = -(float)Math.toDegrees(planeRotation.x) + 20;
        rotation.y = -(float)Math.toDegrees(planeRotation.y);
        rotation.z = -(float)Math.toDegrees(planeRotation.z);

        position.x = planePosition.x;// - (float)Math.sin(planeRotation.y) * 0.2f;
        position.y = planePosition.y - (float)Math.sin(planeRotation.x) * 2;
        position.z = planePosition.z + (float)Math.cos(planeRotation.x) * 2;
    }

    public Vector3f getRotation() {
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