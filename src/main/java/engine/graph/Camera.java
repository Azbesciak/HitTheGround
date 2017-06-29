package engine.graph;

import engine.Plane;
import org.joml.Matrix4f;
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

    public void followPlane(Plane plane, Vector3f distance) {
        final Vector3f planePosition = plane.getPosition();
        position.x = planePosition.x + distance.x;
        position.y = planePosition.y + distance.y;
        position.z = planePosition.z + distance.z;
    }

//    public void updatePosition(float offsetX, float offsetY, float offsetZ, Terrain terrain) {
//        if ( offsetZ != 0 ) {
//            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
//            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
//        }
//        if ( offsetX != 0) {
//            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
//            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
//        }
//        position.y += offsetY;
//        final float height = terrain.getHeight(position) + 2;
//        if (position.y < height) {
//            position.y = height;
//        }
//    }

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