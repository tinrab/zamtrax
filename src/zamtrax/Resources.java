package zamtrax;

import org.lwjgl.BufferUtils;
import zamtrax.resources.Mesh;
import zamtrax.resources.PlyLoader;
import zamtrax.resources.Shader;
import zamtrax.resources.bmfont.BMFont;
import zamtrax.resources.bmfont.BMFontParser;
import zamtrax.resources.Texture;

import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Resources {

	private static ClassLoader classLoader;

	static void init(ClassLoader classLoader) {
		Resources.classLoader = classLoader;
	}

	public static String loadText(String pathname) {
		return loadText(pathname, classLoader);
	}

	public static String loadText(String pathname, ClassLoader classLoader) {
		//pathname = getResourcePath(pathname, classLoader);

		try (InputStream fs = classLoader.getResourceAsStream(pathname)) {
			StringBuilder sb = new StringBuilder();
			int ch;

			while ((ch = fs.read()) != -1) {
				sb.append((char) ch);
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	}

	public static Mesh loadModel(String pathname) {
		if (pathname.endsWith(PlyLoader.getFileType())) {
			return PlyLoader.load(pathname);
		}

		throw new RuntimeException("unsupported file type");
	}

	public static Texture loadTexture(String pathname, Texture.Format format, Texture.WrapMode wrapMode, Texture.FilterMode filterMode) {
		try {
			BufferedImage image = ImageIO.read(classLoader.getResourceAsStream(pathname));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * 4);
			boolean hasAlpha = image.getColorModel().hasAlpha();

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int rgb = pixels[y * image.getWidth() + x];

					buffer.put((byte) ((rgb >> 16) & 0xFF));
					buffer.put((byte) ((rgb >> 8) & 0xFF));
					buffer.put((byte) ((rgb) & 0xFF));

					if (hasAlpha) {
						buffer.put((byte) ((rgb >> 24) & 0xFF));
					} else {
						buffer.put((byte) (0xFF));
					}
				}
			}

			buffer.flip();

			return new Texture(image.getWidth(), image.getHeight(), buffer, format, wrapMode, filterMode);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	}

	public static BMFont loadFont(String pathname) {
		return loadFont(pathname, classLoader);
	}

	public static BMFont loadFont(String pathname, ClassLoader classLoader) {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			BMFontParser handler = new BMFontParser(pathname.substring(0, pathname.indexOf('/')));

			parser.parse(classLoader.getResourceAsStream(pathname), handler);

			return handler.getResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	}

}
