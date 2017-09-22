package edu.fiu.cate.breader.tools;

import math2.Vector;

public class RollingImageFilter {
	private int depth = 1;
	private int imgLenght = 0;
	private java.util.ArrayList<float[]> images;
	
	public RollingImageFilter(int depth, int imgLenght){
		this.depth = depth;
		this.imgLenght = imgLenght;
		this.images = new java.util.ArrayList<float[]>(depth-1);
		for(int i=0; i<depth-1; i++){
			this.images.add(new float[this.imgLenght]);
		}
	}
	
	public static RollingImageFilter getRollingImageFilter(int depth, int imgLenght){
		return new RollingImageFilter(depth, imgLenght);
	}
	
	public float[] filter(float[] img){
		float[] out = new float[this.imgLenght];
		out = Vector.add(out, img);
		for(int i=0; i<depth-1; i++){
			out = Vector.add(out, images.get(i));
		}
		this.images.remove(0);
		this.images.add(img);
		out = Vector.scalarMult(out, 1f/(float)depth);
		return out;
	}
}
