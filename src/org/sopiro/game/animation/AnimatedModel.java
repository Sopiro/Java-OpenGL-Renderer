package org.sopiro.game.animation;

import org.sopiro.game.texture.Material;

public class AnimatedModel
{
    private final int vaoID;
    private int count;
    private int textureID;

    public AnimatedModel(int vaoID, int count, int textureID)
    {
        this.vaoID = vaoID;
        this.count = count;
        this.textureID = textureID;
    }

    public int getVaoID()
    {
        return vaoID;
    }

    public int getCount()
    {
        return count;
    }

    public int getTextureID()
    {
        return textureID;
    }
}
