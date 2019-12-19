package org.sopiro.game.models;

import java.nio.*;

public class RawTexture
{
	private byte[] pixels;
	private ByteBuffer buffer;
	private int width;
	private int height;
	
	public RawTexture(byte[] pixels, ByteBuffer buffer, int width, int height)
	{
		this.pixels = pixels;
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

	public byte[] getPixels()
	{
		return pixels;
	}

	public void setPixels(byte[] pixels)
	{
		this.pixels = pixels;
	}

	public ByteBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
}
