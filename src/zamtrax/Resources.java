package zamtrax;

import org.lwjgl.BufferUtils;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.BMFontParser;
import zamtrax.resources.Model;
import zamtrax.resources.Texture;

import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class Resources {

	private static ClassLoader classLoader;

	static void init(ClassLoader classLoader) {
		Resources.classLoader = classLoader;
	}

	public static String loadText(String pathname) {
		return loadText(pathname, classLoader);
	}

	public static String loadText(String pathname, ClassLoader classLoader) {
		pathname = getResourcePath(pathname, classLoader);

		try (FileReader fr = new FileReader(new File(pathname))) {

			StringBuilder sb = new StringBuilder();
			int ch;

			while ((ch = fr.read()) != -1) {
				sb.append((char) ch);
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Model loadModel(String pathname) {
		pathname = getResourcePath(pathname, classLoader);

		List<Vector3> normals = new ArrayList<>();
		List<Vector2> texCoords = new ArrayList<>();
		List<Vector3> positions = new ArrayList<>();

		List<Vertex> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(pathname))) {
			String line;

			while ((line = br.readLine()) != null) {
				String[] values = line.trim().split(" +");

				if (values[0].equals("v")) {
					float x = Float.valueOf(values[1]);
					float y = Float.valueOf(values[2]);
					float z = Float.valueOf(values[3]);

					positions.add(new Vector3(x, y, z));
				} else if (values[0].equals("vt")) {
					float u = Float.valueOf(values[1]);
					float v = Float.valueOf(values[2]);

					texCoords.add(new Vector2(u, v));
				} else if (values[0].equals("vn")) {
					float x = Float.valueOf(values[1]);
					float y = Float.valueOf(values[2]);
					float z = Float.valueOf(values[3]);

					normals.add(new Vector3(x, y, z));
				} else if (values[0].equals("f")) {
					for (int i = 1; i <= 3; i++) {
						String[] v = values[i].split("/");

						int vp = Integer.valueOf(v[0]) - 1;
						Vector3 position = positions.get(vp);
						Vertex vertex = new Vertex();

						vertex.position = new Vector3(position);

						if (!v[1].isEmpty()) {
							Vector2 uv = texCoords.get(Integer.valueOf(v[1]) - 1);
							vertex.uv = new Vector2(uv.x, 1.0f - uv.y);
						}

						if (!v[2].isEmpty()) {
							Vector3 normal = normals.get(Integer.valueOf(v[2]) - 1);
							vertex.normal = new Vector3(normal);
						}

						vertices.add(vertex);
						indices.add(indices.size());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return new Model(vertices, indices);
	}

	public static Texture loadTexture(String pathname, Texture.Format format, Texture.WrapMode wrapMode, Texture.FilterMode filterMode) {
		pathname = getResourcePath(pathname, classLoader);

		try {
			BufferedImage image = ImageIO.read(new File(pathname));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * 4);
			boolean hasAlpha = image.getColorModel().hasAlpha();

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int rgb = pixels[y * image.getWidth() + x];

					buffer.put((byte) ((rgb >> 16) & 0xFF));
					buffer.put((byte) ((rgb >> 8) & 0xFF));
					buffer.put((byte) ((rgb) & 0xFF));

					/*
					if (hasAlpha) {
						buffer.put((byte) ((rgb >> 24) & 0xFF));
					} else {
						buffer.put((byte) (0xFF));
					}
					*/

					buffer.put((byte) 0xFF);
				}
			}

			buffer.flip();

			return new Texture(image.getWidth(), image.getHeight(), buffer, format, wrapMode, filterMode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static BMFont loadFont(String pathname) {
		return loadFont(pathname, classLoader);
	}

	public static BMFont loadFont(String pathname, ClassLoader classLoader) {
		File file = new File(getResourcePath(pathname, classLoader));

		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			BMFontParser handler = new BMFontParser(file);

			parser.parse(file, handler);

			return handler.getResult();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getResourcePath(String pathname, ClassLoader classLoader) {
		try {
			return classLoader.getResource(pathname).getFile();
		} catch (Exception e) {
			throw new RuntimeException("Error loading resource " + pathname);
		}
	}

}
