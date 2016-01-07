package zamtrax.components;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLUtil;
import zamtrax.*;
import zamtrax.resources.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ParticleSystem extends Component {

	private static final int ATTRIBUTE_POSITION = 0;
	private static final int ATTRIBUTE_COLOR = 1;
	private static final int ATTRIBUTE_MVP = 2;

	private static final int MAX_PARTICLES = 500000;
	private static final int INSTANCE_DATA_LENGTH = 16;

	public enum SortMode {
		NONE, BY_DISTANCE, YOUNGEST_FIRST, OLDEST_FIRST
	}

	public enum BlendMode {
		ALPHA, ADDITIVE, MULTIPLY
	}

	private Shader shader;
	private Vector3 gravity;
	private List<Particle> particles;
	private SortMode sortMode;
	private Comparator<Particle> byDistance, youngestFirst, oldestFirst;
	private BlendMode blendMode;

	private FloatBuffer buffer;
	private int vao, staticVBO, streamVBO, ibo;

	@Override
	public void onAdd() {
		super.onAdd();

		gravity = new Vector3(0.0f, 0.0f, 0.0f);
		particles = new ArrayList<>();
		sortMode = SortMode.NONE;

		byDistance = (a, b) -> a.distance < b.distance ? -1 : 1;
		youngestFirst = (a, b) -> a.age < b.age ? -1 : 1;
		oldestFirst = (a, b) -> a.age > b.age ? -1 : 1;

		shader = new Shader(Resources.loadText("shaders/particle.vs"), Resources.loadText("shaders/particle.fs"));
		shader.bindAttribute(ATTRIBUTE_POSITION, "position");
		shader.bindAttribute(ATTRIBUTE_COLOR, "color");
		shader.bindAttribute(ATTRIBUTE_MVP, "MVP");
		shader.link();

		{
			FloatBuffer staticBuffer = BufferUtils.createFloatBuffer(MAX_PARTICLES * INSTANCE_DATA_LENGTH);
			IntBuffer indexBuffer = BufferUtils.createIntBuffer(6);
			staticVBO = glGenBuffers();
			ibo = glGenBuffers();
			vao = glGenVertexArrays();

			glBindBuffer(GL_ARRAY_BUFFER, staticVBO);
			glBindVertexArray(vao);

			staticBuffer.put(new float[]{
					//  X     Y   Z    R     G     B
					0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, // vertex 0
					-0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, // vertex 1
					0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1.0f, // vertex 2
					-0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1.0f, // vertex 3
			});
			staticBuffer.flip();

			glBufferData(GL_ARRAY_BUFFER, staticBuffer, GL_STATIC_DRAW);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);

			glVertexAttribPointer(ATTRIBUTE_POSITION, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
			glVertexAttribPointer(ATTRIBUTE_COLOR, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

			indexBuffer.put(new int[]{
					0, 1, 2,
					2, 1, 3
			});
			indexBuffer.flip();

			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		}

		{
			streamVBO = glGenBuffers();
			buffer = BufferUtils.createFloatBuffer(MAX_PARTICLES * INSTANCE_DATA_LENGTH);

			glBindBuffer(GL_ARRAY_BUFFER, streamVBO);

			glBufferData(GL_ARRAY_BUFFER, MAX_PARTICLES * INSTANCE_DATA_LENGTH * Float.BYTES, GL_STREAM_DRAW);

			for (int i = 0; i < 4; i++) {
				glEnableVertexAttribArray(ATTRIBUTE_MVP + i);
				glVertexAttribDivisor(ATTRIBUTE_MVP + i, 1);
				glVertexAttribPointer(ATTRIBUTE_MVP + i, 4, GL_FLOAT, false, 16 * Float.BYTES, i * 4 * Float.BYTES);
			}
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	@Override
	public void update(float delta) {
		Iterator<Particle> particleIterator = particles.iterator();

		Vector3 cameraPosition = Camera.getMainCamera().getTransform().getPosition();

		while (particleIterator.hasNext()) {
			Particle p = particleIterator.next();

			if (p.update(delta)) {
				particleIterator.remove();
			} else {
				p.distance = Vector3.sqrDistance(cameraPosition, p.getPosition());
			}
		}

		switch (sortMode) {
			case BY_DISTANCE:
				particles.sort(byDistance);
				break;
			case OLDEST_FIRST:
				particles.sort(oldestFirst);
				break;
			case YOUNGEST_FIRST:
				particles.sort(youngestFirst);
				break;
		}
	}

	public void emit(Vector3 position) {
		particles.add(new Particle(this, position, Random.direction().mul(5.0f), 0, 10, 10, 0.1f));
	}

	private void updateVBO(Matrix4 view, Matrix4 projection) {
		buffer.clear();

		for (Particle p : particles) {
			projection.mul(p.getModelView(view)).toBuffer(buffer);
		}

		buffer.flip();

		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
	}

	public void render(Matrix4 view, Matrix4 projection) {
		if (particles.size() != 0) {
			shader.bind();

			glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, streamVBO);

			updateVBO(view, projection);

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

			glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, particles.size());

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindVertexArray(0);

			shader.release();
		}
	}

	@Override
	public void onRemove() {
		shader.dispose();
	}

	public Vector3 getGravity() {
		return gravity;
	}

	public void setGravity(Vector3 gravity) {
		this.gravity = gravity;
	}

	public Shader getShader() {
		return shader;
	}

	public List<Particle> getParticles() {
		return particles;
	}

	public BlendMode getBlendMode() {
		return blendMode;
	}

	public void setBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public SortMode getSortMode() {
		return sortMode;
	}

	public void setSortMode(SortMode sortMode) {
		this.sortMode = sortMode;
	}

	public static class Particle {

		private ParticleSystem particleSystem;
		private Vector3 position;
		private Vector3 velocity;
		private float gravity;
		private float lifetime;
		private float rotation;
		private float scale;
		private float age;
		private Color color;
		private float distance;

		public Particle(ParticleSystem particleSystem, Vector3 position, Vector3 velocity, float gravity, float lifetime, float rotation, float scale) {
			this.particleSystem = particleSystem;
			this.position = position;
			this.velocity = velocity;
			this.gravity = gravity;
			this.lifetime = lifetime;
			this.rotation = rotation;
			this.scale = scale;
			color = new Color();
		}

		public Vector3 getPosition() {
			return position;
		}

		public Vector3 getVelocity() {
			return velocity;
		}

		public float getGravity() {
			return gravity;
		}

		public float getLifetime() {
			return lifetime;
		}

		public float getRotation() {
			return rotation;
		}

		public float getScale() {
			return scale;
		}

		public float getAge() {
			return age;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public boolean update(float delta) {
			Vector3 g = particleSystem.getGravity();

			velocity.x += g.x * delta * gravity;
			velocity.y += g.y * delta * gravity;
			velocity.z += g.z * delta * gravity;

			position.x += velocity.x * delta;
			position.y += velocity.y * delta;
			position.z += velocity.z * delta;

			age += delta;

			return age > lifetime;
		}

		public Matrix4 getModelView(Matrix4 view) {
			Matrix4 model = Matrix4.createTranslation(position);

			model.set(0, 0, view.get(0, 0));
			model.set(0, 1, view.get(1, 0));
			model.set(0, 2, view.get(2, 0));

			model.set(1, 0, view.get(0, 1));
			model.set(1, 1, view.get(1, 1));
			model.set(1, 2, view.get(2, 1));

			model.set(2, 0, view.get(0, 2));
			model.set(2, 1, view.get(1, 2));
			model.set(2, 2, view.get(2, 2));

			Matrix4 rot = Quaternion.fromEuler(0, 0, rotation).toMatrix();

			return view.mul(model.mul(rot.mul(Matrix4.createScale(scale, scale, scale))));
		}

	}

}
