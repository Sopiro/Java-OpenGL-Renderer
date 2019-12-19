package org.sopiro.game.postProcess;

import org.sopiro.game.renderer.*;

public class PostProcesser
{
	private float gamma = 2.2f;
	
	private QuadRenderer renderer;
	
	private HDR hdr;
	private GaussianBlur blur;
	private GammaCorrection gammaCorrection;
	
	public PostProcesser(Loader loader)
	{
		renderer = new QuadRenderer(loader);

		hdr = new HDR();
		blur = new GaussianBlur();
		gammaCorrection = new GammaCorrection(gamma);
	}
	
	public void postProcess(int texture)
	{
		int output;
		hdr.postProcess(renderer, texture);
		output = hdr.getOutputTexture();
//		blur.postProcess(renderer, output);
//		output = blur.getOutputTexture();
		
		gammaCorrection.postProcess(renderer, output);
	}
	
	public void terminate()
	{
		hdr.terminate();
		gammaCorrection.terminate();
	}
}
