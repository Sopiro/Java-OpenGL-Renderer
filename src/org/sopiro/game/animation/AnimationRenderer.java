package org.sopiro.game.animation;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30.*;

public class AnimationRenderer
{
    private AnimationShader shader;

    public AnimationRenderer(AnimationShader animationShader, Matrix4f projectionMatrix)
    {
        this.shader = animationShader;
        setProjectionMatrix(projectionMatrix);
    }

    public void render(AnimatedModel model)
    {
        shader.setBoneTransforms(model.getBones());

        glBindVertexArray(model.getVaoID());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTextureID());

        glDrawElements(GL_TRIANGLES, model.getCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix)
    {
        shader.start();
        shader.setProjectionMatrix(projectionMatrix);
        shader.stop();
    }
}
