package org.sopiro.game.postProcess;

public class GammaCorrection
{
	private GammaCorrectionShader shader;
	
	private float gamma;
	
	public GammaCorrection(float gamma)
	{
		shader = new GammaCorrectionShader();
		setGamma(gamma);
	}
	
	public void postProcess(QuadRenderer renderer, int texture)
	{
		shader.start();
		renderer.render(texture);
		shader.stop();
	}
	
	public void setGamma(float gamma)
	{
		this.gamma = gamma;
		shader.start();
		shader.setGamma(gamma);
		shader.stop();
	}
	
	public float getGamma()
	{
		return gamma;
	}
	
	public void terminate()
	{
		shader.terminate();
	}
}
