package edu.fiu.cate.joda;

import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;

public class TreSegmentation {

	public static void main(String[] args) {
		
		
		byte[][][] tree = ImageManipulation.loadImage("/home/harold/Pictures/tree2.jpg");
		new IViewer("original", ImageManipulation.getBufferedImage(tree));
		new IViewer("green", ImageManipulation.getGrayBufferedImage(tree[1]));
		
		double[][][] normC = ITools.getNormColors(ITools.byte2double(tree));
		
		new IViewer("greenN", ImageManipulation.getGrayBufferedImage(ITools.normilize(normC[1])));

	}

}
