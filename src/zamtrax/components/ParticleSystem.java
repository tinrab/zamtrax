package zamtrax.components;

import org.lwjgl.BufferUtils;
import zamtrax.*;
import zamtrax.rendering.Filter;
import zamtrax.resources.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class ParticleSystem extends Component {

	private static final int ATTRIBUTE_POSITION = 0;
	private static final int ATTRIBUTE_COLOR = 1;
	private static final int ATTRIBUTE_MVP = 2;

	private static final int MAX_PARTICLES = 500000;
	private static final int INSTANCE_DATA_LENGTH = 4 + 16;

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

	private Color startColor, endColor;
	private float rotationSpeed;

	private FloatBuffer buffer;
	private int vao, staticVBO, streamVBO, ibo;

	private Filter filter;

	@Override
	public void onAdd() {
		super.onAdd();

		gravity = new Vector3(0.0f, 0.0f, 0.0f);
		particles = new ArrayList<>();

		sortMode = SortMode.NONE;
		byDistance = (a, b) -> a.distance < b.distance ? -1 : 1;
		youngestFirst = (a, b) -> a.age < b.age ? -1 : 1;
		oldestFirst = (a, b) -> a.age > b.age ? -1 : 1;

		startColor = Color.createWhite();
		endColor = Color.createWhite();

		shader = new Shader(Resources.loadText("shaders/particle.vs"), Resources.loadText("shaders/particle.fs"));
		shader.bindAttribute(ATTRIBUTE_POSITION, "position");
		shader.bindAttribute(ATTRIBUTE_COLOR, "color");
		shader.bindAttribute(ATTRIBUTE_MVP, "MVP");
		shader.link();

		{
			FloatBuffer staticBuffer = BufferUtils.createFloatBuffer(3 * 4);
			IntBuffer indexBuffer = BufferUtils.createIntBuffer(6);
			staticVBO = glGenBuffers();
			ibo = glGenBuffers();
			vao = glGenVertexArrays();

			glBindBuffer(GL_ARRAY_BUFFER, staticVBO);
			glBindVertexArray(vao);

			staticBuffer.put(new float[]{
					0.5f, 0.5f, 0.5f,
					-0.5f, 0.5f, 0.5f,
					0.5f, -0.5f, 0.5f,
					-0.5f, -0.5f, 0.5f
			});
			staticBuffer.flip();

			glBufferData(GL_ARRAY_BUFFER, staticBuffer, GL_STATIC_DRAW);

			glEnableVertexAttribArray(0);
			glVertexAttribPointer(ATTRIBUTE_POSITION, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

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

			glEnableVertexAttribArray(ATTRIBUTE_COLOR);
			glVertexAttribDivisor(ATTRIBUTE_COLOR, 1);
			glVertexAttribPointer(ATTRIBUTE_COLOR, 4, GL_FLOAT, false, INSTANCE_DATA_LENGTH * Float.BYTES, 0);

			glEnableVertexAttribArray(ATTRIBUTE_MVP + 0);
			glEnableVertexAttribArray(ATTRIBUTE_MVP + 1);
			glEnableVertexAttribArray(ATTRIBUTE_MVP + 2);
			glEnableVertexAttribArray(ATTRIBUTE_MVP + 3);
			glVertexAttribDivisor(ATTRIBUTE_MVP + 0, 1);
			glVertexAttribDivisor(ATTRIBUTE_MVP + 1, 1);
			glVertexAttribDivisor(ATTRIBUTE_MVP + 2, 1);
			glVertexAttribDivisor(ATTRIBUTE_MVP + 3, 1);
			glVertexAttribPointer(ATTRIBUTE_MVP + 0, 4, GL_FLOAT, false, INSTANCE_DATA_LENGTH * Float.BYTES, 4 * Float.BYTES);
			glVertexAttribPointer(ATTRIBUTE_MVP + 1, 4, GL_FLOAT, false, INSTANCE_DATA_LENGTH * Float.BYTES, 8 * Float.BYTES);
			glVertexAttribPointer(ATTRIBUTE_MVP + 2, 4, GL_FLOAT, false, INSTANCE_DATA_LENGTH * Float.BYTES, 12 * Float.BYTES);
			glVertexAttribPointer(ATTRIBUTE_MVP + 3, 4, GL_FLOAT, false, INSTANCE_DATA_LENGTH * Float.BYTES, 16 * Float.BYTES);
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

			{
				p.velocity.x += gravity.x * delta * p.gravity;
				p.velocity.y += gravity.y * delta * p.gravity;
				p.velocity.z += gravity.z * delta * p.gravity;

				p.position.x += p.velocity.x * delta;
				p.position.y += p.velocity.y * delta;
				p.position.z += p.velocity.z * delta;

				p.age += delta;
				p.rotation += rotationSpeed * delta;

				p.color.set(Color.lerp(startColor, endColor, p.age / p.lifetime));
			}

			if (p.age > p.lifetime) {
				particleIterator.remove();
			} else {
				p.distance = Vector3.sqrDistance(cameraPosition, p.position);
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

	public void emit(Particle particle) {
		particle.color.set(startColor);

		particles.add(particle);
	}

	private void updateVBO(Matrix4 view, Matrix4 projection) {
		buffer.clear();

		for (Particle p : particles) {
			buffer.put(p.color.r).put(p.color.g).put(p.color.b).put(p.color.a);
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

	public Color getStartColor() {
		return startColor;
	}

	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}

	public Color getEndColor() {
		return endColor;
	}

	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public static class Particle {

		private Vector3 position;
		private Vector3 velocity;
		private float gravity;
		private float lifetime;
		private float rotation;
		private float scale;
		private float age;
		private float distance;
		private Color color;

		public Particle() {
			position = new Vector3();
			velocity = new Vector3();
			color = Color.createWhite();
			gravity = 1.0f;
			scale = 1.0f;
		}

		public void setPosition(Vector3 position) {
			this.position = position;
		}

		public void setVelocity(Vector3 velocity) {
			this.velocity = velocity;
		}

		public void setLifetime(float lifetime) {
			this.lifetime = lifetime;
		}

		public void setScale(float scale) {
			this.scale = scale;
		}

		public void setRotation(float rotation) {
			this.rotation = rotation;
		}

		public void setGravity(float gravity) {
			this.gravity = gravity;
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
			Matrix4 scl = Matrix4.createScale(scale, scale, scale);

			return view.mul(model.mul(rot.mul(scl)));
		}

	}

}
