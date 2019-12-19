package org.sopiro.game.utils;

import java.lang.Math;

import org.joml.*;
import org.sopiro.game.entities.*;

public class Maths
{
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.translate(translation.x, translation.y, 0);
		matrix.scaleXY(scale.x, scale.y);

		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.translate(translation);
		rotation = new Vector3f(rotation).mul((float) (Math.PI / 180));
		matrix.rotateXYZ(rotation);
		matrix.scale(scale);

		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera)
	{
		Matrix4f matrix = new Matrix4f();
		Vector3f negative = new Vector3f(camera.getRotation()).mul(-1);
		matrix.rotateXYZ(negative);
		negative = new Vector3f(camera.getPosition()).mul(-1);
		matrix.translate(negative);

		// matrix = new Matrix4f().lookAt(new Vector3f(-300, 300, 300), new
		// Vector3f(0), new Vector3f(0, 1, 0));
//		matrix = new Matrix4f().lookAlong(new Vector3f(300, -300, -300), new Vector3f(0,1,0));
//		matrix.translate(negative);
		
//		matrix = new Matrix4f().lookAt(Maths.sum(camera.getPosition(), new Vector3f(-300, 300, 300).mul(-1)), camera.getPosition(), new Vector3f(0, 1, 0));
		
		return matrix;
	}
	
	public static Vector3f mul(Vector3f a, float b)
	{
		return copy(a).mul(b);
	}

	public static Vector3f sum(Vector3f a, Vector3f b)
	{
		return new Vector3f(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
	}
	
	public static Vector2f sum(Vector2f a, Vector2f b)
	{
		return new Vector2f(a.x() + b.x(), a.y() + b.y());
	}
	
	public static Vector3f sub(Vector3f left, Vector3f right)
	{
		return new Vector3f(left.x() - right.x(), left.y() - right.y(), left.z() - right.z());
	}
	
	public static Vector2f sub(Vector2f left, Vector2f right)
	{
		return new Vector2f(left.x() - right.x(), left.y() - right.y());
	}

	public static Matrix4f mul(Matrix4f left, Matrix4f right)
	{
		return new Matrix4f(left).mul(new Matrix4f(right));
	}

	public static Vector3f copy(Vector3f c)
	{
		return new Vector3f(c);
	}

	public static Vector4f mul(Matrix4f left, Vector4f right)
	{
		return right.mul(left);
	}

	public static Vector3f mul(Matrix4f left, Vector3f right)
	{
		return convert(mul(left, convert(right)));
	}

	public static Matrix4f invert(Matrix4f original)
	{
		return new Matrix4f(original).invert();
	}

	public static Vector3f convert(Vector4f v)
	{
		return new Vector3f(v.x(), v.y(), v.z());
	}

	public static Vector4f convert(Vector3f v)
	{
		return new Vector4f(v.x(), v.y(), v.z(), 1);
	}
	
	public static void mul(float[] array, float s)
	{
		for(int i = 0; i < array.length; i++)
			array[i] *= s;
	}
}
