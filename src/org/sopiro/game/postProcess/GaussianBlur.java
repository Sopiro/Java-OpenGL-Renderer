package org.sopiro.game.postProcess;

public class GaussianBlur
{
	private GaussianBlurShader shader;
	private FrameBuffer fbo;

	public GaussianBlur()
	{
		fbo = new FrameBuffer();
		shader = new GaussianBlurShader();
	}

	public void postProcess(QuadRenderer renderer, int texture)
	{
		fbo.bind();
		shader.start();
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
