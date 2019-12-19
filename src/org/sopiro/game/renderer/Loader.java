package org.sopiro.game.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import javax.imageio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.sopiro.game.models.*;
import org.sopiro.game.texture.*;

public class Loader
{
	public static final int RGBA = 0xA00000;
	public static final int SRGBA = 0xB00000;
	public static final int NORMAL = 0x000000;
	public static final int MIPMAP = 0x00000A;
	public static final int FLIP_Y = 0x0000A0;
	public static final int REVERSE = 0x000A00;

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();

	private final Texture grayTexture1;
	private final Texture grayTexture2;
	private final Texture grayTexture3;
	private final Texture whiteTexture;
	private final Texture blackTexture;

	public final Material white;
	public final Material gray;
	public final Material grass;
	public final Material fern;
	public final Material test;

	public final Material stall;
	public final Material container;

	public final Material brick;
	public final Material brick2;
	public final Material brick3;
	public final Material barrel;
	public final Material wood;

	public Loader()
	{
		grayTexture1 = loadTexture("gray.png", RGBA);
		grayTexture2 = loadTexture("gray2.png", RGBA);
		grayTexture3 = loadTexture("gray3.png", RGBA);
		whiteTexture = loadTexture("white.png", RGBA);
		blackTexture = loadTexture("black.png", RGBA);

		white = new Material(whiteTexture, blackTexture, 32);
		gray = new Material(grayTexture1, grayTexture1, 32);
		grass = new Material(loadTexture("grassTexture.png"), blackTexture, 32);
		grass.setHasTransparency(true);
		grass.setUseFakeLighting(true);
		fern = new Material(loadTexture("fern.png"), blackTexture, 32);
		fern.setHasTransparency(true);

		test = new Material(loadTexture("test.png"), blackTexture, 32);
		test.setHasTransparency(true);

		container = new Material(loadTexture("/container/diffuseMap.png"), loadTexture("/container/specularMap.png", RGBA), 10);
		stall = new Material(loadTexture("stall.png"), loadTexture("black.png", RGBA), 10);

		brick = new Material(loadTexture("/brickwall/brickwall.png"), grayTexture1, 32, loadTexture("/brickwall/brickwall_normal.png", FLIP_Y | RGBA));
		brick2 = new Material(loadTexture("/brick2/bricks.png"), grayTexture2, 32, loadTexture("/brick2/bricks_normal.png", FLIP_Y | RGBA), loadTexture("/brick2/bricks_disp.png", REVERSE | RGBA));
		brick3 = new Material(loadTexture("/brick3/bricks3.png"), grayTexture1, 32, loadTexture("/brick3/bricks3_normal.png", FLIP_Y | RGBA), loadTexture("/brick3/bricks3_disp.png", RGBA));

		barrel = new Material(loadTexture("/barrel/barrel.png"), grayTexture1, 32, loadTexture("/barrel/barrelNormal.png", FLIP_Y | RGBA));

		wood = new Material(loadTexture("/wood/wood.png"), grayTexture1, 32, loadTexture("/wood/wood_normal.png", FLIP_Y | RGBA), loadTexture("/wood/wood_disp.png", RGBA));
	}

	public RawModel loadVAO(float[] positions, float[] texCoords, float[] normals, float[] tangents, int[] indices)
	{
		int vao = createVAOandBind();
		vaos.add(vao);
		storeDataInAttribList(0, 3, positions);
		storeDataInAttribList(1, 2, texCoords);
		storeDataInAttribList(2, 3, normals);
		storeDataInAttribList(3, 3, tangents);
		storeIndicesBufferToVao(indices);
		unbindVAO();

		return new RawModel(vao, indices.length, true);
	}

	public RawModel loadVAO(float[] positions, float[] texCoords, float[] normals, int[] indices)
	{
		int vao = createVAOandBind();
		vaos.add(vao);
		storeDataInAttribList(0, 3, positions);
		storeDataInAttribList(1, 2, texCoords);
		storeDataInAttribList(2, 3, normals);
		storeIndicesBufferToVao(indices);
		unbindVAO();

		return new RawModel(vao, indices.length);
	}

