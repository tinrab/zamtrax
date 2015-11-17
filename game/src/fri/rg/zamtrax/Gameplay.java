package fri.rg.zamtrax;

import zamtrax.*;
import zamtrax.resources.*;

public class Gameplay extends Scene {

	@Override
	public void onEnter() {
		{
			// FPS
			SceneObject cameraObject = SceneObject.create();

			FPSController fps = cameraObject.addComponent(FPSController.class);
			Camera camera = cameraObject.addComponent(Camera.class);
			camera.setProjection(Matrix4.createPerspective(60.0f, Game.getScreenWidth() / (float) Game.getScreenHeight(), 0.01f, 500.0f));

			Transform t = cameraObject.getTransform();

			t.translate(0.0f, 4.0f, -10.0f);
			t.rotate(Mathf.PI / 10.0f, 0.0f, 0.0f);
		}

		BindingInfo bindingInfo = new BindingInfo.Builder()
				.bind(AttributeType.POSITION, 0, "position")
				.bind(AttributeType.UV, 1, "uv")
				.bind(AttributeType.NORMAL, 2, "normal")
				.build();

		Shader shader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("shaders/textured.vs"))
				.setFragmentShaderSource(Resources.loadText("shaders/textured.fs"))
				.setBindingInfo(bindingInfo)
				.addUniform("projectionMatrix")
				.addUniform("modelViewMatrix")
				//.addUniform("normalMatrix")
				.addUniform("ambientColor")
				.addUniform("lightingDirection")
				.addUniform("directionalColor")
				.build();

		Model cubeModel = Resources.loadModel("models/cube.obj");
		Mesh mesh = Mesh.Factory.fromModel(cubeModel, bindingInfo);
		Material dirtMaterial = new Material.Builder()
				.setShader(shader)
				.setTexture(Resources.loadTexture("textures/dirt.png",
						Texture.Format.ARGB,
						Texture.WrapMode.REPEAT,
						Texture.FilterMode.LINEAR))
				.build();
		Material brickMaterial = new Material.Builder()
				.setShader(shader)
				.setTexture(Resources.loadTexture("textures/brick_grey.png",
						Texture.Format.ARGB,
						Texture.WrapMode.REPEAT,
						Texture.FilterMode.LINEAR))
				.build();
		Material diamondMaterial = new Material.Builder()
				.setShader(shader)
				.setTexture(Resources.loadTexture("textures/stone_diamond.png",
						Texture.Format.ARGB,
						Texture.WrapMode.REPEAT,
						Texture.FilterMode.LINEAR))
				.build();

		SceneObject floor = SceneObject.create();

		MeshFilter mf = floor.addComponent(MeshFilter.class);
		MeshRenderer mr = floor.addComponent(MeshRenderer.class);

		mr.setMaterial(dirtMaterial);
		mf.setMesh(mesh);

		floor.getTransform().setScale(new Vector3(8.0f, 0.25f, 8.0f));

		floor.addComponent(BoxCollider.class);
		floor.addComponent(RigidBody.class).setMass(0.0f);

		floor.addComponent(ObjectSpawner.class).setResources(mesh, brickMaterial);

		Mesh longCube = Mesh.Factory.fromModel(Resources.loadModel("models/tall_cube.obj"), bindingInfo);

		SceneObject spinner = SceneObject.create();
		spinner.addComponent(MeshFilter.class).setMesh(longCube);
		spinner.addComponent(MeshRenderer.class).setMaterial(diamondMaterial);
		spinner.getTransform().translate(4.0f, 1.0f, 0.0f);
		spinner.addComponent(BoxCollider.class).setSize(16.0f, 1.0f, 1.0f);
		spinner.addComponent(RigidBody.class).setMass(0.0f);

		Rotate r = spinner.addComponent(Rotate.class);
		r.setAxis(Vector3.UP);
		r.setSpeed(-1.0f);
	}

	@Override
	public void onExit() {

	}

}

