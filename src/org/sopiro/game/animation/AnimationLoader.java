package org.sopiro.game.animation;

import org.lwjgl.assimp.*;
import org.sopiro.game.renderer.Loader;
import org.sopiro.game.texture.Texture;
import org.sopiro.game.utils.Maths;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class AnimationLoader
{
    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();

    public static AnimatedModel load(Loader loader, String fileName, Texture texture)
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

        /*
            position    3
            tex         2
            normal      3
            tangent     3
            bone_id     3
            weights     3
                        17
        */
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

        for (int b = 0; b < mesh.mNumBones(); b++)
        {
            AIBone bone = AIBone.create(mesh.mBones().get(b));

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

//        for (int j = 1100 * vertexSize; j < 1101 * vertexSize; j++)
//        {
//            System.out.println(vertices[j]);
//            if ((j + 1) % vertexSize == 0)
//                System.out.println();
//        }

        Bone[] bones = new Bone[mesh.mNumBones()];

        for (int b = 0; b < mesh.mNumBones(); b++)
        {
            AIBone bone = AIBone.create(mesh.mBones().get(b));
            bones[b] = new Bone(bone.mName().dataString(), Maths.convertMatrix(bone.mOffsetMatrix()));
        }

        AIAnimation[] animations = new AIAnimation[scene.mNumAnimations()];
        for (int a = 0; a < animations.length; a++)
            animations[a] = AIAnimation.create(scene.mAnimations().get(a));

        int vao = glGenVertexArrays();
        vaos.add(vao);
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        vbos.add(vbo);

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

        AnimatedModel model = new AnimatedModel(vao, indices.length, texture.getID());
        model.globalInverseTransform = Maths.convertMatrix(scene.mRootNode().mTransformation()).invert();
        model.bones = bones;
        model.animations = animations;
        model.root = scene.mRootNode();

        return model;
    }

    public void terminate()
    {
        for (int vao : vaos)
            glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            glDeleteBuffers(vbo);
    }
}
