package engine.graph;

import org.lwjgl.system.MemoryUtil;
import engine.items.GameItem;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

	public static final int MAX_WEIGHTS = 4;

	protected final int vaoId;

	protected final List<Integer> vboIdList;

	private final int vertexCount;

	private Material material;

	private float boundingRadius;

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		this(positions, textCoords, normals, indices, createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0),
			 createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0));
	}

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices,
				float[] weights) {
		vertexCount = indices.length;
		vboIdList = new ArrayList<>();
		boundingRadius = 0.8f;
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		prepareVBOS(positions, textCoords, normals, indices, jointIndices, weights);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	private void prepareVBOS(float[] positions, float[] textCoords, float[] normals,
							 int[] indices, int[] jointIndices, float[] weights) {
		prepareVBO(positions, 0, 3);
		prepareVBO(textCoords, 1, 2);
		prepareVBO(normals, 2, 3);
		prepareVBO(weights, 3, 4);
		prepareVBO(jointIndices, 4, 4);
		prepareVBO(indices);

	}

	private void prepareVBO(float[] data, int index, int size) {
		FloatBuffer buffer = null;
		try {
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			buffer = MemoryUtil.memAllocFloat(data.length);
			buffer.put(data).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
			glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
		} finally {
			if (buffer != null) {
				MemoryUtil.memFree(buffer);
			}
		}
	}

	private void prepareVBO(int[] data, int index, int size) {
		IntBuffer buffer = null;
		try {
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			buffer = MemoryUtil.memAllocInt(data.length);
			buffer.put(data).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
			glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
		} finally {
			if (buffer != null) {
				MemoryUtil.memFree(buffer);
			}
		}
	}

	private void prepareVBO(int[] data) {
		IntBuffer buffer = null;
		try {
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			buffer = MemoryUtil.memAllocInt(data.length);
			buffer.put(data).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		} finally {
			if (buffer != null) {
				MemoryUtil.memFree(buffer);
			}
		}
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public final int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float getBoundingRadius() {
		return boundingRadius;
	}

	public void setBoundingRadius(float boundingRadius) {
		this.boundingRadius = boundingRadius;
	}

	protected void initRender() {
		Texture texture = material != null ? material.getTexture() : null;
		if (texture != null) {
			// Activate first texture bank
			glActiveTexture(GL_TEXTURE0);
			// Bind the texture
			glBindTexture(GL_TEXTURE_2D, texture.getId());
		}
		Texture normalMap = material != null ? material.getNormalMap() : null;
		if (normalMap != null) {
			// Activate second texture bank
			glActiveTexture(GL_TEXTURE1);
			// Bind the texture
			glBindTexture(GL_TEXTURE_2D, normalMap.getId());
		}

		// Draw the mesh
		glBindVertexArray(getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
//		glEnableVertexAttribArray(5);
	}

	protected void endRender() {
		// Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
//		glDisableVertexAttribArray(5);
		glBindVertexArray(0);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void render() {
		initRender();

		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

		endRender();
	}

	public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
		initRender();

		for (GameItem gameItem : gameItems) {
			if (gameItem.isInsideFrustum()) {
				// Set up data requiered by gameItem
				consumer.accept(gameItem);
				// Render this game item
				glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
			}
		}

		endRender();
	}

	public void cleanUp() {
		glDisableVertexAttribArray(0);

		// Delete the VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for (int vboId : vboIdList) {
			glDeleteBuffers(vboId);
		}

		// Delete the texture
		Texture texture = material.getTexture();
		if (texture != null) {
			texture.cleanup();
		}

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}

	public void deleteBuffers() {
		glDisableVertexAttribArray(0);

		// Delete the VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for (int vboId : vboIdList) {
			glDeleteBuffers(vboId);
		}

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}

	protected static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	protected static int[] createEmptyIntArray(int length, int defaultValue) {
		int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

}
