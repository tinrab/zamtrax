package zamtrax.resources;

import zamtrax.Vector3;
import zamtrax.Vertex;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * A mesh file loader for *.ply files.
 *
 * @author Kenton McHenry
 */

public class PlyLoader {

	private enum plyProperty {x, y, z, n, nx, ny, nz, s, t, red, green, blue, vertex1, vertex2, vertex3, unsupported}

	private enum plyPropertyType {uint8, int32, float32, uchar}

	public static String getFileType() {
		return "ply";
	}

	public static Mesh load(String pathname) {
		List<Vertex> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		Vector<plyProperty> vertex_properties = new Vector<plyProperty>();
		Vector<plyPropertyType> vertex_types = new Vector<plyPropertyType>();
		Vector<plyProperty> face_properties = new Vector<plyProperty>();
		Vector<plyPropertyType> face_types = new Vector<plyPropertyType>();

		try {
			InputStream is1 = new FileInputStream(pathname);
			InputStream is2 = new FileInputStream(pathname);

			Scanner ins = new Scanner(is1);

			String tmps;

			//Read in header
			String format = new String();
			int vertex_count = 0;
			int face_count = 0;

			tmps = ins.next();

			while (true) {
				if (tmps.equals("format")) {
					format = ins.next();
					ins.next();   //Read in version
					tmps = ins.next();
				} else if (tmps.equals("element")) {
					tmps = ins.next();

					if (tmps.equals("vertex")) {
						vertex_count = ins.nextInt();
						tmps = ins.next();

						while (tmps.equals("property")) {
							tmps = ins.next();  //Read in type

							if (tmps.equals("list")) {  //Assume it's a triangle since that's all we support for now!
								System.out.println("Error: loadPly -> vertex lists are not supported!");
								System.exit(1);
							} else {
								vertex_types.add(plyPropertyType_valueOf(tmps));
								tmps = ins.next();
								vertex_properties.add(plyProperty_valueOf(tmps));
							}

							tmps = ins.next();
						}
					} else if (tmps.equals("face")) {
						face_count = ins.nextInt();
						tmps = ins.next();

						while (tmps.equals("property")) {
							tmps = ins.next();  //Read in type

							if (tmps.equals("list")) {  //Assume it's a triangle since that's all we support for now!
								tmps = ins.next();      //Read in type for list length
								face_types.add(plyPropertyType_valueOf(tmps));
								face_properties.add(plyProperty.n);

								tmps = ins.next();      //Read in type for list
								face_types.add(plyPropertyType_valueOf(tmps));
								face_properties.add(plyProperty.vertex1);
								face_types.add(plyPropertyType_valueOf(tmps));
								face_properties.add(plyProperty.vertex2);
								face_types.add(plyPropertyType_valueOf(tmps));
								face_properties.add(plyProperty.vertex3);

								ins.next();             //Read in name of list
							} else {
								face_types.add(plyPropertyType_valueOf(tmps));
								tmps = ins.next();
								face_properties.add(plyProperty_valueOf(tmps));
							}

							tmps = ins.next();
						}
					}
				} else if (tmps.equals("end_header")) {
					break;
				} else {
					tmps = ins.next();
				}
			}

			//Read in data
			if (format.equals("ascii")) {
				for (int i = 0; i < vertex_count; i++) {
					Vertex vertex = new Vertex();

					for (int j = 0; j < vertex_properties.size(); j++) {
						if (vertex_properties.get(j) == plyProperty.x) {
							vertex.setX(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.y) {
							vertex.setY(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.z) {
							vertex.setZ(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.nx) {
							vertex.setNX(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.ny) {
							vertex.setNY(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.nz) {
							vertex.setNZ(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.red) {
							vertex.setRed(ins.nextFloat() / 255.0f);
						} else if (vertex_properties.get(j) == plyProperty.green) {
							vertex.setGreen(ins.nextFloat() / 255.0f);
						} else if (vertex_properties.get(j) == plyProperty.blue) {
							vertex.setBlue(ins.nextFloat() / 255.0f);
						} else if (vertex_properties.get(j) == plyProperty.s) {
							vertex.setU(ins.nextFloat());
						} else if (vertex_properties.get(j) == plyProperty.t) {
							vertex.setV(ins.nextFloat());
						} else {     //PLY_UNSUPORTED!
							ins.nextFloat();
						}
					}

					vertices.add(vertex);
				}

				for (int i = 0; i < face_count; i++) {
					int v1 = 0;
					int v2 = 0;
					int v3 = 0;

					for (int j = 0; j < face_properties.size(); j++) {
						if (face_properties.get(j) == plyProperty.n) {
							if (ins.nextInt() != 3) {
								System.out.println("Error: faces must contain 3 points!");
								System.exit(1);
							}
						} else if (face_properties.get(j) == plyProperty.vertex1) {
							v1 = ins.nextInt();
						} else if (face_properties.get(j) == plyProperty.vertex2) {
							v2 = ins.nextInt();
						} else if (face_properties.get(j) == plyProperty.vertex3) {
							v3 = ins.nextInt();
						} else {     //PLY_UNSUPORTED!
							ins.nextFloat();
						}
					}

					indices.add(v1);
					indices.add(v2);
					indices.add(v3);
				}
			}

			ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		List<AttributePointer> attributePointers = new ArrayList<>();
		int i = 0;

		for (plyProperty prop : vertex_properties) {
			if (prop == plyProperty.x) {
				attributePointers.add(new AttributePointer(AttributeType.POSITION, i++));
			} else if (prop == plyProperty.nx) {
				attributePointers.add(new AttributePointer(AttributeType.NORMAL, i++));
			} else if (prop == plyProperty.s) {
				attributePointers.add(new AttributePointer(AttributeType.UV, i++));
			} else if (prop == plyProperty.red) {
				attributePointers.add(new AttributePointer(AttributeType.COLOR, i++));
			}
		}

		return new Mesh.Builder()
				.setBindingInfo(new BindingInfo(attributePointers))
				.setVertices(vertices)
				.setIndices(indices)
				.build();
	}

	/**
	 * Convert a string to a plyProperty
	 *
	 * @param s the string to convert
	 * @return the resulting plyProperty
	 */
	public static plyProperty plyProperty_valueOf(String s) {
		if (s.equals("x")) {
			return plyProperty.x;
		} else if (s.equals("y")) {
			return plyProperty.y;
		} else if (s.equals("z")) {
			return plyProperty.z;
		} else if (s.equals("vertex1")) {
			return plyProperty.vertex1;
		} else if (s.equals("vertex2")) {
			return plyProperty.vertex2;
		} else if (s.equals("vertex3")) {
			return plyProperty.vertex3;
		} else if (s.equals("nx")) {
			return plyProperty.nx;
		} else if (s.equals("ny")) {
			return plyProperty.ny;
		} else if (s.equals("nz")) {
			return plyProperty.nz;
		} else if (s.equals("red")) {
			return plyProperty.red;
		} else if (s.equals("green")) {
			return plyProperty.green;
		} else if (s.equals("blue")) {
			return plyProperty.blue;
		} else if (s.equals("s")) {
			return plyProperty.s;
		} else if (s.equals("t")) {
			return plyProperty.t;
		} else {
			return plyProperty.unsupported;
		}
	}

	/**
	 * Convert a string to a plyPropertyType
	 *
	 * @param s the string to convert
	 * @return the resulting plyPropertyType
	 */
	public static plyPropertyType plyPropertyType_valueOf(String s) {
		if (s.equals("uint8")) {
			return plyPropertyType.uint8;
		} else if (s.equals("int32")) {
			return plyPropertyType.int32;
		} else if (s.equals("float32")) {
			return plyPropertyType.float32;
		} else if (s.equals("uchar")) {
			return plyPropertyType.uchar;
		} else {
			return plyPropertyType.float32;
		}
	}

}
