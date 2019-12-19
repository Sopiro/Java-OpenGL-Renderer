package org.sopiro.game.entities.light;

import org.joml.*;
import org.sopiro.game.utils.*;

public class Sun extends DirectionalLight
{
	public Sun(Vector3f direction)
	{
		super(direction.normalize(), new Vector3f(0.015f), new Vector3f(2f), new Vector3f(1));
		this.setPosition(Maths.copy(direction).mul(-1)); // Position for shadow mapping
	}
}
