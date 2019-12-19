package org.sopiro.game.postProcess;

public class HDR
{
	private HDRShader shader;
	private FrameBuffer fbo;

	private float exposure = 1.0f;

	public HDR()
	{
		fbo = new FrameBuffer();
		shader = new HDRShader();
	}

	public void postProcess(QuadRenderer renderer, int texture)
	{
		fbo.bind();
		shader.start();
		shader.setExposure(exposure);
		renderer.render(texture);
		shader.stop();
		fbo.unbind();
	}

	public int getOutputTexture()
	{
		return fbo.getTexture();
	}

	public void terminate()
	{
		shader.terminate();
		fbo.terminate();
	}
}
