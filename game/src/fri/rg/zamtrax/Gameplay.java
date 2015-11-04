package fri.rg.zamtrax;

import zamtrax.*;
import zamtrax.components.Camera;
import zamtrax.components.MeshFilter;
import zamtrax.components.MeshRenderer;
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

			cameraObject.getTransform().translate(0.0f, 0.0f, -1.0f);
		}

		String[] texNames = {"brick_grey.png", "dirt.png", "leaves.png", "stone_diamond.png"};

		AttributeScheme attributeScheme = new AttributeScheme.Builder()
				.addPointer(AttributeType.POSITION, 0, "position")
				.addPointer(AttributeType.UV, 1, "uv")
				.addPointer(AttributeType.NORMAL, 2, "normal")
				.build();

		Shader shader = new Shader.Builder()
				.setVertexShaderSource(Resources.loadText("shaders/textured.vs"))
				.setFragmentShaderSource(Resources.loadText("shaders/textured.fs"))
				.setAttributeScheme(attributeScheme)
				.addUniform("mvp")
				.build();

		Model cubeModel = Resources.loadModel("models/cube.obj");
		Mesh mesh = Mesh.Factory.fromModel(cubeModel, attributeScheme);

		Material[] materials = new Material[texNames.length];

		for (int i = 0; i < texNames.length; i++) {
			materials[i] = new Material.Builder()
					.setShader(shader)
					.setTexture(Resources.loadTexture("textures/" + texNames[i],
							Texture.Format.ARGB,
							Texture.WrapMode.REPEAT,
							Texture.FilterMode.LINEAR))
					.build();
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					SceneObject test = SceneObject.create();

					MeshFilter mf = test.addComponent(MeshFilter.class);
					MeshRenderer mr = test.addComponent(MeshRenderer.class);

					mr.setMaterial(materials[Random.randomInteger(materials.length)]);
					mf.setMesh(mesh);

					Rotate r = test.addComponent(Rotate.class);
					r.setAxis(Random.direction());

					test.getTransform().setPosition(new Vector3(i, j, k).mul(3.0f));
				}
			}
		}
	}

	@Override
	public void onExit() {

	}

}

