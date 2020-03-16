package org.sopiro.game.animation;

import org.joml.Matrix4f;
import org.sopiro.game.entities.Camera;
import org.sopiro.game.renderer.shader.ShaderProgram;
import org.sopiro.game.utils.Maths;

public class AnimationShader extends ShaderProgram
{
    private final int MAX_BONES = 50;

    private final static String vsPath = "./res/shaders/animation.vs";
    private final static String fsPath = "./res/shaders/animation.fs";

    // Uniform locations
    private int transformationMatrix;
    private int viewMatrix;
    private int projectionMatrix;
    private int[] boneTransforms;

    public AnimationShader()
    {
        super(vsPath, fsPath);
    }

    @Override
    protected void getAllUniformLocation()
    {
        transformationMatrix = super.getUniformLocation("transformationMatrix");
        viewMatrix = super.getUniformLocation("viewMatrix");
        projectionMatrix = super.getUniformLocation("projectionMatrix");

        boneTransforms = new int[MAX_BONES];
        for (int i = 0; i < MAX_BONES; i++)
            boneTransforms[i] = super.getUniformLocation("bones[" + i + "]");
    }

    public void setTransformationMatrix(Matrix4f matrix)
    {
        super.setMatrix(this.transformationMatrix, matrix);
    }

    public void setViewMatrix(Camera camera)
    {
        super.setMatrix(viewMatrix, Maths.createViewMatrix(camera));
    }

    public void setProjectionMatrix(Matrix4f matrix)
    {
        super.setMatrix(this.projectionMatrix, matrix);
    }

    public void setBoneTransforms(Bone[] bones)
    {
        for (int i = 0; i < bones.length; i++)
            super.setMatrix(this.boneTransforms[i], bones[i].getTransformation());
    }
}