	public RawModel loadVAO(float[] positions, int dimension)
	{
		int vaoID = createVAOandBind();
		storeDataInAttribList(0, dimension, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / dimension);
	}

	public Texture loadTexture(String path, int flags)
	{
		if (path.endsWith(".png"))
			return loadPNG(path, flags);
		else
		{
			System.err.println("this is not valid image type");
			return null;
		}
	}

	public Texture loadTexture(String path)
	{
		return loadTexture(path, MIPMAP | SRGBA);
	}

	private Texture loadPNG(String path, int flags)
	{
		boolean flipY = false;
		boolean reverse = false;

		if ((flags & 0x0000F0) >> 4 == 0xA)
			flipY = true;
		if ((flags & 0x000F00) >> 8 == 0xA)
			reverse = true;

		RawTexture data = loadTextureData(path, flipY, reverse);
		int format = GL_RGBA;
		if ((flags & 0xF00000) >> 20 == 0xB)
		{
			format = GL_SRGB8_ALPHA8;
		}

		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexImage2D(GL_TEXTURE_2D, 0, format, data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.getBuffer());

		if ((flags & 0x000000F) == 0x0)
		{
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		} else
		{
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0.0f);
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic)
			{
				float amount = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else
			{
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.4f);
			}
		}

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glBindTexture(GL_TEXTURE_2D, 0);

		return new Texture(id);
	}

	public int loadCubeMap(String[] textureFiles)
	{
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);

		for (int i = 0; i < textureFiles.length; i++)
		{
			RawTexture data = loadTextureData(textureFiles[i]);
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_SRGB_ALPHA, data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.getBuffer());
		}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		textures.add(id);

		return id;
	}

	private RawTexture loadTextureData(String path)
	{
		return loadTextureData(path, false);
	}

	private RawTexture loadTextureData(String path, boolean flipY)
	{
		return loadTextureData(path, flipY, false);
	}

	private RawTexture loadTextureData(String path, boolean flipY, boolean reverseColor)
	{
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(new File("./res/textures/" + path));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		int width = image.getWidth();
		int height = image.getHeight();

		byte[] pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		ByteBuffer res = BufferUtils.createByteBuffer(width * height * 4);

		byte reverser1 = (byte) (reverseColor ? 255 : 0);
		byte reverser2 = (byte) (reverseColor ? 1 : -1);
		for (int i = 0; i < pixels.length; i += 4)
		{
			if (flipY)
				pixels[i + 2] = (byte) (255 - pixels[i + 2]);

			res.put((byte) (reverser1 - pixels[i + 3] * reverser2)) // R
			   .put((byte) (reverser1 - pixels[i + 2] * reverser2)) // G
			   .put((byte) (reverser1 - pixels[i + 1] * reverser2)) // B
			   .put((byte) (pixels[i]));							// A
		}

		res.flip();

		return new RawTexture(pixels, res, width, height);
	}

	private int createVAOandBind()
	{
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		return vao;
	}

	private void storeDataInAttribList(int attribIndex, int coordinateSize, float[] data)
	{
		int vbo = glGenBuffers();
		vbos.add(vbo);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glVertexAttribPointer(attribIndex, coordinateSize, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(attribIndex);
		// glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void storeIndicesBufferToVao(int[] indices)
	{
		int ibo = glGenBuffers();
		vbos.add(ibo);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
	}

	private void unbindVAO()
	{
		glBindVertexArray(0);
	}

	public void terminate()
	{
		for (int vao : vaos)
			glDeleteVertexArrays(vao);
		for (int vbo : vbos)
			glDeleteBuffers(vbo);
		for (int tex : textures)
			glDeleteTextures(tex);

	}

	// private FloatBuffer convertBuffer(float[] data)
	// {
	// FloatBuffer res = BufferUtils.createFloatBuffer(data.length);
	// res.put(data);
	// res.flip();
	// return res;
	// }
	//
	// private IntBuffer convertBuffer(int[] data)
	// {
	// IntBuffer res = BufferUtils.createIntBuffer(data.length);
	// res.put(data);
	// res.flip();
	// return res;
	// }
}
