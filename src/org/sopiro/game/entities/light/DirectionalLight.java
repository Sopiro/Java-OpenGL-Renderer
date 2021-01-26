package org.sopiro.game.entities.light;

import org.joml.*;

public class DirectionalLight extends Light
{
    private Vector3f direction;

    public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular)
    {
        super(new Vector3f(0), ambient, diffuse, specular);
        this.direction = direction;
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    public Vector3f getDirection()
    {
        return direction;
    }
}
