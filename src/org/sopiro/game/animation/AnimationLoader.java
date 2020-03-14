package org.sopiro.game.animation;

import org.lwjgl.assimp.*;
import org.sopiro.game.renderer.Loader;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class AnimationLoader
{
    private static HashMap<String, Integer> boneMap;

    public static AnimatedModel load(Loader loader, String fileName)
    {
        AIScene scene = Assimp.aiImportFile("./res/models/" + fileName,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs |
                        Assimp.aiProcess_CalcTangentSpace
        );

        assert scene != null;
        assert scene.mNumMeshes() == 1;
        assert scene.mNumAnimations() > 0;
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        final int vertexSize = 17;
        final int floatSize = 4;

        float[] vertices = new float[mesh.mNumVertices() * vertexSize];

        int i = 0;
        for (int v = 0; v < mesh.mNumVertices(); v++)
        {
            AIVector3D position = mesh.mVertices().get(v);
            AIVector3D tex = mesh.mTextureCoords(0).get(v);
            AIVector3D normal = mesh.mNormals().get(v);
            AIVector3D tangent = mesh.mTangents().get(v);

            vertices[i++] = position.x();
            vertices[i++] = position.y();
            vertices[i++] = position.z();

            vertices[i++] = tex.x();
            vertices[i++] = tex.y();

            vertices[i++] = normal.x();
            vertices[i++] = normal.y();
            vertices[i++] = normal.z();

            vertices[i++] = tangent.x();
            vertices[i++] = tangent.y();
            vertices[i++] = tangent.z();

            i += 6;
        }

        int[] indices = new int[mesh.mNumFaces() * 3];

        i = 0;
        for (int f = 0; f < mesh.mNumFaces(); f++)
        {
            AIFace face = mesh.mFaces().get(f);

            indices[i++] = (face.mIndices().get(0));
            indices[i++] = (face.mIndices().get(1));
            indices[i++] = (face.mIndices().get(2));
        }

        final int offset = 11;

        boneMap = new HashMap<>();

        for (int b = 0; b < mesh.mNumBones(); b++)
        {
            AIBone bone = AIBone.create(mesh.mBones().get(b));
            boneMap.put(bone.mName().dataString(), b);

            for (int w = 0; w < bone.mNumWeights(); w++)
            {
                AIVertexWeight vw = bone.mWeights().get(w);

                int access = vw.mVertexId() * vertexSize + offset;

                for (int j = 0; j < 3; j++)
                {
                    if (vertices[access] == 0 && vertices[access + 3] == 0)
                    {
                        vertices[access] = b;
                        vertices[access + 3] = vw.mWeight();
                        break;
                    } else
                    {
                        access++;
                    }
                }
            }
        }

        for (int j = 1100 * vertexSize; j < 1200 * vertexSize; j++)
        {
            System.out.println(vertices[j]);
            if ((j + 1) % vertexSize == 0)
                System.out.println();
        }

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, vertexSize * floatSize, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, vertexSize * floatSize, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, vertexSize * floatSize, 20);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, vertexSize * floatSize, 32);
        glVertexAttribPointer(4, 3, GL_FLOAT, false, vertexSize * floatSize, 44);
        glVertexAttribPointer(5, 3, GL_FLOAT, false, vertexSize * floatSize, 56);

        int ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);

        return new AnimatedModel(vao, indices.length, loader.runner.getDiffuseMap().getID());
    }
}
