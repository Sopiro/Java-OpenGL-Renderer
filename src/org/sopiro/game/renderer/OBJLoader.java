package org.sopiro.game.renderer;

import java.io.*;
import java.util.*;

import org.joml.*;
import org.sopiro.game.models.*;
import org.sopiro.game.utils.*;

public class OBJLoader
{
   public static RawModel load(Loader loader, String path)
   {
      Scanner s = null;
      try
      {
         s = new Scanner(new File("./res/models/" + path + ".obj"));
      } catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }

      String line = null;

      List<Vector3f> positions = new ArrayList<Vector3f>();
      List<Vector2f> texCoords = new ArrayList<Vector2f>();
      List<Vector3f> normals = new ArrayList<Vector3f>();
      List<Vector3i> vertexData = new ArrayList<Vector3i>();

      float[] positionArray = null;
      float[] textureArray = null;
      float[] normalArray = null;
      float[] tangentArray = null;
      int[] indexArray = null;

      String[] tokens = null;

      while (s.hasNext())
      {
         line = s.nextLine();
         tokens = line.split(" ");

         if (tokens[0].equals("v"))
         {
            positions.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
         } else if (tokens[0].equals("vt"))
         {
            texCoords.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
         } else if (tokens[0].equals("vn"))
         {
            normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
         } else if (tokens[0].equals("f"))
         {
        	 for(int i = 0; i < 3; i++)
        		 vertexData.add(splitVertexData(tokens[i + 1]));
         }
      }

      s.close();

      positionArray = new float[positions.size() * 3];
      textureArray = new float[positions.size() * 2];
      normalArray = new float[positions.size() * 3];
      tangentArray = new float[positions.size() * 3];
      indexArray = new int[vertexData.size()];

      int[] vertPointer = new int[3];
      Vector3f[] faceVert = new Vector3f[3];
      Vector2f[] faceTexc = new Vector2f[3];

      for (int i = 0; i < vertexData.size(); i++)
      {
         Vector3i vertex = vertexData.get(i);

         int index = vertex.x;
         
         indexArray[i] = index;

         vertPointer[i % 3] = index;
         
         Vector3f position = positions.get(vertex.x);
         Vector2f texCoord = texCoords.get(vertex.y);
         Vector3f normal = normals.get(vertex.z);

         positionArray[index * 3] = position.x;
         positionArray[index * 3 + 1] = position.y;
         positionArray[index * 3 + 2] = position.z;

         if (texCoords.size() != 0)
         {
            textureArray[index * 2] = texCoord.x;
            textureArray[index * 2 + 1] = 1 - texCoord.y;
         }

         normalArray[index * 3] = normal.x;
         normalArray[index * 3 + 1] = normal.y;
         normalArray[index * 3 + 2] = normal.z;

         faceVert[i % 3] = position;
         faceTexc[i % 3] = texCoord;

         if (i % 3 == 2)
         {
            Vector3f tangent = calcTangent(faceVert, faceTexc);
            
            for (int j = 0; j < 3; j++)
            {
               tangentArray[vertPointer[j] * 3] = tangent.x;
               tangentArray[vertPointer[j] * 3 + 1] = tangent.y;
               tangentArray[vertPointer[j] * 3 + 2] = tangent.z;
            }
         }
      }

      return loader.loadVAO(positionArray, textureArray, normalArray, tangentArray, indexArray);
   }

	private static Vector3i splitVertexData(String data)
	{
		String[] vertexData = data.split("/");
		Vector3i res = new Vector3i();
		res.x = Integer.parseInt(vertexData[0]) - 1;
		res.y = vertexData[1].equals("") ? 1 : Integer.parseInt(vertexData[1]) - 1;
		res.z = Integer.parseInt(vertexData[2]) - 1;

		return res;
	}

   public static Vector3f calcTangent(Vector3f[] faceVert, Vector2f[] faceTexc)
   {
      Vector3f deltaPos1 = Maths.sub(faceVert[1], faceVert[0]);
      Vector3f deltaPos2 = Maths.sub(faceVert[2], faceVert[0]);

      Vector2f deltaUV1 = Maths.sub(faceTexc[1], faceTexc[0]);
      Vector2f deltaUV2 = Maths.sub(faceTexc[2], faceTexc[0]);

      float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

      Vector3f tangent = Maths.sub(deltaPos1.mul(deltaUV2.y), deltaPos2.mul(deltaUV1.y)).mul(f);

      return tangent.normalize();
   }
}