package org.sopiro.game.animation;

import org.lwjgl.assimp.*;
import org.sopiro.game.models.RawModel;
import org.sopiro.game.renderer.Loader;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class AnimationLoader
{
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
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        System.out.println(scene.mNumAnimations());

        final int vertexSize = 11;
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

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, vertexSize * floatSize, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, vertexSize * floatSize, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, vertexSize * floatSize, 20);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, vertexSize * floatSize, 32);

        int ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);

        return new AnimatedModel(vao, indices.length, loader.runner.getDiffuseMap().getID());
    }
}
